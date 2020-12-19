/* *****************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.josp.jod.comm;

import com.robypomper.communication.client.Client;
import com.robypomper.communication.server.Server;
import com.robypomper.discovery.DiscoverySystemFactory;
import com.robypomper.discovery.Publisher;
import com.robypomper.discovery.impl.DiscoveryJmDNS;
import com.robypomper.java.JavaJSONArrayToFile;
import com.robypomper.josp.clients.AbsGWsClient;
import com.robypomper.josp.clients.JCPAPIsClientObj;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.events.Events;
import com.robypomper.josp.jod.events.JODEvents_002;
import com.robypomper.josp.jod.objinfo.JODObjectInfo_002;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.permissions.JODPermissions_002;
import com.robypomper.josp.jod.structure.*;
import com.robypomper.josp.protocol.*;
import com.robypomper.josp.states.StateException;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Implementation of the {@link JODCommunication} interface.
 */
public class JODCommunication_002 implements JODCommunication {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    // JOD
    private final JODSettings_002 locSettings;
    private final JODObjectInfo_002 objInfo;
    private final JODPermissions_002 permissions;
    private final JODEvents_002 events;
    private JODStructure_002 structure;
    // Comms
    private final JCPAPIsClientObj jcpClient;
    private final JODGwO2SClient gwClient;
    private JODLocalServer localServer = null;
    private final Publisher localServerPublisher;


    // Constructor

    /**
     * Default constructor that initialize the local server and the O2S gateway
     * client.
     *
     * @param settings    the JOD settings.
     * @param objInfo     the object's info.
     * @param jcpClient   the jcp object client.
     * @param permissions the {@link JODPermissions} instance used to check
     *                    connected services's permissions to receive updates or
     * @param events      the object's events manager.
     */
    public JODCommunication_002(JODSettings_002 settings, JODObjectInfo_002 objInfo, JCPAPIsClientObj jcpClient, JODPermissions_002 permissions, JODEvents_002 events, String instanceId) throws LocalCommunicationException, AbsGWsClient.GWsClientException {
        this.locSettings = settings;
        this.objInfo = objInfo;
        this.permissions = permissions;
        this.jcpClient = jcpClient;
        jcpClient.addConnectionListener(jcpConnectionListener);
        this.events = events;

        // Publish local object server
        String publisherImpl = locSettings.getLocalDiscovery();
        String publisherSrvName = objInfo.getObjName() + "-" + instanceId;
        try {
            log.debug(Mrk_JOD.JOD_COMM, String.format("Creating publisher '%s' service for local object's server", publisherImpl));
            // If required JmDNS implementation, start his sub-system
            if (DiscoveryJmDNS.IMPL_NAME.equalsIgnoreCase(publisherImpl)) {
                log.trace(Mrk_JOD.JOD_COMM, "Communication initializing discovery subsystem on JmDNS");
                DiscoveryJmDNS.startJmDNSSubSystem();
            }
            log.trace(Mrk_JOD.JOD_COMM, String.format("Local object's server publisher use '%s' service name", publisherSrvName));
            int localPort = locSettings.getLocalServerPort();
            localServerPublisher = DiscoverySystemFactory.createPublisher(publisherImpl, JOSPProtocol.DISCOVERY_TYPE, publisherSrvName, localPort, instanceId);
            log.debug(Mrk_JOD.JOD_COMM, String.format("Publisher '%s' service created for local object's server", publisherImpl));

        } catch (Publisher.PublishException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on creating publisher '%s' service for local object's server because %s", publisherImpl, e.getMessage()), e);
            throw new LocalCommunicationException(String.format("Error on creating publisher '%s' service for local object's server", publisherImpl), e);
        }

        // Init cloud object client
        this.gwClient = new JODGwO2SClient(this, objInfo, jcpClient, instanceId);

        log.info(Mrk_JOD.JOD_COMM, String.format("Initialized JODCommunication instance for '%s' ('%s') object", objInfo.getObjName(), objInfo.getObjId()));
    }

    private JODLocalServer initLocalServer() throws IOException {
        int localPort = locSettings.getLocalServerPort();
        String localPubCertFile = File.createTempFile(String.format("josp/jodCert-%s-", objInfo.getObjId()), ".crt").getAbsolutePath();
        log.trace(Mrk_JOD.JOD_COMM, String.format("Local object's server use '%s' server id", objInfo.getObjId()));
        log.trace(Mrk_JOD.JOD_COMM, String.format("Local object's server use '%d' port", localPort));
        log.trace(Mrk_JOD.JOD_COMM, String.format("Local object's server use public certificate file '%s'", localPubCertFile));
        return new JODLocalServer(this, objInfo, permissions, localPort, localPubCertFile);
    }


