package com.robypomper.josp.jsl.systems;

import com.robypomper.communication.client.Client;
import com.robypomper.discovery.Discover;
import com.robypomper.discovery.DiscoverListener;
import com.robypomper.discovery.DiscoverySystemFactory;
import com.robypomper.discovery.impl.DiscoveryJmDNS;
import com.robypomper.josp.jsl.JSL_002;
import com.robypomper.josp.jsl.comm.JSLGwS2OClient;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.protocol.JOSPProtocol;

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

    private final JSL_002.Settings settings;
    private final JSLServiceInfo srvInfo;
    private final JSLUserMngr usr;
    private final JSLObjsMngr objs;
    private final JCPClient_Service jcpClient;
    private final String discoveryImpl;
    private final Discover localServerDiscovery;
    private final List<JSLLocalClient> localClients = new ArrayList<>();
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
        System.out.println("DEB: JSL Communication initialization...");

        this.settings = settings;
        this.srvInfo = srvInfo;
        this.usr = usr;
        this.objs = objs;
        this.jcpClient = jcpClient;

        // Init local service client and discovery
        try {
            discoveryImpl = settings.getJSLDiscovery();
            // If required JmDNS implementation, start his sub-system
            if (DiscoveryJmDNS.IMPL_NAME.equalsIgnoreCase(discoveryImpl)) {
                DiscoveryJmDNS.startJmDNSSubSystem();
                System.out.println("DEB: Discovery system started JmDNSSubSystem");
            }
            localServerDiscovery = DiscoverySystemFactory.createDiscover(discoveryImpl, JOSPProtocol.DISCOVERY_TYPE);
            System.out.println("DEB: JOD Local server discoverer initialized");

        } catch (Discover.DiscoveryException e) {
            throw new LocalCommunicationException("Can't initialize local JOD server", e);
        }

        // Init cloud object client
        InetAddress cloudAddr = InetAddress.getLoopbackAddress();           // from APIs
        int cloudPort = 9014;                                               // from APIs
        String clientPubCertFile = settings.getCloudClientPublicCertificate();
        String cloudPubCertFile = settings.getCloudServerPublicCertificate();
        gwClient = new JSLGwS2OClient(this, srvInfo, cloudAddr, cloudPort, clientPubCertFile, cloudPubCertFile);
        System.out.println("DEB: JOD Cloud client initialized");

        System.out.println("DEB: JSL Communication initialized");
    }


    // Action cmd flow (objMng - comm)

    /**
     * {@inheritDoc}
     */
    @Override
    public void forwardAction(JSLRemoteObject object/*, JSLAction component, JSLActionCommand command*/) {
        String msg = JOSPProtocol.fromCmdToMsg(srvInfo.getFullId(), ""/*component.getPath().getString()/*, JSLActionCommand command*/);

        for (JSLLocalClient cl : object.getLocalClients())
            if (cl.isConnected()) {
                try {
                    cl.sendData(msg);
                    System.out.println(String.format("DEB: send action '%s' to object '%s'", msg, object.getId()));
                } catch (Client.ServerNotConnectedException e) {
                    System.out.println(String.format("WAR: error sending action '%s' to object '%s' because %s.", msg, object.getId(), e.getMessage()));
                }
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
    public List<JSLLocalClient> getAllLocalClients() {
        return Collections.unmodifiableList(localClients);
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
        if (isLocalRunning())
            System.out.println(String.format("DEB: '%s' discovery system already started", discoveryImpl));

        // Start discovery implementation
        localServerDiscovery.addListener(this);

        try {
            localServerDiscovery.start();
        } catch (Discover.DiscoveryException t) {
            throw new LocalCommunicationException(String.format("Can't start '%s' discovery system.", discoveryImpl), t);
        }

        System.out.println(String.format("INF: '%s' discovery system started successfully", discoveryImpl));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopLocal() throws LocalCommunicationException {
        if (!isLocalRunning()) {
            System.out.println(String.format("DEB: '%s' discovery system already stopped", discoveryImpl));
            return;
        }

        try {
            localServerDiscovery.stop();
        } catch (Discover.DiscoveryException t) {
            throw new LocalCommunicationException(String.format("Can't stop '%s' discovery system.", discoveryImpl), t);
        }

        localServerDiscovery.removeListener(this);
        System.out.println(String.format("INF: '%s' discovery system stopped successfully", discoveryImpl));

        for (JSLLocalClient locConn : localClients)
            if (locConn.isConnected())
                locConn.disconnect();
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


    // Local communication discovery

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServiceDiscovered(String type, String name, InetAddress address, int port, String extraText) {
        Thread.currentThread().setName("JSLDiscovery");

        System.out.println(String.format("DEB: discovered object at '%s:%d'.", address, port));
        if (address instanceof Inet6Address) {
            System.out.println(String.format("DEB: object at '%s:%d' use IPv6, discarded.", address, port));
            return;
        }

        String ksFile = settings.getLocalClientKeyStore();
        String ksPass = settings.getLocalClientKeyStorePass();

        JSLLocalClient locCli;
        try {
            String clientPubCertFile = File.createTempFile(String.format("jslCert-%s-%d", address, port), ".crt").getAbsolutePath();
            locCli = new JSLLocalClient(this, srvInfo.getFullId(), address, port, ksFile, ksPass, clientPubCertFile);
            locCli.connect();
        } catch (IOException | Client.ConnectionException e) {
            System.out.println(String.format("WAR: error on service discovered procedure on creating local connection because %s", e.getMessage()));
            return;
        }

        for (JSLLocalClient cl : localClients)
            if (cl.getServerAddr().equals(locCli.getServerAddr())
                    && cl.getServerPort() == locCli.getServerPort()
                    && cl.getObjId().equals(locCli.getObjId())) {

                JSLRemoteObject obj = objs.getById(cl.getObjId());
                if (obj != null && !obj.isConnected()) {
                    System.out.println(String.format("DEB: local connection to '%s' object already known but not connected, connect.", locCli.getObjId()));  // probably caused by a bad DNS discovery
                    localClients.remove(cl);
                    localClients.add(locCli);
                    obj.replaceLocalClient(cl, locCli);
                } else {
                    System.out.println(String.format("DEB: local connection to '%s' object already known, discarded.", locCli.getObjId()));  // probably caused by a bad DNS discovery
                    if (locCli.isConnected())
                        locCli.disconnect();
                }
                return;
            }

        localClients.add(locCli);
        System.out.println(String.format("DEB: discovered object '%s' at '%s:%d'.", locCli.getObjId(), address, port));

        objs.addNewConnection(locCli);  // disconnect if ther's already another client to same service connected
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServiceLost(String type, String name) {}

}
