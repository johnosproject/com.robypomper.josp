package com.robypomper.josp.jsl.comm;

import com.robypomper.communication.client.Client;
import com.robypomper.discovery.Discover;
import com.robypomper.discovery.DiscoverListener;
import com.robypomper.discovery.DiscoverySystemFactory;
import com.robypomper.discovery.impl.DiscoveryJmDNS;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import com.robypomper.log.Mrk_JOD;
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
    private final JSLSettings_002 locSettings;
    private final JSLServiceInfo srvInfo;
    private final JSLUserMngr usr;
    private final JSLObjsMngr objs;
    private final JCPClient_Service jcpClient;
    private final JCPCommSrv jcpComm;
    private final String instanceId;
    private final Discover localServerDiscovery;
    private final List<JSLLocalClient> localServers = new ArrayList<>();
    private JSLGwS2OClient gwClient;
    private final List<CommunicationListener> listeners = new ArrayList<>();


    // Constructor

    /**
     * @param settings  the JSL settings.
     * @param srvInfo   the service's info.
     * @param jcpClient the jcp service client.
     * @param usr       the {@link JSLUserMngr} instance used get user info.
     * @param objs      the {@link JSLObjsMngr} instance used to update component
     *                  status.
     */
    public JSLCommunication_002(JSLSettings_002 settings, JSLServiceInfo srvInfo, JCPClient_Service jcpClient, JSLUserMngr usr, JSLObjsMngr objs, String instanceId) throws LocalCommunicationException, CloudCommunicationException {
        this.locSettings = settings;
        this.srvInfo = srvInfo;
        this.jcpClient = jcpClient;
        this.jcpComm = new JCPCommSrv(jcpClient, settings, instanceId);
        this.usr = usr;
        this.objs = objs;
        this.instanceId = instanceId;

        // Init local service client and discovery
        String discoveryImpl = locSettings.getJSLDiscovery();
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
        try {
            log.debug(Mrk_JOD.JOD_COMM, "Creating communication cloud client for Object2Service Gateway");
            gwClient = new JSLGwS2OClient(locSettings, this, srvInfo, jcpClient, jcpComm);
            log.debug(Mrk_JOD.JOD_COMM, "Communication cloud client created for Object2Service Gateway");
        } catch (CloudCommunicationException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on creating service's cloud client because %s", e.getMessage()), e);
            throw new CloudCommunicationException("Error on creating service's cloud client", e);
        }

        log.info(Mrk_JSL.JSL_COMM, String.format("Initialized JODCommunication instance for '%s' ('%s') service", srvInfo.getSrvName(), srvInfo.getSrvId()));
    }


    // To Object Msg

    // see JSLRemoteObject


    // From Object Msg

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean processFromObjectMsg(String msg, JOSPPerm.Connection connType) {
        log.info(Mrk_JSL.JSL_COMM, String.format("Received '%s' message from %s", msg.substring(0, msg.indexOf('\n')), connType == JOSPPerm.Connection.OnlyLocal ? "local object" : "cloud"));

        try {
            String objId = JOSPProtocol_ObjectToService.getObjId(msg);

            JSLRemoteObject obj = objs.getById(objId);
            int count = 0;
            while (obj == null && count < 5) {
                count++;
                Thread.sleep(100);
                obj = objs.getById(objId);
            }

            if (obj == null && connType == JOSPPerm.Connection.LocalAndCloud && JOSPProtocol_ObjectToService.isObjectInfoMsg(msg)) {
                objs.addCloudObject(objId);
                obj = objs.getById(objId);
            }

            if (obj == null)
                throw new Throwable(String.format("Object '%s' not found", objId));//throw new ObjectNotFound(objId)

            if (!obj.processFromObjectMsg(msg, connType))
                throw new Throwable(String.format("Error on processing '%s' message", msg.substring(0, msg.indexOf('\n'))));

            log.info(Mrk_JSL.JSL_COMM, String.format("Message '%s' processed successfully", msg.substring(0, msg.indexOf('\n'))));
            return true;

        } catch (Throwable t) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on processing '%s' message from %s because %s", msg.substring(0, msg.indexOf('\n')), connType == JOSPPerm.Connection.OnlyLocal ? "local object" : "cloud", t.getMessage()), t);
            return false;
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
        return localServerDiscovery.isRunning();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startLocal() throws LocalCommunicationException {
        log.info(Mrk_JSL.JSL_COMM, String.format("Start local service's discovery '%s'", srvInfo.getSrvId()));

        if (isLocalRunning())
            return;

//        String discoveryImpl = locSettings.getJSLDiscovery();
        try {
//            localServerDiscovery = DiscoverySystemFactory.createDiscover(discoveryImpl, JOSPProtocol.DISCOVERY_TYPE);
            localServerDiscovery.addListener(this);
            log.debug(Mrk_JSL.JSL_COMM, "Starting local service's discovery");
            localServerDiscovery.start();
            log.debug(Mrk_JSL.JSL_COMM, "Local service's discovery started");
            emit_LocalStarted();

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
            emit_LocalStopped();

        } catch (Discover.DiscoveryException e) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on stopping local communication service's discovery '%s' because %s", srvInfo.getSrvId(), e.getMessage()), e);
            throw new LocalCommunicationException(String.format("Error on stopping local communication service's discovery '%s' system '%s'", srvInfo.getSrvId(), locSettings.getJSLDiscovery()), e);
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
            gwClient = new JSLGwS2OClient(locSettings, this, srvInfo, jcpClient, jcpComm);
            gwClient.connect();
            if (gwClient.isConnected()) {
                log.debug(Mrk_JSL.JSL_COMM, "Cloud service's client connected");
                emit_CloudConnected();
            } else
                log.warn(Mrk_JSL.JSL_COMM, "Cloud service's client NOT connected");

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
        emit_CloudDisconnected();
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

        JSLLocalClient locConn;
        try {
            log.debug(Mrk_JSL.JSL_COMM, String.format("Connecting to '%s' object on server '%s:%d' from '%s' service", name, address, port, srvInfo.getSrvId()));
            String clientPubCertFile = File.createTempFile(String.format("jslCert-%s-%d", address, port), ".crt").getAbsolutePath();
            log.trace(Mrk_JSL.JSL_COMM, String.format("Local service's client use public certificate file '%s'", clientPubCertFile));
            locConn = new JSLLocalClient(this, srvInfo.getFullId(), address, port, clientPubCertFile);
            locConn.connect();
            log.debug(Mrk_JSL.JSL_COMM, String.format("Service connected to '%s' object on server '%s:%d' from '%s' service", name, address, port, srvInfo.getSrvId()));

        } catch (IOException | Client.ConnectionException e) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on connecting to '%s' object on server '%s:%d' from '%s' service because %s", name, address, port, srvInfo.getSrvId(), e.getMessage()), e);
            return;
        }

        localServers.add(locConn);
        objs.addNewConnection(locConn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServiceLost(String type, String name) {}


    // Listeners connections

    public void addListener(CommunicationListener listener) {
        if (listeners.contains(listener))
            return;

        listeners.add(listener);
    }

    public void removeListener(CommunicationListener listener) {
        if (!listeners.contains(listener))
            return;

        listeners.remove(listener);
    }

    private void emit_CloudConnected() {
        // ToDo move emit_CloudConnected() calls to gwClient onConnected() event
        for (CommunicationListener l : listeners)
            l.onCloudConnected(this);
    }

    private void emit_CloudDisconnected() {
        // ToDo move emit_CloudDisconnected() calls to gwClient onDisconnected() event
        for (CommunicationListener l : listeners)
            l.onCloudDisconnected(this);
    }

    private void emit_LocalStarted() {
        for (CommunicationListener l : listeners)
            l.onLocalStarted(this);
    }

    private void emit_LocalStopped() {
        for (CommunicationListener l : listeners)
            l.onLocalStopped(this);
    }

}