    // To Service Msg

    @Override
    public boolean sendToServices(String msg, JOSPPerm.Type minReqPerm) {
        log.info(Mrk_JOD.JOD_COMM, String.format("Send '%s' message to local services and cloud", msg.substring(0, msg.indexOf('\n'))));

        // Send via local communication
        if (isLocalRunning()) {
            for (JODLocalClientInfo locConn : getAllLocalClientsInfo()) {
                if (!locConn.isConnected() || !permissions.checkPermission(locConn.getSrvId(), locConn.getUsrId(), minReqPerm, JOSPPerm.Connection.OnlyLocal))
                    continue;

                try {
                    localServer.sendData(locConn.getClientId(), msg);

                } catch (Server.ServerStoppedException | Server.ClientNotFoundException | Server.ClientNotConnectedException ignore) {
                }
            }
        }

        // Send via cloud communication
        if (gwClient.isConnected()) {
            try {
                gwClient.sendData(msg);

            } catch (Client.ServerNotConnectedException e) {
                log.warn(Mrk_JOD.JOD_COMM, String.format("Error on sending message '%s' to service (via cloud) because %s", msg.substring(0, msg.indexOf('\n')), e.getMessage()), e);
            }
        }

        return isLocalRunning() || gwClient.isConnected();
    }

    @Override
    public boolean sendToCloud(String msg) throws CloudNotConnected {
        log.info(Mrk_JOD.JOD_COMM, String.format("Send '%s' message to cloud only", msg.substring(0, msg.indexOf('\n'))));

        try {
            gwClient.sendData(msg);

        } catch (Client.ServerNotConnectedException e) {
            throw new CloudNotConnected(gwClient);
        }

        return true;
    }

