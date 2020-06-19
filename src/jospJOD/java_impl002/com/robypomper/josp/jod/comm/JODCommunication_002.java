package com.robypomper.josp.jod.comm;

import com.robypomper.communication.client.Client;
import com.robypomper.communication.server.Server;
import com.robypomper.discovery.DiscoverySystemFactory;
import com.robypomper.discovery.Publisher;
import com.robypomper.discovery.impl.DiscoveryJmDNS;
import com.robypomper.josp.jcp.apis.params.permissions.PermissionsTypes;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.structure.AbsJODAction;
import com.robypomper.josp.jod.structure.DefaultJODComponentPath;
import com.robypomper.josp.jod.structure.JODAction;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODComponentPath;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.JODStateUpdate;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_CloudRequests;
import com.robypomper.josp.protocol.JOSPProtocol_ServiceRequests;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Implementation of the {@link JODCommunication} interface.
 */
public class JODCommunication_002 implements JODCommunication {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JODSettings_002 locSettings;
    private final JODObjectInfo objInfo;
    private final JODPermissions permissions;
    private JODStructure structure;
    private final JCPClient_Object jcpClient;
    private final JCPCommObj jcpComm;
    private final String instanceId;
    private JODLocalServer localServer = null;
    private final Publisher localServerPublisher;
    private JODGwO2SClient gwClient;


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
     *                    exec actions.
     */
    public JODCommunication_002(JODSettings_002 settings, JODObjectInfo objInfo, JCPClient_Object jcpClient, JODPermissions permissions, String instanceId) throws LocalCommunicationException, CloudCommunicationException {
        this.locSettings = settings;
        this.objInfo = objInfo;
        this.permissions = permissions;
        this.jcpClient = jcpClient;
        this.jcpComm = new JCPCommObj(jcpClient, settings, instanceId);
        this.instanceId = instanceId;

//        // Init local object server
//        log.debug(Mrk_JOD.JOD_COMM, "Initializing communication local object's server");
//        localServer = initLocalServer();
//        log.debug(Mrk_JOD.JOD_COMM, "Communication local object's server initialized");

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
        try {
            log.debug(Mrk_JOD.JOD_COMM, "Creating communication cloud client for Object2Service Gateway");
            gwClient = new JODGwO2SClient(locSettings, this, objInfo, jcpClient, jcpComm);
            log.debug(Mrk_JOD.JOD_COMM, "Communication cloud client created for Object2Service Gateway");
        } catch (CloudCommunicationException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on creating object's cloud client because %s", e.getMessage()), e);
            throw new CloudCommunicationException("Error on creating object's cloud client", e);
        }

        log.info(Mrk_JOD.JOD_COMM, String.format("Initialized JODCommunication instance for '%s' ('%s') object", objInfo.getObjName(), objInfo.getObjId()));
        //log.debug(Mrk_JOD.JOD_COMM, String.format("                                    local server%s started and%s published", localServer.isRunning() ? "" : " NOT", localServerPublisher.isPublished() ? "" : " NOT"));
        log.debug(Mrk_JOD.JOD_COMM, String.format("                                    cloud client%s connected", gwClient.isConnected() ? "" : " NOT"));
    }

