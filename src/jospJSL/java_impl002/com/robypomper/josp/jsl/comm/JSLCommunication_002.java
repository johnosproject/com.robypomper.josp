package com.robypomper.josp.jsl.comm;

import com.robypomper.communication.client.Client;
import com.robypomper.discovery.Discover;
import com.robypomper.discovery.DiscoverListener;
import com.robypomper.discovery.DiscoverySystemFactory;
import com.robypomper.discovery.impl.DiscoveryJmDNS;
import com.robypomper.josp.jsl.JSL_002;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLAction;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Implementation of the {@link JSLCommunication} interface.
 */
public class JSLCommunication_002 implements JSLCommunication, DiscoverListener {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JSL_002.Settings settings;
    private final JSLServiceInfo srvInfo;
    private final JSLUserMngr usr;
    private final JSLObjsMngr objs;
    private final JCPClient_Service jcpClient;
    private final String discoveryImpl;
    private final Discover localServerDiscovery;
    private final List<JSLLocalClient> localServers = new ArrayList<>();
    private final JSLGwS2OClient gwClient;


    // Constructor

    /**
     * @param settings  the JSL settings.
     * @param srvInfo   the service's info.
     * @param jcpClient the jcp service client.
     * @param usr       the {@link JSLUserMngr} instance used get user info.
     * @param objs      the {@link JSLObjsMngr} instance used to update component
     *                  status.
     */
    public JSLCommunication_002(JSL_002.Settings settings, JSLServiceInfo srvInfo, JCPClient_Service jcpClient, JSLUserMngr usr, JSLObjsMngr objs) throws LocalCommunicationException {
        this.settings = settings;
        this.srvInfo = srvInfo;
        this.usr = usr;
        this.objs = objs;
        this.jcpClient = jcpClient;

        // Init local service client and discovery
        discoveryImpl = settings.getJSLDiscovery();
        try {
            log.debug(Mrk_JSL.JSL_COMM, String.format("Creating discovery '%s' service for local object's servers", discoveryImpl));
            // If required JmDNS implementation, start his sub-system
            if (DiscoveryJmDNS.IMPL_NAME.equalsIgnoreCase(discoveryImpl)) {
                log.trace(Mrk_JSL.JSL_COMM, "Communication initializing discovery subsystem on JmDNS");
                DiscoveryJmDNS.startJmDNSSubSystem();
            }
            localServerDiscovery = DiscoverySystemFactory.createDiscover(discoveryImpl, JOSPProtocol.DISCOVERY_TYPE);
            log.debug(Mrk_JSL.JSL_COMM, String.format("Discovery '%s' service created for local object's servers", discoveryImpl));

        } catch (Discover.DiscoveryException e) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on creating discovery '%s' service for local object's servers because %s", discoveryImpl, e.getMessage()), e);
            throw new LocalCommunicationException(String.format("Error on creating discovery '%s' service for local object's servers", discoveryImpl), e);
        }

        // Init cloud service client
        InetAddress cloudAddr = InetAddress.getLoopbackAddress();           // from APIs
        int cloudPort = 9014;                                               // from APIs
        log.debug(Mrk_JSL.JSL_COMM, "Creating communication cloud client for Service2Object Gateway");
        String cloudClientPubCertFile = settings.getCloudClientPublicCertificate();
        String cloudServerPubCertFile = settings.getCloudServerPublicCertificate();
        log.trace(Mrk_JSL.JSL_COMM, String.format("Service2Object Gateway client use local certificate file '%s'", cloudClientPubCertFile));
        log.trace(Mrk_JSL.JSL_COMM, String.format("Service2Object Gateway client use public cloud certificate file '%s'", cloudServerPubCertFile));
        log.debug(Mrk_JSL.JSL_COMM, String.format("Service2Object Gateway client use address '%s:%d'", cloudAddr, cloudPort));
        gwClient = new JSLGwS2OClient(this, srvInfo, cloudAddr, cloudPort, cloudClientPubCertFile, cloudServerPubCertFile);
        log.debug(Mrk_JSL.JSL_COMM, "Communication cloud client created for Service2Object Gateway");