    @Override
    public boolean sendToSingleLocalService(JODLocalClientInfo locConn, String msg, JOSPPerm.Type minReqPerm) throws ServiceNotConnected {
        log.info(Mrk_JOD.JOD_COMM, String.format("Send '%s' message to local service '%s' only", msg.substring(0, msg.indexOf('\n')), locConn.getFullSrvId()));

        if (!permissions.checkPermission(locConn.getSrvId(), locConn.getUsrId(), minReqPerm, JOSPPerm.Connection.OnlyLocal))
            return false;

        try {
            localServer.sendData(locConn.getClientId(), msg);

        } catch (Server.ServerStoppedException | Server.ClientNotFoundException | Server.ClientNotConnectedException e) {
            throw new ServiceNotConnected(locConn);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendObjectUpdMsg(JODState component, JODStateUpdate update) {
        String msg = JOSPProtocol_ObjectToService.createObjectStateUpdMsg(objInfo.getObjId(), component.getPath().getString(), update);
        sendToServices(msg, JOSPPerm.Type.Status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void syncObject() {
        objInfo.syncObjInfo();
        structure.syncObjStruct();
        permissions.syncObjPermissions();
    }


    // From Service Msg

    @Override
    public boolean processFromServiceMsg(String msg, JOSPPerm.Connection connType) {
        log.info(Mrk_JOD.JOD_COMM, String.format("Received '%s' message from %s", msg.substring(0, msg.indexOf('\n')), connType == JOSPPerm.Connection.OnlyLocal ? "local service" : "cloud"));

        try {
            String srvId = JOSPProtocol_ServiceToObject.getSrvId(msg);
            String usrId = JOSPProtocol_ServiceToObject.getUsrId(msg);

            // dispatch to processor
            boolean processedSuccessfully;
            if (JOSPProtocol_ServiceToObject.isObjectSetNameMsg(msg))
                processedSuccessfully = permissions.checkPermission(srvId, usrId, JOSPPerm.Type.CoOwner, connType) && processObjectSetNameMsg(msg);
            else if (JOSPProtocol_ServiceToObject.isObjectSetOwnerIdMsg(msg))
                processedSuccessfully = permissions.checkPermission(srvId, usrId, JOSPPerm.Type.CoOwner, connType) && processObjectSetOwnerIdMsg(msg);
            else if (JOSPProtocol_ServiceToObject.isObjectAddPermMsg(msg))
                processedSuccessfully = permissions.checkPermission(srvId, usrId, JOSPPerm.Type.CoOwner, connType) && processObjectAddPermMsg(msg);
            else if (JOSPProtocol_ServiceToObject.isObjectUpdPermMsg(msg))
                processedSuccessfully = permissions.checkPermission(srvId, usrId, JOSPPerm.Type.CoOwner, connType) && processObjectUpdPermMsg(msg);
            else if (JOSPProtocol_ServiceToObject.isObjectRemPermMsg(msg))
                processedSuccessfully = permissions.checkPermission(srvId, usrId, JOSPPerm.Type.CoOwner, connType) && processObjectRemPermMsg(msg);

            else if (JOSPProtocol_ServiceToObject.isObjectActionCmdMsg(msg))
                processedSuccessfully = permissions.checkPermission(srvId, usrId, JOSPPerm.Type.Actions, connType) && processObjectCmdMsg(msg, srvId, usrId, connType);

            else if (JOSPProtocol_ServiceToObject.isHistoryCompStatusMsg(msg))
                processedSuccessfully = permissions.checkPermission(srvId, usrId, JOSPProtocol_ServiceToObject.HISTORY_STATUS_REQ_MIN_PERM, connType) && processHistoryCompStatusMsg(msg, srvId, usrId, connType);
            else if (JOSPProtocol_ServiceToObject.isHistoryEventsMsg(msg))
                processedSuccessfully = permissions.checkPermission(srvId, usrId, JOSPProtocol_ServiceToObject.HISTORY_EVENTS_REQ_MIN_PERM, connType) && processHistoryEventsMsg(msg, srvId, usrId, connType);

            else
                throw new Throwable(String.format("Error on processing '%s' message because unknown message type", msg.substring(0, msg.indexOf('\n'))));

            if (!processedSuccessfully)
                throw new Throwable(String.format("Error on processing '%s' message", msg.substring(0, msg.indexOf('\n'))));

            log.info(Mrk_JOD.JOD_COMM, String.format("Message '%s' processed successfully", msg.substring(0, msg.indexOf('\n'))));
            return true;

        } catch (Throwable t) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing '%s' message from %s because %s", msg.substring(0, msg.indexOf('\n')), connType == JOSPPerm.Connection.OnlyLocal ? "local service" : "cloud", t.getMessage()), t);
            return false;
        }
    }

    private boolean processObjectCmdMsg(String msg, String srvId, String usrId, JOSPPerm.Connection connType) {
        if (!JOSPProtocol.isCmdMsg(msg))
            return false;

        JOSPProtocol.ActionCmd cmd;
        try {
            cmd = JOSPProtocol.fromMsgToCmd(msg, AbsJODAction.getActionClasses());
        } catch (JOSPProtocol.ParsingException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on parsing command '%s...' because %s", msg.substring(0, msg.indexOf("\n")), e.getMessage()), e);
            return false;
        }

        log.info(Mrk_JOD.JOD_COMM, String.format("Forward command to component '%s'", cmd.getComponentPath()));
        Events.registerActionReq(srvId, usrId, connType, cmd);

        // search destination components
        JODComponentPath compPath = new DefaultJODComponentPath(cmd.getComponentPath());
        JODComponent comp = DefaultJODComponentPath.searchComponent(structure.getRoot(), compPath);

        // exec command msg
        log.trace(Mrk_JOD.JOD_COMM, String.format("Processing command on '%s' component", compPath.getString()));
        if (comp == null) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing update on '%s' component because component not found", compPath.getString()));
            Events.registerActionExecFail("Component not found", srvId, usrId, connType, cmd);
            return false;
        }
        if (!(comp instanceof JODAction)) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing update on '%s' component because component not an action component", compPath.getString()));
            Events.registerActionExecFail("Component is not an action", srvId, usrId, connType, cmd);
            return false;
        }
        JODAction actionComp = (JODAction) comp;

        // exec component's action
        if (!actionComp.execAction(cmd)) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing command on '%s' component", compPath.toString()));
            Events.registerActionExecFail("Unknown error on executing action command", srvId, usrId, connType, cmd);
            return false;
        }
        log.info(Mrk_JOD.JOD_COMM, String.format("Command status of '%s' component", compPath.toString()));

        log.debug(Mrk_JOD.JOD_COMM, String.format("Command '%s...' processed", msg.substring(0, Math.min(10, msg.length()))));
        Events.registerActionExec(srvId, usrId, connType, cmd);
        return true;
    }

