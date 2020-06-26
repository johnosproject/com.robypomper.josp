package com.robypomper.josp.jsl.objs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robypomper.communication.client.Client;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.structure.AbsJSLState;
import com.robypomper.josp.jsl.objs.structure.DefaultJSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLAction;
import com.robypomper.josp.jsl.objs.structure.JSLActionParams;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.jsl.objs.structure.JSLRoot_Jackson;
import com.robypomper.josp.jsl.objs.structure.JSLState;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import com.robypomper.josp.protocol.JOSPProtocol_ServiceToObject;
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
    private String model = null;
    private String brand = null;
    private String longDescr = null;
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
     * {@inheritDoc}
     */
    @Override
    public void setName(String newName) throws ObjectNotConnected {
        sendSetObjectNameMsg(newName);
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
    public void setOwnerId(String newOwnerId) throws ObjectNotConnected {
        sendSetObjectOwnerIdMsg(newOwnerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJODVersion() {
        return jodVersion != null ? jodVersion : "N/A";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModel() {
        return model != null ? model : "N/A";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBrand() {
        return brand != null ? brand : "N/A";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLongDescr() {
        return longDescr != null ? longDescr : "N/A";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLCommunication getCommunication() {
        return communication;
    }


    // Object's connection

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

        log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Checking object '%s' connection from '%s' service already known", localClient.getServerInfo().getServerId(), srvInfo.getSrvId()));
        JSLLocalClient toUpdate = null;
        for (JSLLocalClient cl : localConnections)
            if (cl.getServerAddr().equals(localClient.getServerAddr())
                    && cl.getServerPort() == localClient.getServerPort()
                //&& cl.getClientAddr().equals(locConn.getClientAddr()) // client's address and port vary on socket disconnection
                //&& cl.getClientPort() == locConn.getClientPort()
            ) {
                log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Connection already known to '%s' object on server '%s:%d' from '%s' service's '%s:%d' client", name, localClient.getServerAddr(), localClient.getServerPort(), srvInfo.getSrvId(), localClient.getClientAddr(), localClient.getClientPort()));
                if (!cl.isConnected()) {
                    toUpdate = cl;
                    break;
                }
                log.trace(Mrk_JSL.JSL_OBJS_SUB, String.format("Disconnect new connection to '%s' object on server '%s:%d' from '%s' service's '%s:%d' client", name, localClient.getServerAddr(), localClient.getServerPort(), srvInfo.getSrvId(), localClient.getClientAddr(), localClient.getClientPort()));
                cl.disconnect();
                return;
            }
        if (toUpdate == null)
            log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Connection NOT known to '%s' object on server '%s:%d' from '%s' service's '%s:%d' client", name, localClient.getServerAddr(), localClient.getServerPort(), srvInfo.getSrvId(), localClient.getClientAddr(), localClient.getClientPort()));

        if (toUpdate == null) {
            log.trace(Mrk_JSL.JSL_OBJS_SUB, String.format("Adding new connection for '%s' object on '%s' service", localClient.getObjId(), srvInfo.getSrvId()));
        } else {
            log.trace(Mrk_JSL.JSL_OBJS_SUB, String.format("Updating existing connection for '%s' object on '%s' service", localClient.getObjId(), srvInfo.getSrvId()));
            localConnections.remove(toUpdate);
        }
        localConnections.add(localClient);
        localClient.setRemoteObject(this);
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


    // To Object Msg

    private void sendToObject(String msg) throws ObjectNotConnected {
        if (!isConnected())
            throw new ObjectNotConnected(this);

        // Send via local communication
        if (isLocalConnected()) {
            JSLLocalClient object = getConnectedLocalClient();
            try {
                object.sendData(msg);
                return;

            } catch (Client.ServerNotConnectedException e) {
                log.warn(Mrk_JSL.JSL_OBJS_SUB, String.format("Error on sending message '%s' to object (via local) because %s", msg.substring(0, msg.indexOf('\n')), e.getMessage()), e);
            }
        }

        // Send via cloud communication
        if (isCloudConnected()) {
            try {
                communication.getCloudConnection().sendData(msg);

            } catch (Client.ServerNotConnectedException e) {
                log.warn(Mrk_JSL.JSL_OBJS_SUB, String.format("Error on sending message '%s' to object (via cloud) because %s", msg.substring(0, msg.indexOf('\n')), e.getMessage()), e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendObjectCmdMsg(JSLAction component, JSLActionParams command) throws ObjectNotConnected {
        sendToObject(JOSPProtocol_ServiceToObject.createObjectActionCmdMsg(srvInfo.getFullId(), getId(), component.getPath().getString(), command));
    }

    /**
     * Send SetObjectName request to represented object.
     */
    private void sendSetObjectNameMsg(String newName) throws ObjectNotConnected {
        sendToObject(JOSPProtocol_ServiceToObject.createObjectSetNameMsg(srvInfo.getFullId(), getId(), newName));
    }

    /**
     * Send SetObjectOwnerId request to represented object.
     */
    private void sendSetObjectOwnerIdMsg(String newOwnerId) throws ObjectNotConnected {
        sendToObject(JOSPProtocol_ServiceToObject.createObjectSetOwnerIdMsg(srvInfo.getFullId(), getId(), newOwnerId));
    }


    // From Object Msg

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean processFromObjectMsg(String msg, JOSPPerm.Connection connType) throws Throwable {
        if (JOSPProtocol_ObjectToService.isObjectInfoMsg(msg)) {
            return processObjectInfoMsg(msg, connType);
        } else if (JOSPProtocol_ObjectToService.isObjectStructMsg(msg))
            return processObjectStructMsg(msg);
        else if (JOSPProtocol_ObjectToService.isObjectPermsMsg(msg))
            return processObjectPermsMsg(msg);
        else if (JOSPProtocol_ObjectToService.isServicePermsMsg(msg))
            return processServicePermMsg(msg);
        else if (JOSPProtocol_ObjectToService.isObjectDisconnectMsg(msg)) {
            return processObjectDisconnectMsg(msg, connType);
        } else if (JOSPProtocol_ObjectToService.isObjectStateUpdMsg(msg))
            return processObjectUpdMsg(msg);

        throw new Throwable(String.format("Error on processing '%s' message because unknown message type", msg.substring(0, msg.indexOf('\n'))));
    }

    private boolean processObjectInfoMsg(String msg, JOSPPerm.Connection connType) {
        try {
            String newName = JOSPProtocol_ObjectToService.getObjectInfoMsg_Name(msg);
            if (name == null || !name.equals(newName)) {
                name = newName;
                // ToDo: trigger onObjectNameUpdated() event
            }
            String newOwnerId = JOSPProtocol_ObjectToService.getObjectInfoMsg_OwnerId(msg);
            if (ownerId == null || !ownerId.equals(newOwnerId)) {
                ownerId = newOwnerId;
                // ToDo: trigger onObjectOwnerIdUpdated() event
            }
            String newJODVersion = JOSPProtocol_ObjectToService.getObjectInfoMsg_JODVersion(msg);
            if (jodVersion == null || !jodVersion.equals(newJODVersion)) {
                jodVersion = newJODVersion;
                // ToDo: trigger onObjectJODVersionUpdated() event
            }
            String newModel = JOSPProtocol_ObjectToService.getObjectInfoMsg_Model(msg);
            if (model == null || !model.equals(newModel)) {
                model = newModel;
                // ToDo: trigger onObjectModelUpdated() event
            }
            String newBrand = JOSPProtocol_ObjectToService.getObjectInfoMsg_Brand(msg);
            if (brand == null || !brand.equals(newBrand)) {
                brand = newBrand;
                // ToDo: trigger onObjectBrandUpdated() event
            }
            String newLongDescr = JOSPProtocol_ObjectToService.getObjectInfoMsg_LongDescr(msg);
            if (longDescr == null || !longDescr.equals(newLongDescr)) {
                longDescr = newLongDescr;
                // ToDo: trigger onObjectLongDescrUpdated() event
            }

            isCloudConnected = connType == JOSPPerm.Connection.LocalAndCloud;

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(Mrk_JSL.JSL_OBJS_SUB, String.format("Error on processing ObjectInfo message for '%s' object because %s", objId, e.getMessage()), e);
            return false;
        }

        return true;
    }

    private boolean processObjectStructMsg(String msg) {
        try {
            String structStr = JOSPProtocol_ObjectToService.getObjectStructMsg_Struct(msg);
            try {
                ObjectMapper mapper = new ObjectMapper();

                InjectableValues.Std injectVars = new InjectableValues.Std();
                injectVars.addValue(JSLRemoteObject.class, this);
                mapper.setInjectableValues(injectVars);
                root = mapper.readValue(structStr, JSLRoot_Jackson.class);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new ParsingException(this, String.format("Can't init JOD Structure, error on parsing JSON: '%s'.", e.getMessage().substring(0, e.getMessage().indexOf('\n'))), e, e.getLocation().getLineNr(), e.getLocation().getColumnNr());
            }

        } catch (JOSPProtocol.ParsingException | ParsingException e) {
            log.warn(Mrk_JSL.JSL_OBJS_SUB, String.format("Error on processing ObjectStructure message '%s...' for '%s' object because %s", msg.substring(0, Math.min(10, msg.length())), objId, e.getMessage()), e);
            return false;
        }

        return true;
    }

    private boolean processObjectPermsMsg(String msg) throws Throwable {
        throw new Throwable("Processing 'objectPermsMsg' message not implemented");
    }

    private boolean processServicePermMsg(String msg) throws Throwable {
        throw new Throwable("Processing 'servicePermMsg' message not implemented");
    }

    private boolean processObjectDisconnectMsg(String msg, JOSPPerm.Connection connType) throws Throwable {
        if (connType == JOSPPerm.Connection.LocalAndCloud)
            isCloudConnected = false;
        return true;
    }

    private boolean processObjectUpdMsg(String msg) {
        // parse received data
        JOSPProtocol.StatusUpd upd;
        try {
            upd = JOSPProtocol.fromMsgToUpd(msg, AbsJSLState.getStateClasses());
        } catch (JOSPProtocol.ParsingException e) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on parsing update '%s...' because %s", msg.substring(0, msg.indexOf("\n")), e.getMessage()), e);
            return false;
        }
        // search destination object/components
        JSLComponentPath compPath = new DefaultJSLComponentPath(upd.getComponentPath());
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(getStructure(), compPath);

        // forward update msg
        log.trace(Mrk_JSL.JSL_COMM, String.format("Processing update on '%s' component for '%s' object", compPath.getString(), getId()));
        if (comp == null) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on processing update on '%s' component for '%s' object because component not found", compPath.getString(), getId()));
            return false;
        }
        if (!(comp instanceof JSLState)) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on processing update on '%s' component for '%s' object because component not a status component", compPath.getString(), getId()));
            return false;
        }
        JSLState stateComp = (JSLState) comp;

        // set object/component's update
        if (stateComp.updateStatus(upd)) {
            log.info(Mrk_JSL.JSL_COMM, String.format("Updated status of '%s' component for '%s' object", compPath.getString(), getId()));

        } else {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on processing update on '%s' component for '%s' object", compPath.getString(), getId()));
            return false;
        }

        log.debug(Mrk_JSL.JSL_COMM, String.format("Update '%s...' processed for '%s' object", msg.substring(0, Math.min(10, msg.length())), getId()));
        return true;
    }

}