    private JODLocalServer initLocalServer() {
        try {
            int localPort = locSettings.getLocalServerPort();
            String localPubCertFile = File.createTempFile(String.format("josp/jodCert-%s-", objInfo.getObjId()), ".crt").getAbsolutePath();
            log.trace(Mrk_JOD.JOD_COMM, String.format("Local object's server use '%s' server id", objInfo.getObjId()));
            log.trace(Mrk_JOD.JOD_COMM, String.format("Local object's server use '%d' port", localPort));
            log.trace(Mrk_JOD.JOD_COMM, String.format("Local object's server use public certificate file '%s'", localPubCertFile));
            return new JODLocalServer(this, objInfo.getObjId(), localPort, localPubCertFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    // Status upd flow (objStruct - comm)

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispatchUpdate(JODState component, JODStateUpdate update) {
        log.info(Mrk_JOD.JOD_COMM, String.format("Dispatch update for component '%s'", component.getName()));

        String msg = JOSPProtocol.generateUpdToMsg(objInfo.getObjId(), component.getPath().getString(), update); // JODStateUpdate not in Commons

        // send to cloud
        if (isCloudConnected()) {
            try {
                log.trace(Mrk_JOD.JOD_COMM, String.format("Send update for component '%s' to cloud", component.getName()));
                gwClient.sendData(msg);
                log.trace(Mrk_JOD.JOD_COMM, String.format("Update for component '%s' send to cloud", component.getName()));

            } catch (Client.ServerNotConnectedException e) {
                log.warn(Mrk_JOD.JOD_COMM, String.format("Error on sending update for component '%s' to cloud because %s", component.getName(), e.getMessage()), e);
            }
        }

        // send to allowed local services
        if (isLocalRunning()) {
            log.trace(Mrk_JOD.JOD_COMM, String.format("Send update for component '%s' to connected local services", component.getName()));
            int countAll = 0;
            int countConnected = 0;
            int countSend = 0;
            for (JODLocalClientInfo locConn : getAllLocalClientsInfo()) {
                countAll++;
                if (locConn.isConnected() && permissions.canSendUpdate(locConn.getSrvId(), locConn.getUsrId(), PermissionsTypes.Connection.OnlyLocal)) {
                    countConnected++;
                    try {
                        localServer.sendData(locConn.getClientId(), msg);
                        countSend++;

                    } catch (Server.ServerStoppedException | Server.ClientNotFoundException | Server.ClientNotConnectedException e) {
                        log.warn(Mrk_JOD.JOD_COMM, String.format("Error on sending update for component '%s' to local client '%s' because %s", component.getName(), locConn.getClientId(), e.getMessage()), e);
                    }
                }
            }
            log.trace(Mrk_JOD.JOD_COMM, String.format("Update for component '%s' send to '%d/%d/%d' connected local services", component.getName(), countSend, countConnected, countAll));
        }
    }


    // Action cmd flow (comm - objStruct)

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean forwardAction(String msg, PermissionsTypes.Connection connType) {
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

        // check service permission
        if (!permissions.canExecuteAction(cmd.getServiceId(), cmd.getUserId(), connType)) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on executing command from '%s' service with '%s' user because not allowed on '%s' connection", cmd.getServiceId(), cmd.getUserId(), connType));
            return false;
        }

        // search destination components
        JODComponentPath compPath = new DefaultJODComponentPath(cmd.getComponentPath());
        JODComponent comp = DefaultJODComponentPath.searchComponent(structure.getRoot(), compPath);

        // exec command msg
        log.trace(Mrk_JOD.JOD_COMM, String.format("Processing command on '%s' component", compPath.getString()));
        if (comp == null) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing update on '%s' component because component not found", compPath.getString()));
            return false;
        }
        if (!(comp instanceof JODAction)) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing update on '%s' component because component not an action component", compPath.getString()));
            return false;
        }
        JODAction actionComp = (JODAction) comp;

        // exec component's action
        if (actionComp.execAction(cmd)) {
            log.info(Mrk_JOD.JOD_COMM, String.format("Command status of '%s' component", compPath.toString()));

        } else {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing command on '%s' component", compPath.toString()));
            return false;
        }

        log.debug(Mrk_JOD.JOD_COMM, String.format("Command '%s...' processed", msg.substring(0, Math.min(10, msg.length()))));
        return true;
    }


    // Local service requests

