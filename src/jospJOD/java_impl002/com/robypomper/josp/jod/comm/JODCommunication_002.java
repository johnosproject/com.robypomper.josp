package com.robypomper.josp.jod.comm;

import com.robypomper.communication.client.Client;
import com.robypomper.communication.server.Server;
import com.robypomper.discovery.DiscoverySystemFactory;
import com.robypomper.discovery.Publisher;
import com.robypomper.discovery.impl.DiscoveryJmDNS;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.JODStateUpdate;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_ServiceRequests;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private final JODLocalServer localServer;
    private final Publisher localServerPublisher;
    private final JODGwO2SClient gwClient;


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

        // Init local object server
        log.debug(Mrk_JOD.JOD_COMM, "Initializing communication local object's server");
        int localPort = locSettings.getLocalServerPort();
        String localKsFile = locSettings.getLocalServerKeyStore();
        String localKsPass = locSettings.getLocalServerKeyStorePass();
        String localPubCertFile = locSettings.getLocalServerPublicCertificate();
        log.trace(Mrk_JOD.JOD_COMM, String.format("Local object's server use '%d' port", localPort));
        log.trace(Mrk_JOD.JOD_COMM, String.format("Local object's server use local key store file '%s'", localKsFile));
        log.trace(Mrk_JOD.JOD_COMM, String.format("Local object's server use public certificate file '%s'", localPubCertFile));
        localServer = new JODLocalServer(this, objInfo.getObjId(), localPort, localKsFile, localKsPass, localPubCertFile);
        log.debug(Mrk_JOD.JOD_COMM, "Communication local object's server initialized");

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
            localServerPublisher = DiscoverySystemFactory.createPublisher(publisherImpl, JOSPProtocol.DISCOVERY_TYPE, publisherSrvName, localPort, instanceId);
            log.debug(Mrk_JOD.JOD_COMM, String.format("Publisher '%s' service created for local object's server", publisherImpl));

        } catch (Publisher.PublishException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on creating publisher '%s' service for local object's server because %s", publisherImpl, e.getMessage()), e);
            throw new LocalCommunicationException(String.format("Error on creating publisher '%s' service for local object's server", publisherImpl), e);
        }

        // Init cloud object client
        try {
            log.debug(Mrk_JOD.JOD_COMM, "Creating communication cloud client for Object2Service Gateway");
            gwClient = new JODGwO2SClient(this, objInfo, jcpComm);
            log.debug(Mrk_JOD.JOD_COMM, "Communication cloud client created for Object2Service Gateway");
        } catch (CloudCommunicationException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on creating object's cloud client because %s", e.getMessage()), e);
            throw new CloudCommunicationException("Error on creating object's cloud client", e);
        }

        log.info(Mrk_JOD.JOD_COMM, String.format("Initialized JODCommunication instance for '%s' ('%s') object", objInfo.getObjName(), objInfo.getObjId()));
        log.debug(Mrk_JOD.JOD_COMM, String.format("                                    local server%s started and%s published", localServer.isRunning() ? "" : " NOT", localServerPublisher.isPublished() ? "" : " NOT"));
        log.debug(Mrk_JOD.JOD_COMM, String.format("                                    cloud client%s connected", gwClient.isConnected() ? "" : " NOT"));
    }


    // Status upd flow (objStruct - comm)

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispatchUpdate(JODState component, JODStateUpdate update) {
        log.info(Mrk_JOD.JOD_COMM, String.format("Dispatch update for component '%s'", component.getName()));

        String msg = JOSPProtocol.fromUpdToMsg(objInfo.getObjId(), component.getPath().getString()/*,update*/); // JODStateUpdate not in Commons

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
                if (locConn.isConnected() && permissions.canSendLocalUpdate(locConn.getUsrId(), locConn.getSrvId())) {
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
    public boolean forwardAction(String msg) {
        JOSPProtocol.ActionCmd cmd = JOSPProtocol.fromMsgToCmd(msg);
        if (cmd == null)
            return false;

        log.info(Mrk_JOD.JOD_COMM, String.format("Forward command to component '%s'", cmd.getComponentPath()));
        log.warn(Mrk_JOD.JOD_COMM, "Forward command to component not implemented");

        // ToDo: implement forwardAction(String) method
        // check service permission

        // search destination components

        // package params

        // exec component's action

        return false;
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
            String error = null;

            // Object info request
            if (JOSPProtocol_ServiceRequests.isObjectInfoRequest(msg)) {
                log.debug(Mrk_JOD.JOD_COMM, "Processing service request ObjectInfoRequest");
                log.info(Mrk_JOD.JOD_COMM, String.format("Elaborate ObjectInfoRequest request for service '%s'", client.getClientId()));
                checkServiceRequest_ReadPermission(client);
                response = JOSPProtocol_ServiceRequests.createObjectInfoResponse(objInfo.getObjId(), objInfo.getObjName(), objInfo.getOwnerId(), objInfo.getJODVersion());
                log.debug(Mrk_JOD.JOD_COMM, "Service request ObjectInfoRequest processed");
            }

            // Object structure request
            if (JOSPProtocol_ServiceRequests.isObjectStructureRequest(msg)) {
                log.debug(Mrk_JOD.JOD_COMM, "Processing service request ObjectStructureRequest");
                log.info(Mrk_JOD.JOD_COMM, String.format("Elaborate ObjectStructureRequest request for service '%s'", client.getClientId()));
                checkServiceRequest_ReadPermission(client);
                try {
                    String structStr = structure.getStringForJSL();
                    response = JOSPProtocol_ServiceRequests.createObjectStructureResponse(objInfo.getObjId(), structure.getLastStructureUpdate(), structStr);
                    log.debug(Mrk_JOD.JOD_COMM, String.format("Service '%s' request ObjectStructureRequest processed", client.getClientId()));

                } catch (JODStructure.ParsingException e) {
                    log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing service '%s' request ObjectStructureRequest because %s", client.getClientId(), e.getMessage()), e);
                    error = String.format("[%s] %s\n%s", e.getClass().getSimpleName(), e.getMessage(), Arrays.toString(e.getStackTrace()));
                }
            }

            return response != null ? response : error;

        } catch (MissingPermissionException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing service '%s' request because client missing permissions", client.getClientId()), e);
            return e.getMessage();
        }
    }

    /**
     * This method check if given <code>client</code> can send local service
     * requests about object info (read permission).
     * <p>
     * Local service have "read permissions" on current object if it has the
     * permission at least to receive status updates.
     * <p>
     * If the client (srv+usr) don't have the permission to send requests, then
     * a MissingPermissionException is thrown. Otherwise this method dose nothing.
     *
     * @param client the sender JSL info.
     */
    private void checkServiceRequest_ReadPermission(JODLocalClientInfo client) throws MissingPermissionException {
        if (!permissions.canSendLocalUpdate(client.getSrvId(), client.getUsrId()))
            throw new MissingPermissionException("LocalServiceRequest", client.getSrvId(), client.getUsrId());
    }

    /**
     * This method check if given <code>client</code> can send local service
     * requests about object settings (coOwner permission).
     * <p>
     * If the client (srv+usr) don't have the permission to send requests, then
     * a MissingPermissionException is thrown. Otherwise this method dose nothing.
     *
     * @param client the sender JSL info.
     */
    private void checkServiceRequest_OwnerPermission(JODLocalClientInfo client) throws MissingPermissionException {
        if (!permissions.canActAsLocalCoOwner(client.getSrvId(), client.getUsrId()))
            throw new MissingPermissionException("LocalServiceRequest", client.getSrvId(), client.getUsrId());
    }


    // Cloud requests

    /**
     * {@inheritDoc}
     */
    @Override
    public String processCloudRequest(String msg) {
        log.warn(Mrk_JOD.JOD_COMM, "Process cloud request not implemented");
        return "Not implemented";

//        log.trace(Mrk_JOD.JOD_COMM, String.format("Process service request from '%s' service", client.getClientId()));
//
//        try {
//            String response = null;
//            String error = null;
//
//            // Object info request
//            if (JOSPProtocol_ServiceRequests.isObjectInfoRequest(msg)) {
//                log.debug(Mrk_JOD.JOD_COMM, "Processing service request ObjectInfoRequest");
//                log.info(Mrk_JOD.JOD_COMM, String.format("Elaborate ObjectInfoRequest request for service '%s'", client.getClientId()));
//                checkServiceRequest_ReadPermission(client);
//                response = JOSPProtocol_ServiceRequests.createObjectInfoResponse(objInfo.getObjId(), objInfo.getObjName(), objInfo.getOwnerId(), objInfo.getJODVersion());
//                log.debug(Mrk_JOD.JOD_COMM, "Service request ObjectInfoRequest processed");
//            }
//
//            // Object structure request
//            if (JOSPProtocol_ServiceRequests.isObjectStructureRequest(msg)) {
//                log.debug(Mrk_JOD.JOD_COMM, "Processing service request ObjectStructureRequest");
//                log.info(Mrk_JOD.JOD_COMM, String.format("Elaborate ObjectStructureRequest request for service '%s'", client.getClientId()));
//                checkServiceRequest_ReadPermission(client);
//                try {
//                    String structStr = structure.getStringForJSL();
//                    response = JOSPProtocol_ServiceRequests.createObjectStructureResponse(objInfo.getObjId(), structure.getLastStructureUpdate(), structStr);
//                    log.debug(Mrk_JOD.JOD_COMM, String.format("Service '%s' request ObjectStructureRequest processed", client.getClientId()));
//
//                } catch (JODStructure.ParsingException e) {
//                    log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing service '%s' request ObjectStructureRequest because %s", client.getClientId(), e.getMessage()), e);
//                    error = String.format("[%s] %s\n%s", e.getClass().getSimpleName(), e.getMessage(), Arrays.toString(e.getStackTrace()));
//                }
//            }
//
//            return response != null ? response : error;
//
//        } catch (MissingPermissionException e) {
//            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing service '%s' request because client missing permissions", client.getClientId()), e);
//            return e.getMessage();
//        }
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
        return localServer.getLocalClientsInfo();
    }


    // Mngm methods

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLocalRunning() {
        return localServer.isRunning();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startLocal() throws LocalCommunicationException {
        log.info(Mrk_JOD.JOD_COMM, String.format("Start and publish local object's server '%s' on port '%d'", objInfo.getObjId(), localServer.getPort()));

        if (isLocalRunning())
            return;

        try {
            log.debug(Mrk_JOD.JOD_COMM, "Starting local object's server");
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
            log.debug(Mrk_JOD.JOD_COMM, "Connecting cloud object's client");
            gwClient.connect();
            log.debug(Mrk_JOD.JOD_COMM, "Cloud object's client connected");

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
