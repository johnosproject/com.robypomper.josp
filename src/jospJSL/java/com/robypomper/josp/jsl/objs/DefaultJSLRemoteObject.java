package com.robypomper.josp.jsl.objs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robypomper.communication.client.Client;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.jsl.objs.structure.JSLRoot_Jackson;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_CloudRequests;
import com.robypomper.josp.protocol.JOSPProtocol_ServiceRequests;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Default implementation of {@link JSLRemoteObject} interface.
 */
public class DefaultJSLRemoteObject implements JSLRemoteObject {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JSLServiceInfo srvInfo;
    private final String objId;
    private final JSLCommunication communication;
    private final List<JSLLocalClient> localConnections = new ArrayList<>();
    private JSLRoot root = null;
    private Date lastStructureUpdate = null;
    private String name = null;
    private String ownerId = null;
    private String jodVersion = null;
    private boolean isCloudConnected = false;


    // Constructor

    /**
     * Default constructor that set reference to current {@link JSLServiceInfo},
     * represented object's id and the first client connected to represented JOD
     * object.
     *
     * @param srvInfo       current service info.
     * @param objId         represented object's id.
     * @param communication instance of the {@link JSLCommunication}.
     */
    public DefaultJSLRemoteObject(JSLServiceInfo srvInfo, String objId, JSLCommunication communication) {
        this.srvInfo = srvInfo;
        this.objId = objId;
        this.communication = communication;

        log.info(Mrk_JSL.JSL_OBJS_SUB, String.format("Initialized JSLRemoteObject '%s' (to: cloud) on '%s' service", objId, srvInfo.getSrvId()));
    }