    /**
     * {@inheritDoc}
     */
    @Override
    public String processServiceRequest(JODLocalClientInfo client, String msg) {
        log.trace(Mrk_JOD.JOD_COMM, String.format("Process service request from '%s' service", client.getClientId()));

        try {
            String response = null;

            // Object info request
            if (JOSPProtocol_ServiceRequests.isObjectInfoRequest(msg)) {
                log.info(Mrk_JOD.JOD_COMM, String.format("Elaborate ObjectInfoRequest request for service '%s'", client.getClientId()));
                response = processObjectInfoRequest(client.getSrvId(), client.getUsrId(), msg);
            }

            // Object structure request
            if (JOSPProtocol_ServiceRequests.isObjectStructureRequest(msg)) {
                log.info(Mrk_JOD.JOD_COMM, String.format("Elaborate ObjectStructureRequest request for service '%s'", client.getClientId()));
                response = processObjectStructureRequest(client.getSrvId(), client.getUsrId(), msg);
            }

            // Object name request
            if (JOSPProtocol_ServiceRequests.isObjectSetNameRequest(msg)) {
                log.info(Mrk_JOD.JOD_COMM, String.format("Elaborate ObjectSetNameRequest request for service '%s'", client.getClientId()));
                response = processObjectSetNameRequest(client.getSrvId(), client.getUsrId(), msg, false);
            }

            // Object owner id request
            if (JOSPProtocol_ServiceRequests.isObjectSetOwnerIdRequest(msg)) {
                log.info(Mrk_JOD.JOD_COMM, String.format("Elaborate ObjectSetOwnerIdRequest request for service '%s'", client.getClientId()));
                response = processObjectSetOwnerIdRequest(client.getSrvId(), client.getUsrId(), msg, false);
            }

            return response;

        } catch (MissingPermissionException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing service '%s' request because client missing permissions", client.getClientId()));
            return e.getMessage();
        }
    }


    // Cloud requests

    /**
     * {@inheritDoc}
     */
    @Override
    public String processCloudRequest(String msg) {
        log.trace(Mrk_JOD.JOD_COMM, "Process cloud request");

        String response = null;

        // Object structure request
        if (JOSPProtocol_CloudRequests.isObjectStructureRequest(msg)) {
            log.info(Mrk_JOD.JOD_COMM, "Elaborate ObjectStructureRequest request from cloud");
            return processObjectStructureRequest(msg);
        }

        if (JOSPProtocol_CloudRequests.isToObjectRequest(msg)) {
            String srvId;
            String usrId;
            try {
                srvId = JOSPProtocol_CloudRequests.extractServiceIdFromRequest(msg);
                usrId = JOSPProtocol_CloudRequests.extractUserIdFromRequest(msg);

            } catch (JOSPProtocol.ParsingException e) {
                log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing cloud request from service because can't parse ids: %s", e.getMessage()));
                return e.getMessage();
            }

            try {
                // Object name request
                if (JOSPProtocol_CloudRequests.isObjectSetNameRequest(msg)) {
                    log.info(Mrk_JOD.JOD_COMM, String.format("Elaborate ObjectSetNameRequest request for service '%s' and user '%s'", srvId, usrId));
                    response = processObjectSetNameRequest(srvId, usrId, msg, true);
                }

                // Object owner id request
                if (JOSPProtocol_CloudRequests.isObjectSetOwnerIdRequest(msg)) {
                    log.info(Mrk_JOD.JOD_COMM, String.format("Elaborate ObjectSetOwnerIdRequest request for service '%s' and user '%s'", srvId, usrId));
                    response = processObjectSetOwnerIdRequest(srvId, usrId, msg, true);
                }

            } catch (MissingPermissionException e) {
                log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing cloud request from service '%s' and user '%s' because client missing permissions", srvId, usrId));
                return e.getMessage();
            }
        }

        return response;
    }


    // Request processors

    private String processObjectInfoRequest(String srvId, String usrId, String msg) throws MissingPermissionException {
        log.debug(Mrk_JOD.JOD_COMM, "Processing service request ObjectInfoRequest");
        checkRequest_ReadPermission(srvId, usrId, false);
        String response = JOSPProtocol_ServiceRequests.createObjectInfoResponse(objInfo.getObjId(), objInfo.getObjName(), objInfo.getOwnerId(), objInfo.getJODVersion());
        log.debug(Mrk_JOD.JOD_COMM, "Service request ObjectInfoRequest processed");
        return response;
    }

    private String processObjectStructureRequest(String msg) {
        try {
            return processObjectStructureRequest(null, null, msg);
        } catch (MissingPermissionException ignore) {/*Request from cloud, no permission checks*/}
        return null;
    }