    private boolean processHistoryCompStatusMsg(String msg, String srvId, String usrId, JOSPPerm.Connection connType) {
        if (connType == JOSPPerm.Connection.LocalAndCloud) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing message %s because received from cloud communication", JOSPProtocol_ServiceToObject.HISTORY_STATUS_REQ_NAME));
            return false;
        }

        String fullSrvId;
        String compPathStr;
        String reqId;
        HistoryLimits limits;
        try {
            fullSrvId = JOSPProtocol_ServiceToObject.getHistoryCompStatusMsg_FullSrvId(msg);
            compPathStr = JOSPProtocol_ServiceToObject.getHistoryCompStatusMsg_CompPath(msg);
            reqId = JOSPProtocol_ServiceToObject.getHistoryCompStatusMsg_ReqId(msg);
            limits = JOSPProtocol_ServiceToObject.getHistoryCompStatusMsg_Limits(msg);

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing message %s because %s", JOSPProtocol_ServiceToObject.HISTORY_STATUS_REQ_NAME, e.getMessage()), e);
            return false;
        }

        // search destination components
        JODComponentPath compPath = new DefaultJODComponentPath(compPathStr);
        JODComponent comp = DefaultJODComponentPath.searchComponent(structure.getRoot(), compPath);
        if (comp == null) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing message %s because component '%s' not found", JOSPProtocol_ObjectToService.HISTORY_STATUS_REQ_NAME, compPathStr));
            return false;
        }

        // prepare response
        List<JOSPStatusHistory> statusesHistory = comp.getHistoryStatus(limits);
        String response = JOSPProtocol_ObjectToService.createHistoryCompStatusMsg(objInfo.getObjId(), compPathStr, reqId, statusesHistory);

        // send response
        JODLocalClientInfo locConn = findLocalClientsInfo(fullSrvId);
        try {
            sendToSingleLocalService(locConn, response, JOSPPerm.Type.Status);
        } catch (ServiceNotConnected e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on sending message %s because %s", JOSPProtocol_ObjectToService.HISTORY_STATUS_REQ_NAME, e.getMessage()), e);
            return false;
        }

        return true;
    }

    private boolean processHistoryEventsMsg(String msg, String srvId, String usrId, JOSPPerm.Connection connType) {
        if (connType == JOSPPerm.Connection.LocalAndCloud) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing message %s because received from cloud communication", JOSPProtocol_ServiceToObject.HISTORY_EVENTS_REQ_NAME));
            return false;
        }

        String fullSrvId;
        String reqId;
        HistoryLimits limits;
        String filterEventType;
        try {
            fullSrvId = JOSPProtocol_ServiceToObject.getHistoryEventsMsg_FullSrvId(msg);
            reqId = JOSPProtocol_ServiceToObject.getHistoryEventsMsg_ReqId(msg);
            limits = JOSPProtocol_ServiceToObject.getHistoryEventsMsg_Limits(msg);
            filterEventType = JOSPProtocol_ServiceToObject.getHistoryEventsMsg_FilterEventType(msg);

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing message %s because %s", JOSPProtocol_ServiceToObject.HISTORY_STATUS_REQ_NAME, e.getMessage()), e);
            return false;
        }


        // prepare response
        List<JOSPEvent> eventsHistory;
        if (filterEventType.isEmpty())
            eventsHistory = events.getHistoryEvents(limits);
        else {
            JOSPEvent.Type type = JOSPEvent.Type.valueOf(filterEventType);
            eventsHistory = events.filterHistoryEvents(limits, new JavaJSONArrayToFile.Filter<JOSPEvent>() {
                @Override
                public boolean accepted(JOSPEvent o) {
                    return o.getType() == type;
                }
            });
        }
        String response = JOSPProtocol_ObjectToService.createHistoryEventsMsg(objInfo.getObjId(), reqId, eventsHistory);

        // send response
        JODLocalClientInfo locConn = findLocalClientsInfo(fullSrvId);
        try {
            sendToSingleLocalService(locConn, response, JOSPProtocol_ObjectToService.HISTORY_EVENTS_REQ_MIN_PERM);
        } catch (ServiceNotConnected e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on sending message %s because %s", JOSPProtocol_ObjectToService.HISTORY_STATUS_REQ_NAME, e.getMessage()), e);
            return false;
        }

        return true;
    }

    private boolean processObjectSetNameMsg(String msg) {
        String newName;
        try {
            newName = JOSPProtocol_ServiceToObject.getObjectSetNameMsg_Name(msg);

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing message %s because %s", JOSPProtocol_ServiceToObject.OBJ_SETNAME_REQ_NAME, e.getMessage()), e);
            return false;
        }

        objInfo.setObjName(newName);
        return true;
    }

    private boolean processObjectSetOwnerIdMsg(String msg) {
        String newOwnerId;
        try {
            newOwnerId = JOSPProtocol_ServiceToObject.getObjectSetOwnerIdMsg_OwnerId(msg);

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing message %s because %s", JOSPProtocol_ServiceToObject.OBJ_SETOWNERID_REQ_NAME, e.getMessage()), e);
            return false;
        }

        permissions.setOwnerId(newOwnerId);
        return true;
    }

    private boolean processObjectAddPermMsg(String msg) {
        String srvId;
        String usrId;
        JOSPPerm.Type permType;
        JOSPPerm.Connection connType;
        try {
            srvId = JOSPProtocol_ServiceToObject.getObjectAddPermMsg_SrvId(msg);
            usrId = JOSPProtocol_ServiceToObject.getObjectAddPermMsg_UsrId(msg);
            permType = JOSPProtocol_ServiceToObject.getObjectAddPermMsg_PermType(msg);
            connType = JOSPProtocol_ServiceToObject.getObjectAddPermMsg_ConnType(msg);

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing message %s because %s", JOSPProtocol_ServiceToObject.OBJ_ADDPERM_REQ_NAME, e.getMessage()), e);
            return false;
        }

        permissions.addPermissions(srvId, usrId, permType, connType);
        return true;
    }

    private boolean processObjectUpdPermMsg(String msg) {
        String permId;
        String srvId;
        String usrId;
        JOSPPerm.Type permType;
        JOSPPerm.Connection connType;
        try {
            permId = JOSPProtocol_ServiceToObject.getObjectUpdPermMsg_PermId(msg);
            srvId = JOSPProtocol_ServiceToObject.getObjectUpdPermMsg_SrvId(msg);
            usrId = JOSPProtocol_ServiceToObject.getObjectUpdPermMsg_UsrId(msg);
            permType = JOSPProtocol_ServiceToObject.getObjectUpdPermMsg_PermType(msg);
            connType = JOSPProtocol_ServiceToObject.getObjectUpdPermMsg_ConnType(msg);

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing message %s because %s", JOSPProtocol_ServiceToObject.OBJ_UPDPERM_REQ_NAME, e.getMessage()), e);
            return false;
        }

        permissions.updPermissions(permId, srvId, usrId, permType, connType);
        return true;
    }

    private boolean processObjectRemPermMsg(String msg) {
        String permId;
        try {
            permId = JOSPProtocol_ServiceToObject.getObjectRemPermMsg_PermId(msg);

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing message %s because %s", JOSPProtocol_ServiceToObject.OBJ_REMPERM_REQ_NAME, e.getMessage()), e);
            return false;
        }

        permissions.remPermissions(permId);
        return true;
    }

    // Connections access

    /**
     * {@inheritDoc}
     */
    @Override
    public JCPAPIsClientObj getCloudAPIs() {
        return jcpClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JODGwO2SClient getCloudConnection() {
        return gwClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JODLocalClientInfo> getAllLocalClientsInfo() {
        return localServer == null ? new ArrayList<>() : localServer.getLocalClientsInfo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JODLocalClientInfo findLocalClientsInfo(String fullSrvId) {
        if (localServer == null)
            return null;

        for (JODLocalClientInfo cl : localServer.getLocalClientsInfo())
            if (cl.getFullSrvId().equalsIgnoreCase(fullSrvId))
                return cl;

        return null;
    }


    // Mngm methods

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLocalRunning() {
        return localServer != null && localServer.isRunning();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startLocal() throws LocalCommunicationException {
        if (isLocalRunning())
            return;

        log.info(Mrk_JOD.JOD_COMM, String.format("Start and publish local object's server '%s'", objInfo.getObjId()));

        try {
            log.debug(Mrk_JOD.JOD_COMM, "Starting local object's server");
            localServer = initLocalServer();
            localServer.start();
            log.debug(Mrk_JOD.JOD_COMM, "Local object's server started");
            Events.registerLocalStart("Comm Local Started", localServer);

            log.debug(Mrk_JOD.JOD_COMM, "Publishing local object's server");
            localServerPublisher.publish(true);
            log.debug(Mrk_JOD.JOD_COMM, "Local object's server published");
            Events.registerLocalStart("Comm Local Published", localServer);

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on initializing local communication object's server '%s' because %s", objInfo.getObjId(), e.getMessage()), e);
            Events.registerLocalStart("Comm Local Started", localServer, e);
            throw new LocalCommunicationException(String.format("Error on starting local communication object's server '%s'", objInfo.getObjId()), e);


        } catch (Server.ListeningException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on starting local communication object's server '%s' because %s", objInfo.getObjId(), e.getMessage()), e);
            Events.registerLocalStart("Comm Local Started", localServer, e);
            throw new LocalCommunicationException(String.format("Error on starting local communication object's server '%s'", objInfo.getObjId()), e);

        } catch (Publisher.PublishException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on publishing local communication object's server '%s' because %s", objInfo.getObjId(), e.getMessage()), e);
            Events.registerLocalStart("Comm Local Published", localServer, e);
            throw new LocalCommunicationException(String.format("Error on publishing local communication object's server '%s'", objInfo.getObjId()), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopLocal() throws LocalCommunicationException {
        if (!isLocalRunning())
            return;

        log.info(Mrk_JOD.JOD_COMM, String.format("Stop and hide local object's server '%s' on port '%d'", objInfo.getObjId(), localServer.getPort()));

        try {
            log.debug(Mrk_JOD.JOD_COMM, "Stopping local object's server");
            localServer.stop();
            log.debug(Mrk_JOD.JOD_COMM, "Local object's server stopped");
            Events.registerLocalStop("Comm Local Stopped", localServer);

            log.debug(Mrk_JOD.JOD_COMM, "Hiding local object's server");
            localServerPublisher.hide(true);
            log.debug(Mrk_JOD.JOD_COMM, "Local object's server hided");
            Events.registerLocalStop("Comm Local Hided", localServer);

        } catch (Publisher.PublishException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on hiding local communication object's server '%s' because %s", objInfo.getObjId(), e.getMessage()), e);
            Events.registerLocalStop("Comm Local Hided", localServer, e);
            throw new LocalCommunicationException(String.format("Error on hiding local communication object's server '%s'", objInfo.getObjId()), e);
        }
    }


    // Cross component references

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStructure(JODStructure structure) throws StructureSetException {
        if (this.structure != null)
            throw new StructureSetException();
        this.structure = (JODStructure_002) structure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JODStructure getStructure() throws StructureSetException {
        if (this.structure == null)
            throw new StructureSetException();
        return this.structure;
    }


    // JCP Client listeners

    private final JCPClient2.ConnectionListener jcpConnectionListener = new JCPClient2.ConnectionListener() {

        @Override
        public void onConnected(JCPClient2 jcpClient) {
            log.info(Mrk_JOD.JOD_COMM, String.format("JCP Client connected with %s flow", getFlowName(jcpClient)));
            Events.registerJCPConnection("JCP Connected", jcpClient, getFlowName(jcpClient));

            if (gwClient != null && !gwClient.isConnected() /*&& gwClient.shouldBeConnected()*/) {
                try {
                    gwClient.connect();

                } catch (StateException e) {
                    log.warn(Mrk_JOD.JOD_COMM, String.format("Error on reconnect JOSP Gw client because %s", e.getMessage()), e);
                }
            }
        }

        @Override
        public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on connecting to JCP APIs because %s", t.getMessage()), t);
            Events.registerJCPConnection("JCP Connection failed", jcpClient, getFlowName(jcpClient), t);
        }

        @Override
        public void onAuthenticationFailed(JCPClient2 jcpClient, Throwable t) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on authenticating to JCP APIs because %s", t.getMessage()), t);
            Events.registerJCPConnection("JCP Connection failed (authentication phase)", jcpClient, getFlowName(jcpClient), t);
        }

        @Override
        public void onDisconnected(JCPClient2 jcpClient) {
            log.info(Mrk_JOD.JOD_COMM, String.format("JCP Client disconnected with %s flow", getFlowName(jcpClient)));
            Events.registerJCPDisconnection("JCP Disconnected", jcpClient, getFlowName(jcpClient));
        }

    };

    private static String getFlowName(JCPClient2 jcpClient) {
        if (jcpClient.isUserAnonymous()) return "ClientCred";
        if (jcpClient.isUserAuthenticated()) return "AuthCode";
        return "NotConnected";
    }

}
