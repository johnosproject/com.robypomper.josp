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
import com.robypomper.josp.jod.structure.AbsJODAction;
import com.robypomper.josp.jod.structure.DefaultJODComponentPath;
import com.robypomper.josp.jod.structure.JODAction;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODComponentPath;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.JODStateUpdate;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.protocol.JOSPPermissions;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import com.robypomper.josp.protocol.JOSPProtocol_ServiceToObject;
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
            return new JODLocalServer(this, objInfo, localPort, localPubCertFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    // To Service Msg

    @Override
    public boolean sendToServices(String msg, JOSPPermissions.Type minReqPerm) {
        log.info(Mrk_JOD.JOD_COMM, String.format("Send '%s' message to local services and cloud", msg.substring(0, msg.indexOf('\n'))));

        // Send via local communication
        if (isLocalRunning()) {
            for (JODLocalClientInfo locConn : getAllLocalClientsInfo()) {
                if (!locConn.isConnected() || !checkRequest_ReadPermission(locConn.getSrvId(), locConn.getUsrId(), JOSPPermissions.Connection.OnlyLocal))
                    continue;

                try {
                    localServer.sendData(locConn.getClientId(), msg);

                } catch (Server.ServerStoppedException | Server.ClientNotFoundException | Server.ClientNotConnectedException ignore) {}
            }
        }

        // Send via cloud communication
        if (isCloudConnected()) {
            try {
                gwClient.sendData(msg);

            } catch (Client.ServerNotConnectedException e) {
                log.warn(Mrk_JOD.JOD_COMM, String.format("Error on sending message '%s' to service (via cloud) because %s", msg.substring(0, msg.indexOf('\n')), e.getMessage()), e);
            }
        }

        return isLocalRunning() || isCloudConnected();
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
    public boolean sendToSingleLocalService(JODLocalClientInfo locConn, String msg, JOSPPermissions.Type minReqPerm) throws ServiceNotConnected {
        log.info(Mrk_JOD.JOD_COMM, String.format("Send '%s' message to local service '%s' only", msg.substring(0, msg.indexOf('\n')), locConn.getFullSrvId()));

        if (!permissions.canSendUpdate(locConn.getSrvId(), locConn.getUsrId(), JOSPPermissions.Connection.OnlyLocal))
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
        sendToServices(msg, JOSPPermissions.Type.Status);
    }


    // From Service Msg

    @Override
    public boolean processFromServiceMsg(String msg, JOSPPermissions.Connection connType) {
        log.info(Mrk_JOD.JOD_COMM, String.format("Received '%s' message from %s", msg.substring(0, msg.indexOf('\n')), connType == JOSPPermissions.Connection.OnlyLocal ? "local service" : "cloud"));

        try {
            String srvId = JOSPProtocol_ServiceToObject.getSrvId(msg);
            String usrId = JOSPProtocol_ServiceToObject.getUsrId(msg);

            // dispatch to processor
            boolean processedSuccessfully = false;
            if (JOSPProtocol_ServiceToObject.isObjectSetNameMsg(msg))
                processedSuccessfully = checkRequest_OwnerPermission(srvId, usrId, connType) && processObjectSetNameMsg(msg);
            else if (JOSPProtocol_ServiceToObject.isObjectSetOwnerIdMsg(msg))
                processedSuccessfully = checkRequest_OwnerPermission(srvId, usrId, connType) && processObjectSetOwnerIdMsg(msg);
            else if (JOSPProtocol_ServiceToObject.isObjectActionCmdMsg(msg))
                processedSuccessfully = checkRequest_ActionPermission(srvId, usrId, connType) && processObjectCmdMsg(msg);
            else
                throw new Throwable(String.format("Error on processing '%s' message because unknown message type", msg.substring(0, msg.indexOf('\n'))));

            if (!processedSuccessfully)
                throw new Throwable(String.format("Error on processing '%s' message", msg.substring(0, msg.indexOf('\n'))));

            log.info(Mrk_JOD.JOD_COMM, String.format("Message '%s' processed successfully", msg.substring(0, msg.indexOf('\n'))));
            return true;

        } catch (Throwable t) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing '%s' message from %s because %s", msg.substring(0, msg.indexOf('\n')), connType == JOSPPermissions.Connection.OnlyLocal ? "local service" : "cloud", t.getMessage()), t);
            return false;
        }
    }

    private boolean processObjectCmdMsg(String msg) {
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
        if (!actionComp.execAction(cmd)) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing command on '%s' component", compPath.toString()));
            return false;
        }
        log.info(Mrk_JOD.JOD_COMM, String.format("Command status of '%s' component", compPath.toString()));

        log.debug(Mrk_JOD.JOD_COMM, String.format("Command '%s...' processed", msg.substring(0, Math.min(10, msg.length()))));
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


    // Request permissions

    private boolean checkRequest_ReadPermission(String srvId, String usrId, JOSPPermissions.Connection fromConnType) {
        return permissions.canSendUpdate(srvId, usrId, fromConnType);
    }

    private boolean checkRequest_ActionPermission(String srvId, String usrId, JOSPPermissions.Connection fromConnType) {
        return permissions.canExecuteAction(srvId, usrId, fromConnType);
    }

    private boolean checkRequest_OwnerPermission(String srvId, String usrId, JOSPPermissions.Connection fromConnType) {
        return permissions.canActAsCoOwner(srvId, usrId, fromConnType);
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