    private String processObjectStructureRequest(String srvId, String usrId, String msg) throws MissingPermissionException {
        boolean fromCloud = srvId == null;
        if (fromCloud)
            log.debug(Mrk_JOD.JOD_COMM, "Processing cloud request ObjectStructureRequest");
        else {
            log.debug(Mrk_JOD.JOD_COMM, "Processing service request ObjectStructureRequest");
            checkRequest_ReadPermission(srvId, usrId, false);
        }

        try {
            String structStr = structure.getStringForJSL();
            String response;
            if (fromCloud) {
                response = JOSPProtocol_CloudRequests.createObjectStructureResponse(objInfo.getObjId(), structure.getLastStructureUpdate(), structStr, true);
                log.debug(Mrk_JOD.JOD_COMM, "Cloud request ObjectStructureRequest processed");
            } else {
                response = JOSPProtocol_ServiceRequests.createObjectStructureResponse(objInfo.getObjId(), structure.getLastStructureUpdate(), structStr);
                log.debug(Mrk_JOD.JOD_COMM, String.format("Service '%s' and user '%s' request ObjectStructureRequest processed", srvId, usrId));
            }
            return response;

        } catch (JODStructure.ParsingException e) {
            if (fromCloud)
                log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing cloud request ObjectStructureRequest because %s", e.getMessage()), e);
            else
                log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing service '%s' and user '%s' request ObjectStructureRequest because %s", srvId, usrId, e.getMessage()), e);
            return String.format("[%s] %s\n%s", e.getClass().getSimpleName(), e.getMessage(), Arrays.toString(e.getStackTrace()));
        }
    }

    private String processObjectSetNameRequest(String srvId, String usrId, String msg, boolean fromCloud) throws MissingPermissionException {
        String origin = fromCloud ? "cloud" : "service";
        log.debug(Mrk_JOD.JOD_COMM, String.format("Processing %s request ObjectSetNameRequest", origin));
        checkRequest_OwnerPermission(srvId, usrId, fromCloud);

        String newName;
        try {
            if (fromCloud)
                newName = JOSPProtocol_CloudRequests.extractObjectSetNameNameFromRequest(msg);
            else
                newName = JOSPProtocol_ServiceRequests.extractObjectSetNameNameFromRequest(msg);

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing %s '%s' and user '%s' request ObjectSetNameRequest because %s", origin, srvId, usrId, e.getMessage()), e);
            return "";
        }

        objInfo.setObjName(newName);
        log.debug(Mrk_JOD.JOD_COMM, String.format("Service '%s' and user '%s' request ObjectSetNameRequest processed", srvId, usrId));
        return "";
    }

    private String processObjectSetOwnerIdRequest(String srvId, String usrId, String msg, boolean fromCloud) throws MissingPermissionException {
        String origin = fromCloud ? "cloud" : "service";
        log.debug(Mrk_JOD.JOD_COMM, String.format("Processing %s request ObjectSetOwnerIdRequest", origin));
        checkRequest_OwnerPermission(srvId, usrId, fromCloud);

        String newOwnerId;
        try {
            if (fromCloud)
                newOwnerId = JOSPProtocol_CloudRequests.extractObjectSetOwnerIdOwnerIdFromRequest(msg);
            else
                newOwnerId = JOSPProtocol_ServiceRequests.extractObjectSetOwnerIdOwnerIdFromRequest(msg);

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing %s '%s' and user '%s' request ObjectSetOwnerIdRequest because %s", origin, srvId, usrId, e.getMessage()), e);
            return "";
        }

        permissions.setOwnerId(newOwnerId);
        log.debug(Mrk_JOD.JOD_COMM, String.format("Service '%s' and user '%s' request ObjectSetOwnerIdRequest processed", srvId, usrId));
        return "";
    }


    // Request permissions

    private void checkRequest_ReadPermission(String srvId, String usrId, boolean fromCloud) throws MissingPermissionException {
        if (!permissions.canSendUpdate(srvId, usrId, fromCloud ? PermissionsTypes.Connection.LocalAndCloud : PermissionsTypes.Connection.OnlyLocal))
            throw new MissingPermissionException("LocalServiceRequest", srvId, usrId);
    }

