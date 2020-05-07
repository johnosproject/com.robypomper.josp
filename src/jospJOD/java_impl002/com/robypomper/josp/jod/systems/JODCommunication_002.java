package com.robypomper.josp.jod.systems;

import com.robypomper.communication.client.Client;
import com.robypomper.communication.server.Server;
import com.robypomper.discovery.DiscoverySystemFactory;
import com.robypomper.discovery.Publisher;
import com.robypomper.discovery.impl.DiscoveryJmDNS;
import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jod.JOD_002;
import com.robypomper.josp.jod.comm.JODGwO2SClient;
import com.robypomper.josp.jod.comm.JODLocalClientInfo;
import com.robypomper.josp.jod.comm.JODLocalServer;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.JODStateUpdate;
import com.robypomper.josp.protocol.JOSPProtocol;

import java.net.InetAddress;
import java.util.List;


/**
 * Implementation of the {@link JODCommunication} interface.
 */
public class JODCommunication_002 implements JODCommunication {

    // Internal vars

    private final JOD_002.Settings locSettings;
    private final JODObjectInfo objInfo;
    private final JODPermissions permissions;
    private JODStructure structure;
    private final JODLocalServer localServer;
    private final Publisher localServerPublisher;
    private final JODGwO2SClient gwClient;
    private final String instanceId;


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
    public JODCommunication_002(JOD_002.Settings settings, JODObjectInfo objInfo, JCPClient jcpClient, JODPermissions permissions, String instanceId) throws LocalCommunicationException {
        System.out.println("DEB: JOD Communication initialization...");

        this.locSettings = settings;
        this.objInfo = objInfo;
        this.permissions = permissions;
        this.instanceId = instanceId;

        // Init local object server
        try {
            int localPort = locSettings.getLocalServerPort();
            String localKsFile = locSettings.getLocalServerKeyStore();
            String localKsPass = locSettings.getLocalServerKeyStorePass();
            String localPubCertFile = locSettings.getLocalServerPublicCertificate();
            localServer = new JODLocalServer(this, objInfo.getObjId(), localPort, localKsFile, localKsPass, localPubCertFile);
            System.out.println(String.format("DEB: JOD Local server initialized on '%d' port", localPort));

            String publisherImpl = locSettings.getLocalDiscovery();
            // If required JmDNS implementation, start his sub-system
            if (DiscoveryJmDNS.IMPL_NAME.equalsIgnoreCase(publisherImpl)) {
                DiscoveryJmDNS.startJmDNSSubSystem();
                System.out.println("DEB: Discovery system started JmDNSSubSystem");
            }
            String publisherSrvName = objInfo.getObjName() + "-" + instanceId;
            localServerPublisher = DiscoverySystemFactory.createPublisher(publisherImpl, JOSPProtocol.DISCOVERY_TYPE, publisherSrvName, localPort, instanceId);
            System.out.println(String.format("DEB: JOD Local server publisher initialized with '%s' service name", publisherSrvName));

        } catch (Publisher.PublishException e) {
            throw new LocalCommunicationException("Can't initialize local JOD server", e);
        }

        // Init cloud object client
        InetAddress cloudAddr = InetAddress.getLoopbackAddress();           // from APIs
        int cloudPort = 9014;                                               // from APIs
        String cloudClientPubCertFile = locSettings.getCloudClientPublicCertificate();
        String cloudServerPubCertFile = locSettings.getCloudServerPublicCertificate();
        gwClient = new JODGwO2SClient(this, objInfo, cloudAddr, cloudPort, cloudClientPubCertFile, cloudServerPubCertFile);
        System.out.println("DEB: JOD Cloud client initialized");

        System.out.println("DEB: JOD Communication initialized");
    }


    // Status upd flow (objStruct - comm)

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispatchUpdate(JODState component, JODStateUpdate update) {
        String msg = JOSPProtocol.fromUpdToMsg(objInfo.getObjId(), component.getPath().getString()/*,update*/);

        // send to cloud
        if (isCloudConnected()) {
            try {
                gwClient.sendData(msg);
            } catch (Client.ServerNotConnectedException e) {
                System.out.println(String.format("WAR: error on sending data to the cloud because '%s'", e.getMessage()));
            }
        }

        // send to allowed local services
        if (isLocalRunning()) {
            for (JODLocalClientInfo locConn : getAllLocalClientsInfo())
                if (locConn.isConnected() && permissions.canSendLocalUpdate(locConn.getUsrId(), locConn.getSrvId()))
                    try {
                        localServer.sendData(locConn.getClientId(), msg);
                    } catch (Server.ServerStoppedException | Server.ClientNotFoundException | Server.ClientNotConnectedException e) {
                        System.out.println(String.format("WAR: error on sending data to local client '%s' because '%s'", locConn.getClientId(), e.getMessage()));
                    }
        }
    }


    // Action cmd flow (comm - objStruct)

    /**
     * {@inheritDoc}
     */
    @Override
    public void forwardAction(String msg) {
        JOSPProtocol.ActionCmd cmd = JOSPProtocol.fromMsgToCmd(msg);

        // ToDo: implement forwardAction(String) method
        // check service permission

        // search destination components

        // package params

        // exec component's action

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
        if (isLocalRunning()) {
            System.out.println("WAR: Local communication already running.");
            return;
        }

        try {
            localServer.start();
            localServerPublisher.publish(true);

        } catch (Server.ListeningException | Publisher.PublishException e) {
            throw new LocalCommunicationException("Error on start local JOD server", e);
        }

        System.out.println("DEB: JOD Local communication started successfully.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopLocal() throws LocalCommunicationException {
        if (!isLocalRunning()) {
            System.out.println("WAR: Local communication already stopped.");
            return;
        }

        try {
            localServer.stop();
            localServerPublisher.hide(true);

        } catch (Publisher.PublishException e) {
            throw new LocalCommunicationException("Error on stop local object server.", e);
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
        if (isCloudConnected()) {
            System.out.println("WAR: Cloud communication already connected.");
            return;
        }

        try {
            gwClient.connect();
        } catch (Client.ConnectionException e) {
            throw new CloudCommunicationException("error on cloud connection", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnectCloud() {
        if (!isCloudConnected()) {
            System.out.println("WAR: Cloud communication already disconnected.");
            return;
        }

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