    /**
     * Default constructor that set reference to current {@link JSLServiceInfo},
     * represented object's id and the first client connected to represented JOD
     * object.
     *
     * @param srvInfo       current service info.
     * @param objId         represented object's id.
     * @param localClient   the client connected with JOD object.
     * @param communication instance of the {@link JSLCommunication}.
     */
    public DefaultJSLRemoteObject(JSLServiceInfo srvInfo, String objId, JSLLocalClient localClient, JSLCommunication communication) {
        this.srvInfo = srvInfo;
        this.objId = objId;
        this.communication = communication;
        addLocalClient(localClient);

        log.info(Mrk_JSL.JSL_OBJS_SUB, String.format("Initialized JSLRemoteObject '%s' (to: %s:%d) on '%s' service (from: '%s:%d')", objId, localClient.getServerAddr(), localClient.getServerPort(), srvInfo.getSrvId(), localClient.getClientAddr(), localClient.getClientPort()));
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
     * Set the represent object's name and trigger corresponding event.
     *
     * @param name the object's name.
     */
    private void updateName(String name) {
        this.name = name;
        // ToDo: trigger onNameUpdated() event
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(String newName) throws ObjectNotConnected {
        requestSetObjectName(newName);
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
     * Set the represent object's owner id and trigger corresponding event.
     *
     * @param ownerId the object's owner id.
     */
    private void updateOwnerId(String ownerId) {
        this.ownerId = ownerId;
        // ToDo: trigger onOwnerIdUpdated() event
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOwnerId(String newOwnerId) throws ObjectNotConnected {
        requestSetObjectOwnerId(newOwnerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJODVersion() {
        return jodVersion != null ? jodVersion : "N/A";
    }

    /**
     * Set the represent object's JOD version and trigger corresponding event.
     *
     * @param jodVersion the object's JOD version.
     */
    private void updateJODVersion(String jodVersion) {
        this.jodVersion = jodVersion;
        // ToDo: trigger onJODVersionUpdated() event
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLCommunication getCommunication() {
        return communication;
    }


    // Object's local communication

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return isCloudConnected() || isLocalConnected();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCloudConnected() {
        return communication.isCloudConnected() && isCloudConnected;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLocalConnected() {
        return getConnectedLocalClient() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLocalClient(JSLLocalClient localClient) {
        boolean wasObjectConnected = isLocalConnected();
        if (!isLocalConnected() && !localClient.isConnected()) {
            log.trace(Mrk_JSL.JSL_OBJS_SUB, String.format("Object '%s' not connected, connect local connection", localClient.getObjId()));
            try {
                localClient.connect();
            } catch (Client.ConnectionException ignore) {}
        }
        if (isLocalConnected() && localClient.isConnected()) {
            log.trace(Mrk_JSL.JSL_OBJS_SUB, String.format("Object '%s' already connected", localClient.getObjId()));
            // Force switch thread, to allow starting client's thread
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {}
            localClient.disconnect();
        }

        log.debug(Mrk_JSL.JSL_COMM, String.format("Checking object '%s' connection from '%s' service already known", localClient.getServerInfo().getServerId(), srvInfo.getSrvId()));
        JSLLocalClient toUpdate = null;
        for (JSLLocalClient cl : localConnections)
            if (cl.getServerAddr().equals(localClient.getServerAddr())
                    && cl.getServerPort() == localClient.getServerPort()
                //&& cl.getClientAddr().equals(locConn.getClientAddr()) // client's address and port vary on socket disconnection
                //&& cl.getClientPort() == locConn.getClientPort()
            ) {
                log.debug(Mrk_JSL.JSL_COMM, String.format("Connection already known to '%s' object on server '%s:%d' from '%s' service's '%s:%d' client", name, localClient.getServerAddr(), localClient.getServerPort(), srvInfo.getSrvId(), localClient.getClientAddr(), localClient.getClientPort()));
                if (!cl.isConnected()) {
                    toUpdate = cl;
                    break;
                }
                log.trace(Mrk_JSL.JSL_COMM, String.format("Disconnect new connection to '%s' object on server '%s:%d' from '%s' service's '%s:%d' client", name, localClient.getServerAddr(), localClient.getServerPort(), srvInfo.getSrvId(), localClient.getClientAddr(), localClient.getClientPort()));
                cl.disconnect();
                return;
            }
        if (toUpdate == null)
            log.debug(Mrk_JSL.JSL_COMM, String.format("Connection NOT known to '%s' object on server '%s:%d' from '%s' service's '%s:%d' client", name, localClient.getServerAddr(), localClient.getServerPort(), srvInfo.getSrvId(), localClient.getClientAddr(), localClient.getClientPort()));

        if (toUpdate == null) {
            log.trace(Mrk_JSL.JSL_OBJS_SUB, String.format("Adding new connection for '%s' object on '%s' service", localClient.getObjId(), srvInfo.getSrvId()));
        } else {
            log.trace(Mrk_JSL.JSL_OBJS_SUB, String.format("Updating existing connection for '%s' object on '%s' service", localClient.getObjId(), srvInfo.getSrvId()));
            localConnections.remove(toUpdate);
        }
        localConnections.add(localClient);
        localClient.setRemoteObject(this);

        if (!wasObjectConnected) {
            log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Requesting object's '%s' presentation", localClient.getObjId()));
            try {
                requestLocalObjectInfo();
                requestLocalObjectStructure();
                log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Object '%s' info and structure requested", localClient.getObjId()));

            } catch (ObjectNotConnected e) {
                log.warn(Mrk_JSL.JSL_OBJS_SUB, String.format("Error on requesting object's '%s' presentations because %s", localClient.getObjId(), e.getMessage()), e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceLocalClient(JSLLocalClient oldClient, JSLLocalClient newClient) {
        log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Replacing local connection for '%s' object (from: '%s:%d' to: '%s:%d')", oldClient.getObjId(), oldClient.getClientAddr(), oldClient.getClientPort(), oldClient.getServerAddr(), oldClient.getServerPort()));
        localConnections.remove(oldClient);
        localConnections.add(newClient);
        log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Local connection replaced for '%s' object (from: '%s:%d' to: '%s:%d')", newClient.getObjId(), newClient.getClientAddr(), newClient.getClientPort(), newClient.getServerAddr(), newClient.getServerPort()));
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
    public JSLLocalClient getConnectedLocalClient() {
        for (JSLLocalClient client : localConnections) {
            if (client.isConnected())
                return client;
        }
        return null;
    }

    /**
     * Util method that send given message to represented object via local comm.
     * <p>
     * This method use the connected client, in the object's clients list, to
     * send the message.
     *
     * @param msg the message to send to the represented object.
     */
    private void sendLocal(String msg) throws ObjectNotConnected {
        JSLLocalClient cli = getConnectedLocalClient();
        if (cli == null)
            throw new ObjectNotConnected(this);

        try {
            cli.sendData(msg);
        } catch (Client.ServerNotConnectedException e) {
            throw new ObjectNotConnected(this, e);
        }
    }


    // Object's cloud communication

    /**
     * Util method that send given message to represented object via cloud.
     * <p>
     * This method use the connected S2O GW client to send the message.
     *
     * @param msg the message to send to the represented object.
     */
    private void sendCloud(String msg) throws ObjectNotConnected {
        if (!communication.getCloudConnection().isConnected())
            throw new ObjectNotConnected(this);

        try {
            communication.getCloudConnection().sendData(msg);
        } catch (Client.ServerNotConnectedException e) {
            throw new ObjectNotConnected(this, e);
        }
    }


    // Object's requests

    /**
     * Send ObjectInfo request to represented object.
     */
    private void requestLocalObjectInfo() throws ObjectNotConnected {
        sendLocal(JOSPProtocol_ServiceRequests.createObjectInfoRequest(srvInfo.getFullId()));
    }

    /**
     * Send ObjectStruct request to represented object.
     */
    private void requestLocalObjectStructure() throws ObjectNotConnected {
        sendLocal(JOSPProtocol_ServiceRequests.createObjectStructureRequest(srvInfo.getFullId(), lastStructureUpdate));
    }

    /**
     * Send SetObjectName request to represented object.
     */
    private void requestSetObjectName(String newName) throws ObjectNotConnected {
        if (isLocalConnected())
            sendLocal(JOSPProtocol_ServiceRequests.createObjectSetNameRequest(srvInfo.getFullId(), newName));
        else if (isCloudConnected())
            sendCloud(JOSPProtocol_CloudRequests.createObjectSetNameRequest(srvInfo.getFullId(), getId(), newName));
        else
            throw new ObjectNotConnected(this);
    }

    /**
     * Send SetObjectOwnerId request to represented object.
     */
    private void requestSetObjectOwnerId(String newOwnerId) throws ObjectNotConnected {
        if (isLocalConnected())
            sendLocal(JOSPProtocol_ServiceRequests.createObjectSetOwnerIdRequest(srvInfo.getFullId(), newOwnerId));
        else if (isCloudConnected())
            sendCloud(JOSPProtocol_CloudRequests.createObjectSetOwnerIdRequest(srvInfo.getFullId(), getId(), newOwnerId));
        else
            throw new ObjectNotConnected(this);
    }


    // Process object's data

    /**
     * {@inheritDoc}
     * <p>
     * // ToDo: remove 'return false;' on processing errors and substitute with 'thrown new ProcessingException()'
     */
    @Override
    public boolean processServiceRequestResponse(String msg) {
        // Object info request's response
        if (JOSPProtocol_ServiceRequests.isObjectInfoRequestResponse(msg)) {
            log.info(Mrk_JSL.JSL_OBJS_SUB, String.format("Process ObjectInfo response for '%s' object", objId));
            return processObjectInfoResponse(msg);
        }

        // Object structure request's response
        if (JOSPProtocol_ServiceRequests.isObjectStructureRequestResponse(msg)) {
            log.info(Mrk_JSL.JSL_OBJS_SUB, String.format("Process ObjectStructure response for '%s' object", objId));
            return processObjectStructureResponse(msg);
        }

        return false;
    }

    private boolean processObjectInfoResponse(String msg) {
        log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Processing ObjectInfo message for '%s' object", objId));

        try {
            updateName(JOSPProtocol_ServiceRequests.extractObjectInfoObjNameFromResponse(msg));
            updateOwnerId(JOSPProtocol_ServiceRequests.extractObjectInfoOwnerIdFromResponse(msg));
            updateJODVersion(JOSPProtocol_ServiceRequests.extractObjectInfoJodVersionFromResponse(msg));

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(Mrk_JSL.JSL_OBJS_SUB, String.format("Error on processing ObjectInfo message for '%s' object because %s", objId, e.getMessage()), e);
            return false;
        }
        log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("ObjectInfo message processed for '%s' object", objId));
        return true;
    }

    private boolean processObjectStructureResponse(String msg) {
        log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Processing ObjectStructure message for '%s' object", objId));

        try {
            isCloudConnected = JOSPProtocol_CloudRequests.extractObjectStructureConnectedFromResponse(msg);
        } catch (Throwable ignore) { /*if parsing error, then the response is not from cloud*/}

        try {
            Date lastUpdated = JOSPProtocol_ServiceRequests.extractObjectStructureLastUpdateFromResponse(msg);
            if (lastStructureUpdate != null && lastStructureUpdate.compareTo(lastUpdated) >= 0) {
                log.warn(Mrk_JSL.JSL_OBJS_SUB, String.format("ObjectStructure message '%s...' for object '%s' object older than local object structure", msg.substring(0, Math.min(10, msg.length())), objId));
                return true;
            }

            String structStr = JOSPProtocol_ServiceRequests.extractObjectStructureStructureFromResponse(msg);
            try {
                ObjectMapper mapper = new ObjectMapper();

                InjectableValues.Std injectVars = new InjectableValues.Std();
                injectVars.addValue(JSLRemoteObject.class, this);
                mapper.setInjectableValues(injectVars);
                root = mapper.readValue(structStr, JSLRoot_Jackson.class);

                lastStructureUpdate = lastUpdated;

            } catch (JsonProcessingException e) {
                throw new ParsingException(this, String.format("Can't init JOD Structure, error on parsing JSON: '%s'.", e.getMessage().substring(0, e.getMessage().indexOf('\n'))), e, e.getLocation().getLineNr(), e.getLocation().getColumnNr());
            }

        } catch (JOSPProtocol.ParsingException | ParsingException e) {
            log.warn(Mrk_JSL.JSL_OBJS_SUB, String.format("Error on processing ObjectStructure message '%s...' for '%s' object because %s", msg.substring(0, Math.min(10, msg.length())), objId, e.getMessage()), e);
            return false;
        }
        log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("ObjectStructure message processed for '%s' object", objId));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean processCloudData(String msg) {

        // Object info request's response
        if (JOSPProtocol_CloudRequests.isObjectInfoRequestResponse(msg)) {
            log.info(Mrk_JSL.JSL_OBJS_SUB, String.format("Process ObjectInfo response for '%s' object", objId));
            return processObjectInfoResponse(msg);
        }

        // Object structure request's response
        if (JOSPProtocol_CloudRequests.isObjectStructureRequestResponse(msg)) {
            log.info(Mrk_JSL.JSL_OBJS_SUB, String.format("Process ObjectStructure response for '%s' object", objId));
            return processObjectStructureResponse(msg);
        }

        // Object disconnection request's response
        if (JOSPProtocol_CloudRequests.isObjectDisconnectRequestResponse(msg)) {
            log.info(Mrk_JSL.JSL_OBJS_SUB, String.format("Process ObjectDisconnection response for '%s' object", objId));
            isCloudConnected = false;
            return true;
        }

        return false;
    }

}
