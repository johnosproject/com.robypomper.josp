/* *****************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2020 Roberto Pompermaier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **************************************************************************** */

package com.robypomper.josp.jsl.objs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robypomper.communication.client.Client;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.structure.*;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import com.robypomper.josp.protocol.JOSPProtocol_ServiceToObject;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;


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
    private List<JOSPPerm> perms = new ArrayList<>();
    private final Map<JOSPPerm.Connection, JOSPPerm.Type> permTypes = new HashMap<>();
    private final List<RemoteObjectConnListener> listenersConn = new ArrayList<>();
    private final List<RemoteObjectInfoListener> listenersInfo = new ArrayList<>();


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
        permTypes.put(JOSPPerm.Connection.OnlyLocal, JOSPPerm.Type.None);
        permTypes.put(JOSPPerm.Connection.LocalAndCloud, JOSPPerm.Type.None);

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
        permTypes.put(JOSPPerm.Connection.OnlyLocal, JOSPPerm.Type.None);
        permTypes.put(JOSPPerm.Connection.LocalAndCloud, JOSPPerm.Type.None);
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
    public void setName(String newName) throws ObjectNotConnected, MissingPermission {
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
    public void setOwnerId(String newOwnerId) throws ObjectNotConnected, MissingPermission {
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
    public List<JOSPPerm> getPerms() {
        return perms;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JOSPPerm.Type getServicePerm(JOSPPerm.Connection connType) {
        return permTypes.get(connType);
    }


    @Override
    public void addPerm(String srvId, String usrId, JOSPPerm.Type permType, JOSPPerm.Connection connType) throws ObjectNotConnected, MissingPermission {
        sendAddObjectPermMsg(srvId, usrId, permType, connType);
    }

    @Override
    public void updPerm(String permId, String srvId, String usrId, JOSPPerm.Type permType, JOSPPerm.Connection connType) throws ObjectNotConnected, MissingPermission {
        sendUpdObjectPermMsg(permId, srvId, usrId, permType, connType);
    }

    @Override
    public void remPerm(String permId) throws ObjectNotConnected, MissingPermission {
        sendRemObjectPermMsg(permId);
    }


    // Object's connection

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLCommunication getCommunication() {
        return communication;
    }

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
                localClient.disconnect();
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
        if (localClient.isConnected())
            emitConn_LocalConnected(localClient);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeLocalClient(JSLLocalClient localClient) {
        localConnections.remove(localClient);
        emitConn_LocalDisconnected(localClient);
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

    private void sendToObject(String msg) throws ObjectNotConnected, MissingPermission {
        if (!isConnected())
            throw new ObjectNotConnected(this);

        JOSPPerm.Type minReqPerm = JOSPPerm.Type.None;
        if (JOSPProtocol_ServiceToObject.isObjectSetNameMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectSetOwnerIdMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectAddPermMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectUpdPermMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectRemPermMsg(msg))
            minReqPerm = JOSPPerm.Type.CoOwner;

        if (JOSPProtocol_ServiceToObject.isObjectActionCmdMsg(msg))
            minReqPerm = JOSPPerm.Type.Actions;

        // Send via local communication
        if (isLocalConnected()) {
            JOSPPerm.Type permType = permTypes.get(JOSPPerm.Connection.OnlyLocal);
            if (permType.compareTo(minReqPerm) < 0)
                throw new MissingPermission(this, JOSPPerm.Connection.OnlyLocal, permType, minReqPerm, msg);

            try {
                getConnectedLocalClient().sendData(msg);
                return;

            } catch (Client.ServerNotConnectedException e) {
                log.warn(Mrk_JSL.JSL_OBJS_SUB, String.format("Error on sending message '%s' to object (via local) because %s", msg.substring(0, msg.indexOf('\n')), e.getMessage()), e);
            }
        }

        // Send via cloud communication
        if (isCloudConnected()) {
            JOSPPerm.Type permType = permTypes.get(JOSPPerm.Connection.LocalAndCloud);
            if (permType.compareTo(minReqPerm) < 0)
                throw new MissingPermission(this, JOSPPerm.Connection.LocalAndCloud, permType, minReqPerm, msg);

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
    public void sendObjectCmdMsg(JSLAction component, JSLActionParams command) throws ObjectNotConnected, MissingPermission {
        sendToObject(JOSPProtocol_ServiceToObject.createObjectActionCmdMsg(srvInfo.getFullId(), getId(), component.getPath().getString(), command));
    }

    /**
     * Send SetObjectName request to represented object.
     */
    private void sendSetObjectNameMsg(String newName) throws ObjectNotConnected, MissingPermission {
        sendToObject(JOSPProtocol_ServiceToObject.createObjectSetNameMsg(srvInfo.getFullId(), getId(), newName));
    }

    /**
     * Send SetObjectOwnerId request to represented object.
     */
    private void sendSetObjectOwnerIdMsg(String newOwnerId) throws ObjectNotConnected, MissingPermission {
        sendToObject(JOSPProtocol_ServiceToObject.createObjectSetOwnerIdMsg(srvInfo.getFullId(), getId(), newOwnerId));
    }

    private void sendAddObjectPermMsg(String srvId, String usrId, JOSPPerm.Type permType, JOSPPerm.Connection connType) throws ObjectNotConnected, MissingPermission {
        sendToObject(JOSPProtocol_ServiceToObject.createObjectAddPermMsg(srvInfo.getFullId(), getId(), srvId, usrId, permType, connType));
    }

    private void sendUpdObjectPermMsg(String permId, String srvId, String usrId, JOSPPerm.Type permType, JOSPPerm.Connection connType) throws ObjectNotConnected, MissingPermission {
        sendToObject(JOSPProtocol_ServiceToObject.createObjectUpdPermMsg(srvInfo.getFullId(), getId(), permId, srvId, usrId, permType, connType));
    }

    private void sendRemObjectPermMsg(String permId) throws ObjectNotConnected, MissingPermission {
        sendToObject(JOSPProtocol_ServiceToObject.createObjectRemPermMsg(srvInfo.getFullId(), getId(), permId));
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
                String oldName = name;
                name = newName;
                emitInfo_NameChanged(newName, oldName);
            }
            String newOwnerId = JOSPProtocol_ObjectToService.getObjectInfoMsg_OwnerId(msg);
            if (ownerId == null || !ownerId.equals(newOwnerId)) {
                String oldOwnerId = ownerId;
                ownerId = newOwnerId;
                emitInfo_OwnerIdChanged(newOwnerId, oldOwnerId);
            }
            String newJODVersion = JOSPProtocol_ObjectToService.getObjectInfoMsg_JODVersion(msg);
            if (jodVersion == null || !jodVersion.equals(newJODVersion)) {
                String oldJODVersion = jodVersion;
                jodVersion = newJODVersion;
                emitInfo_JODVersionChanged(jodVersion, oldJODVersion);
            }
            String newModel = JOSPProtocol_ObjectToService.getObjectInfoMsg_Model(msg);
            if (model == null || !model.equals(newModel)) {
                String oldModel = model;
                model = newModel;
                emitInfo_ModelChanged(model, oldModel);
            }
            String newBrand = JOSPProtocol_ObjectToService.getObjectInfoMsg_Brand(msg);
            if (brand == null || !brand.equals(newBrand)) {
                String oldBrand = brand;
                brand = newBrand;
                emitInfo_BrandChanged(brand, oldBrand);
            }
            String newLongDescr = JOSPProtocol_ObjectToService.getObjectInfoMsg_LongDescr(msg);
            if (longDescr == null || !longDescr.equals(newLongDescr)) {
                String oldLongDescr = longDescr;
                longDescr = newLongDescr;
                emitInfo_LongDescrChanged(longDescr, oldLongDescr);
            }

            if (connType == JOSPPerm.Connection.LocalAndCloud && !isCloudConnected) {
                isCloudConnected = true;
                emitConn_CloudConnected();
            }

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

        emitInfo_StructureChanged(root);
        return true;
    }

    private boolean processObjectPermsMsg(String msg) throws Throwable {
        List<JOSPPerm> oldPerms = perms;
        perms = JOSPProtocol_ObjectToService.getObjectPermsMsg_Perms(msg);
        emitInfo_PermissionsChanged(perms, oldPerms);
        return true;
    }

    private boolean processServicePermMsg(String msg) throws Throwable {
        JOSPPerm.Connection connType = JOSPProtocol_ObjectToService.getServicePermsMsg_ConnType(msg);
        JOSPPerm.Type oldPermType = permTypes.get(connType);
        permTypes.put(connType, JOSPProtocol_ObjectToService.getServicePermsMsg_PermType(msg));
        emitInfo_ServicePermChanged(connType, permTypes.get(connType), oldPermType);
        return true;
    }

    private boolean processObjectDisconnectMsg(String msg, JOSPPerm.Connection connType) throws Throwable {
        if (connType == JOSPPerm.Connection.LocalAndCloud) {
            isCloudConnected = false;
            emitConn_CloudDisconnected();
        }
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


    // Listeners connections

    public void addListener(RemoteObjectConnListener listener) {
        if (listenersConn.contains(listener))
            return;

        listenersConn.add(listener);
    }

    public void removeListener(RemoteObjectConnListener listener) {
        if (!listenersConn.contains(listener))
            return;

        listenersConn.remove(listener);
    }

    private void emitConn_LocalConnected(JSLLocalClient localClient) {
        for (RemoteObjectConnListener l : listenersConn)
            l.onLocalConnected(this, localClient);
    }

    private void emitConn_LocalDisconnected(JSLLocalClient localClient) {
        for (RemoteObjectConnListener l : listenersConn)
            l.onLocalDisconnected(this, localClient);

    }

    private void emitConn_CloudConnected() {
        for (RemoteObjectConnListener l : listenersConn)
            l.onCloudConnected(this);
    }

    private void emitConn_CloudDisconnected() {
        for (RemoteObjectConnListener l : listenersConn)
            l.onCloudDisconnected(this);
    }


    // Listeners info

    public void addListener(RemoteObjectInfoListener listener) {
        if (listenersInfo.contains(listener))
            return;

        listenersInfo.add(listener);
    }

    public void removeListener(RemoteObjectInfoListener listener) {
        if (!listenersInfo.contains(listener))
            return;

        listenersInfo.remove(listener);
    }

    private void emitInfo_NameChanged(String newName, String oldName) {
        for (RemoteObjectInfoListener l : listenersInfo)
            l.onNameChanged(this, newName, oldName);
    }

    private void emitInfo_OwnerIdChanged(String newOwnerId, String oldOwnerId) {
        for (RemoteObjectInfoListener l : listenersInfo)
            l.onOwnerIdChanged(this, newOwnerId, oldOwnerId);
    }

    private void emitInfo_JODVersionChanged(String jodVersion, String oldJODVersion) {
        for (RemoteObjectInfoListener l : listenersInfo)
            l.onJODVersionChanged(this, jodVersion, oldJODVersion);
    }

    private void emitInfo_ModelChanged(String model, String oldModel) {
        for (RemoteObjectInfoListener l : listenersInfo)
            l.onModelChanged(this, model, oldModel);
    }

    private void emitInfo_BrandChanged(String brand, String oldBrand) {
        for (RemoteObjectInfoListener l : listenersInfo)
            l.onBrandChanged(this, brand, oldBrand);
    }

    private void emitInfo_LongDescrChanged(String longDescr, String oldLongDescr) {
        for (RemoteObjectInfoListener l : listenersInfo)
            l.onLongDescrChanged(this, longDescr, oldLongDescr);
    }

    private void emitInfo_StructureChanged(JSLRoot root) {
        for (RemoteObjectInfoListener l : listenersInfo)
            l.onStructureChanged(this, root);
    }

    private void emitInfo_PermissionsChanged(List<JOSPPerm> perms, List<JOSPPerm> oldPerms) {
        for (RemoteObjectInfoListener l : listenersInfo)
            l.onPermissionsChanged(this, perms, oldPerms);
    }

    private void emitInfo_ServicePermChanged(JOSPPerm.Connection connType, JOSPPerm.Type type, JOSPPerm.Type oldPermType) {
        for (RemoteObjectInfoListener l : listenersInfo)
            l.onServicePermChanged(this, connType, type, oldPermType);
    }

}