    private void checkRequest_OwnerPermission(String srvId, String usrId, boolean fromCloud) throws MissingPermissionException {
        if (!permissions.canActAsCoOwner(srvId, usrId, fromCloud ? PermissionsTypes.Connection.LocalAndCloud : PermissionsTypes.Connection.OnlyLocal))
            throw new MissingPermissionException("LocalServiceRequest", srvId, usrId);
    }


    // Connections access

    /**
     * {@inheritDoc}
     */
    @Override
    public JODGwO2SClient getGwO2SClient() {
        return gwClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JODLocalClientInfo> getAllLocalClientsInfo() {
        return localServer == null ? new ArrayList<>() : localServer.getLocalClientsInfo();
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
        log.info(Mrk_JOD.JOD_COMM, String.format("Start and publish local object's server '%s'", objInfo.getObjId()));

        if (isLocalRunning())
            return;

        try {
            log.debug(Mrk_JOD.JOD_COMM, "Starting local object's server");
            localServer = initLocalServer();
            localServer.start();
            log.debug(Mrk_JOD.JOD_COMM, "Local object's server started");
            log.debug(Mrk_JOD.JOD_COMM, "Publishing local object's server");
            localServerPublisher.publish(true);
            log.debug(Mrk_JOD.JOD_COMM, "Local object's server published");

        } catch (Server.ListeningException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on starting local communication object's server '%s' because %s", objInfo.getObjId(), e.getMessage()), e);
            throw new LocalCommunicationException(String.format("Error on starting local communication object's server '%s'", objInfo.getObjId()), e);

        } catch (Publisher.PublishException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on publishing local communication object's server '%s' because %s", objInfo.getObjId(), e.getMessage()), e);
            throw new LocalCommunicationException(String.format("Error on publishing local communication object's server '%s'", objInfo.getObjId()), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopLocal() throws LocalCommunicationException {
        log.info(Mrk_JOD.JOD_COMM, String.format("Stop and hide local object's server '%s' on port '%d'", objInfo.getObjId(), localServer.getPort()));

        if (!isLocalRunning())
            return;

        try {
            log.debug(Mrk_JOD.JOD_COMM, "Stopping local object's server");
            localServer.stop();
            log.debug(Mrk_JOD.JOD_COMM, "Local object's server stopped");
            log.debug(Mrk_JOD.JOD_COMM, "Hiding local object's server");
            localServerPublisher.hide(true);
            log.debug(Mrk_JOD.JOD_COMM, "Local object's server hided");

        } catch (Publisher.PublishException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on hiding local communication object's server '%s' because %s", objInfo.getObjId(), e.getMessage()), e);
            throw new LocalCommunicationException(String.format("Error on hiding local communication object's server '%s'", objInfo.getObjId()), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCloudConnected() {
        return gwClient.isConnected();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectCloud() throws CloudCommunicationException {
        log.info(Mrk_JOD.JOD_COMM, String.format("Connect cloud communication client for '%s' object", objInfo.getObjId()));

        if (isCloudConnected())
            return;

        try {
            gwClient = new JODGwO2SClient(locSettings, this, objInfo, jcpClient, jcpComm);
            log.debug(Mrk_JOD.JOD_COMM, "Connecting cloud object's client");
            gwClient.connect();
            if (gwClient.isConnected())
                log.debug(Mrk_JOD.JOD_COMM, "Cloud object's client connected");
            else
                log.warn(Mrk_JOD.JOD_COMM, "Cloud object's client NOT connected");

        } catch (Client.ConnectionException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on connecting cloud communication client for '%s' object because %s", objInfo.getObjId(), e.getMessage()), e);
            throw new CloudCommunicationException(String.format("Error on connecting cloud communication client for '%s' object", objInfo.getObjId()), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnectCloud() {
        log.info(Mrk_JOD.JOD_COMM, String.format("Disconnect cloud communication client for '%s' object", objInfo.getObjId()));

        if (!isCloudConnected())
            return;

        gwClient.disconnect();
    }


    // Cross component references

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStructure(JODStructure structure) throws StructureSetException {
        if (this.structure != null)
            throw new StructureSetException();
        this.structure = structure;
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

}