        log.info(Mrk_JSL.JSL_COMM, String.format("Initialized JODCommunication instance for '%s' ('%s') service", srvInfo.getSrvName(), srvInfo.getSrvId()));
    }


    // Action cmd flow (objMng - comm)

    /**
     * {@inheritDoc}
     */
    @Override
    public void forwardAction(JSLRemoteObject object, JSLAction component/*, JSLActionCommand command*/) {
        log.info(Mrk_JSL.JSL_COMM, String.format("Forward action for component '%s' of '%s' object", component.getName(), object.getId()));

        String msg = JOSPProtocol.fromCmdToMsg(srvInfo.getFullId(), ""/*component.getPath().getString()/*, JSLActionCommand command*/);

        // Send via cloud communication
        // ToDo: implements forward action to cloud communication

        // Send via local communication
        JSLLocalClient objectServer = object.getConnectedClient();
        if (objectServer.isConnected())
            try {
                objectServer.sendData(msg);

            } catch (Client.ServerNotConnectedException e) {
                log.warn(Mrk_JSL.JSL_COMM, String.format("Error on sending action for component '%s' of '%s' object to local client '%s' because %s", component.getName(), object.getId(), objectServer.getClientId(), e.getMessage()), e);
            }
    }


    // Connections access

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLGwS2OClient getCloudConnection() {
        return gwClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JSLLocalClient> getAllLocalServers() {
        return Collections.unmodifiableList(localServers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeServer(JSLLocalClient server) {
        localServers.remove(server);
    }


    // Mngm methods

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLocalRunning() {
        return localServerDiscovery != null && localServerDiscovery.isRunning();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startLocal() throws LocalCommunicationException {
        log.info(Mrk_JSL.JSL_COMM, String.format("Start local service's discovery '%s'", srvInfo.getSrvId()));

        if (isLocalRunning())
            return;

        try {
            localServerDiscovery.addListener(this);
            log.debug(Mrk_JSL.JSL_COMM, "Starting local service's discovery");
            localServerDiscovery.start();
            log.debug(Mrk_JSL.JSL_COMM, "Local service's discovery started");

        } catch (Discover.DiscoveryException e) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on starting local communication service's discovery '%s' because %s", srvInfo.getSrvId(), e.getMessage()), e);
            throw new LocalCommunicationException(String.format("Error on starting local communication service's discovery '%s'", srvInfo.getSrvId()), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopLocal() throws LocalCommunicationException {
        log.info(Mrk_JSL.JSL_COMM, String.format("Stop local communication service's discovery '%s' and disconnect local clients", srvInfo.getSrvId()));

        if (!isLocalRunning())
            return;

        try {
            log.debug(Mrk_JSL.JSL_COMM, "Stopping local service's discovery");
            localServerDiscovery.stop();
            log.debug(Mrk_JSL.JSL_COMM, "Local service's discovery stopped");
            localServerDiscovery.removeListener(this);

        } catch (Discover.DiscoveryException e) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on stopping local communication service's discovery '%s' because %s", srvInfo.getSrvId(), e.getMessage()), e);
            throw new LocalCommunicationException(String.format("Error on stopping local communication service's discovery '%s' system '%s'", srvInfo.getSrvId(), discoveryImpl), e);
        }

        log.debug(Mrk_JSL.JSL_COMM, "Disconnecting local communication service's clients");
        List<JSLLocalClient> connections = new ArrayList<>(localServers);
        for (JSLLocalClient locConn : connections)
            if (locConn.isConnected())
                locConn.disconnect();
        log.debug(Mrk_JSL.JSL_COMM, "Local communication service's clients disconnected");
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
        log.info(Mrk_JSL.JSL_COMM, String.format("Connect cloud communication client for '%s' service", srvInfo.getSrvId()));

        if (isCloudConnected())
            return;

        try {
            log.debug(Mrk_JSL.JSL_COMM, "Connecting cloud service's client");
            gwClient.connect();
            log.debug(Mrk_JSL.JSL_COMM, "Cloud service's client connected");

        } catch (Client.ConnectionException e) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on connecting cloud communication client because %s", e.getMessage()), e);
            throw new CloudCommunicationException("error on cloud connection", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnectCloud() {
        log.info(Mrk_JSL.JSL_COMM, String.format("Disconnect cloud communication client for '%s' service", srvInfo.getSrvId()));

        if (!isCloudConnected())
            return;

        gwClient.disconnect();
    }


    // Local communication discovery

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServiceDiscovered(String type, String name, InetAddress address, int port, String extraText) {
        Thread.currentThread().setName("JSLDiscovery");
        log.info(Mrk_JSL.JSL_COMM, String.format("Discover object's service '%s' at '%s:%d' by '%s' service", name, address, port, srvInfo.getSrvId()));

        if (address instanceof Inet6Address) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Object's service '%s' at '%s:%d' use IPv6 then discarded", name, address, port));
            return;
        }

        String ksFile = settings.getLocalClientKeyStore();
        String ksPass = settings.getLocalClientKeyStorePass();

        JSLLocalClient locConn;
        try {
            log.debug(Mrk_JSL.JSL_COMM, String.format("Connecting to '%s' object on server '%s:%d' from '%s' service", name, address, port, srvInfo.getSrvId()));
            String clientPubCertFile = File.createTempFile(String.format("jslCert-%s-%d", address, port), ".crt").getAbsolutePath();
            log.trace(Mrk_JSL.JSL_COMM, String.format("Local service's client use local key store file '%s'", ksFile));
            log.trace(Mrk_JSL.JSL_COMM, String.format("Local service's client use public certificate file '%s'", clientPubCertFile));
            locConn = new JSLLocalClient(this, srvInfo.getFullId(), address, port, ksFile, ksPass, clientPubCertFile);
            locConn.connect();
            log.debug(Mrk_JSL.JSL_COMM, String.format("Service connected to '%s' object on server '%s:%d' from '%s' service", name, address, port, srvInfo.getSrvId()));

        } catch (IOException | Client.ConnectionException e) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on connecting to '%s' object on server '%s:%d' from '%s' service because %s", name, address, port, srvInfo.getSrvId(), e.getMessage()), e);
            return;
        }

        log.debug(Mrk_JSL.JSL_COMM, String.format("Checking object '%s' connection from '%s' service already known", locConn.getServerInfo().getServerId(), srvInfo.getSrvId()));
        for (JSLLocalClient cl : localServers)
            if (cl.getServerAddr().equals(locConn.getServerAddr())
                    && cl.getServerPort() == locConn.getServerPort()
                    && cl.getClientAddr().equals(locConn.getClientAddr())
                    && cl.getClientPort() == locConn.getClientPort()
            ) {
                log.debug(Mrk_JSL.JSL_COMM, String.format("Connection already known to '%s' object on server '%s:%d' from '%s' service's '%s:%d' client", name, address, port, srvInfo.getSrvId(), locConn.getClientAddr(), locConn.getClientPort()));
                return;
            }
        log.debug(Mrk_JSL.JSL_COMM, String.format("Connection NOT known to '%s' object on server '%s:%d' from '%s' service's '%s:%d' client", name, address, port, srvInfo.getSrvId(), locConn.getClientAddr(), locConn.getClientPort()));

        localServers.add(locConn);
        objs.addNewConnection(locConn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServiceLost(String type, String name) {}

}