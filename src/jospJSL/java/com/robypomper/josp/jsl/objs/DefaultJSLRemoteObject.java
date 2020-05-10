package com.robypomper.josp.jsl.objs;

import com.robypomper.communication.client.Client;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.structure.DefaultJSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.jsl.objs.structure.JSLState;
import com.robypomper.josp.jsl.systems.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_ServiceRequests;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Default implementation of {@link JSLRemoteObject} interface.
 */
public class DefaultJSLRemoteObject implements JSLRemoteObject {

    // Internal vars

    private final JSLServiceInfo srvInfo;
    private final String objId;
    private final List<JSLLocalClient> localConnections = new ArrayList<>();
    private JSLRoot root = null;
    private Date lastStructureUpdate = null;
    private String name = null;
    private String ownerId = null;
    private String jodVersion = null;


    // Constructor

    /**
     * Default constructor that set reference to current {@link JSLServiceInfo},
     * represented object's id and the first client connected to represented JOD
     * object.
     *
     * @param srvInfo     current service info.
     * @param objId       represented object's id.
     * @param localClient the client connected with JOD object.
     */
    public DefaultJSLRemoteObject(JSLServiceInfo srvInfo, String objId, JSLLocalClient localClient) {
        this.srvInfo = srvInfo;
        this.objId = objId;
        addLocalClient(localClient);
    }


    // Object's info

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return objId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name != null ? name : "N/A";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLRoot getStructure() {
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOwnerId() {
        return ownerId != null ? ownerId : "N/A";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJODVersion() {
        return jodVersion != null ? jodVersion : "N/A";
    }

    /**
     * Set the represent object's name and trigger corresponding event.
     *
     * @param name the object's name.
     */
    private void setName(String name) {
        this.name = name;
        // ToDo: trigger onNameUpdated() event
    }

    /**
     * Set the represent object's owner id and trigger corresponding event.
     *
     * @param ownerId the object's owner id.
     */
    private void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
        // ToDo: trigger onOwnerIdUpdated() event
    }

    /**
     * Set the represent object's JOD version and trigger corresponding event.
     *
     * @param jodVersion the object's JOD version.
     */
    private void setJODVersion(String jodVersion) {
        this.jodVersion = jodVersion;
        // ToDo: trigger onJODVersionUpdated() event
    }


    // Object's communication

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return getConnectedClient() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLocalClient(JSLLocalClient localClient) {
//        // Connected if called from constructor
//        // Disconnected if called from JSLObjMngr::addNewConnection()
//        localConnections.add(localClient);

        boolean wasConnected = isConnected();

        if (!isConnected() && !localClient.isConnected())
            try {
                localClient.connect();
            } catch (Client.ConnectionException ignore) {}

        else if (isConnected() && localClient.isConnected())
            localClient.disconnect();

        localConnections.add(localClient);
        localClient.setRemoteObject(this);

        if (!wasConnected)
            try {
                requestObjectInfo();
            } catch (ObjectNotConnected e) {
                System.out.println("ERR: can't send 'objectStructureRequest' because object is not connected");
            }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceLocalClient(JSLLocalClient oldClient, JSLLocalClient newClient) {
        localConnections.remove(oldClient);
        localConnections.add(newClient);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JSLLocalClient> getLocalClients() {
        return localConnections;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLLocalClient getConnectedClient() {
        //synchronized (localConnections) {
        for (JSLLocalClient client : localConnections) {
            if (client.isConnected())
                return client;
        }
        return null;
        //}
    }

    /**
     * Util method that send given message to represented object.
     * <p>
     * This method use the connected client, in the object's clients list, and
     * use it to send the message.
     *
     * @param msg the message to send to the represented object.
     */
    private void send(String msg) throws ObjectNotConnected {
        JSLLocalClient cli = getConnectedClient();
        if (cli == null)
            throw new ObjectNotConnected(this);

        try {
            cli.sendData(msg);
        } catch (Client.ServerNotConnectedException e) {
            throw new ObjectNotConnected(this, e);
        }
    }

    /**
     * Send ObjectInfo request to represented object.
     */
    private void requestObjectInfo() throws ObjectNotConnected {
        send(JOSPProtocol_ServiceRequests.createObjectInfoRequest(srvInfo.getFullId()));
    }


    // Process object's data

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean processUpdate(String msg) {
        // parse received data (here or in prev methods)
        JOSPProtocol.StatusUpd upd = JOSPProtocol.fromMsgToUpd(msg);

        // search destination object/components
        JSLComponentPath compPath = new DefaultJSLComponentPath(upd.getComponentPath());
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(root, compPath);
        if (comp == null) {
            System.out.println(String.format("WAR: received status update for unknown component '%s'", upd.getComponentPath()));
            return false;
        }
        if (!(comp instanceof JSLState)) {
            System.out.println(String.format("WAR: received status update for not status component '%s'", upd.getComponentPath()));
            return false;
        }
        JSLState stateComp = (JSLState) comp;

        // set object/component's update
        return stateComp.updateStatus(upd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean processServiceRequestResponse(String msg) {
        // Object info request's response
        if (JOSPProtocol_ServiceRequests.isObjectInfoRequestResponse(msg)) {
            try {
                setName(JOSPProtocol_ServiceRequests.extractObjectInfoObjNameFromResponse(msg));
                setOwnerId(JOSPProtocol_ServiceRequests.extractObjectInfoOwnerIdFromResponse(msg));
                setJODVersion(JOSPProtocol_ServiceRequests.extractObjectInfoJodVersionFromResponse(msg));
                return true;

            } catch (JOSPProtocol.ParsingException e) {
                System.out.println(String.format("ERR: can't parse ObjectInfoResponse because %s", e.getMessage()));
                return false;
            }
        }

        return false;
    }

}
