package com.robypomper.josp.jcp.gws.gw;

import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.ServerException;
import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.comm.server.Server;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.comm.server.ServerStateListener;
import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;
import com.robypomper.comm.trustmanagers.DynAddTrustManager;
import com.robypomper.java.*;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.clients.JCPClientsMngr;
import com.robypomper.josp.jcp.clients.jcp.jcp.APIsClient;
import com.robypomper.josp.jcp.info.JCPGWsVersions;
import com.robypomper.josp.jcp.params.jcp.JCPGWsStartup;
import com.robypomper.josp.jcp.params.jcp.JCPGWsStatus;
import com.robypomper.josp.params.jcp.GWsStatus;
import com.robypomper.josp.params.jospgws.AccessInfo;
import com.robypomper.josp.params.jospgws.O2SAccessInfo;
import com.robypomper.josp.params.jospgws.S2OAccessInfo;
import com.robypomper.josp.types.josp.gw.GWType;
import org.slf4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class GWAbs implements ApplicationListener<ContextRefreshedEvent> {

    // Class constants

    private static final String KS_PASS = "123456";
    public static final String TH_REGISTER_NAME = "GW_REG_%s";


    // Internal vars

    private final Logger log;
    protected static final String gwSerial = JavaRandomStrings.randomAlfaString(5);
    private final GWType gwType;
    private final GWServer server;
    private final APIsClient jcpAPIsGWs;
    private final String addrInternal;
    private final String addrPublic;
    private final int apisPort;
    private final int maxClients;
    private final JCPGWsStatus gwStatus;
    private CountDownLatch deregisterCountDown = new CountDownLatch(1);
    private boolean springStarted = false;
    private Timer registerTimer;
    private final int registerTimerDelayMS;


    // Constructor

    protected GWAbs(GWType gwType, String idServer, String addrInternal, String addrPublic, int gwPort, int apiPort, int maxClients,
                    JCPClientsMngr clientsMngr, Logger log) throws ServerStartupException, JavaJKS.GenerationException, JavaSSL.GenerationException {
        this.log = log;
        this.gwType = gwType;
        this.addrInternal = addrInternal;
        this.addrPublic = addrPublic;
        this.apisPort = apiPort;
        this.maxClients = maxClients;
        this.registerTimerDelayMS = 60 * 1000;

        KeyStore ks = JavaJKS.generateKeyStore(idServer, KS_PASS, idServer);
        Certificate publicCertificate = JavaJKS.extractCertificate(ks, idServer);
        DynAddTrustManager trustManager = new DynAddTrustManager();
        SSLContext sslCtx = JavaSSL.generateSSLContext(ks, KS_PASS, trustManager);

        this.gwStatus = new JCPGWsStatus(0, maxClients, null, null);

        this.jcpAPIsGWs = new APIsClient(clientsMngr.getJCPAPIsClient());
        clientsMngr.getJCPAPIsClient().addConnectionListener(jcpAPIsListener_GWRegister);

        this.server = new GWServer(this, sslCtx, idServer, gwPort, trustManager, publicCertificate);
        this.server.addListener(serverStateListener_GWRegister);
        this.server.startup();
    }

    public void destroy() {
        try {
            getServer().shutdown();
            deregisterCountDown.await(1000, TimeUnit.MILLISECONDS);

        } catch (ServerException e) {
            log.warn(String.format("Error shutdown JCP GW '%s' server", getId()), e);

        } catch (InterruptedException e) {
            log.warn(String.format("Error waiting shutdown JCP GW '%s' server", getId()), e);
        }

        if (deregisterCountDown.getCount() > 0) {
            log.info(String.format("Forced JCP GW '%s' de-registration", getId()));
            deregister();
        }
    }

    // Getters

    public static String getSerial() {
        return gwSerial;
    }

    public String getId() {
        return getServer().getLocalId();
    }

    private GWType getType() {
        return gwType;
    }

    private GWServer getServer() {
        return server;
    }

    private String getInternalAddress() {
        return addrInternal;
    }

    private String getPublicAddress() {
        return addrPublic;
    }

    private int getGWPort() {
        return server.getServerPeerInfo().getPort();
    }

    private int getAPIsPort() {
        return apisPort;
    }

    private int getMaxClient() {
        return maxClients;
    }

    private JCPGWsStatus getGWStatus() {
        return gwStatus;
    }

    private String getVersion() {
        return JCPGWsVersions.VER_JCPGWs_S2O_2_0;
    }


    // GWServer's Clients events

    protected abstract void onClientConnection(ServerClient client);

    protected abstract void onClientDisconnection(ServerClient client);

    protected void disconnectBecauseError(ServerClient client, String errorCause) {
        try {
            client.disconnect();
        } catch (PeerDisconnectionException ignore) {
        }
        String what = getType() == GWType.Obj2Srv ? "JOD Object" : "JSL Service";
        log.warn(String.format("%s '%s' %s to JCP GW '%s', disconnected", what, client.getRemoteId(), errorCause, getId()));
    }


    // GWServer's Messages methods

    protected abstract boolean processData(ServerClient client, String data);


    // JCP APIs GWs registration

    private void tryRegisterAndUpdate() {
        if (!server.getState().isRunning())
            return;
        if (!jcpAPIsGWs.getClient().isConnected())
            return;
        if (!springStarted)
            return;

        if (registerTimer==null)
            registerTimer = JavaTimers.initAndStart(new RegisterTimer(), true, String.format(TH_REGISTER_NAME, getId()), this.toString(), 0, registerTimerDelayMS);
    }

    private void deregister() {
        if (!server.getState().isStopped())
            JavaAssertions.makeAssertion(server.getState().isStopped(), "Can't call GWServiceAbs.deregister() method when internal server is not stopped.");

        JavaTimers.stopTimer(registerTimer);
        registerTimer = null;

        if (!jcpAPIsGWs.getClient().isConnected()) {
            log.warn("Can't de-register JCP GW '%s' because JCP APIs not available");
            return;
        }

        try {
            jcpAPIsGWs.postShutdown(getId());
            log.info(String.format("JCP GW '%s' de-registered to JCP APIs successfully.", getId()));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            log.warn(String.format("Error de-register JCP GW '%s' to JCP APIs.", getId()), e);
        }
    }

    private class RegisterTimer implements Runnable {

        boolean isPrinted = false;

        @Override
        public void run() {
            JCPGWsStartup gwStartup = new JCPGWsStartup(getType(), getPublicAddress(), getGWPort(), getInternalAddress(), getAPIsPort(), getMaxClient(), getVersion());
            try {
                jcpAPIsGWs.postStartup(gwStartup, getId());
                if (!isPrinted) {
                    log.info(String.format("JCP GW '%s' registered to JCP APIs successfully.", getId()));
                    isPrinted = true;
                } else
                    log.trace(String.format("JCP GW '%s' registered to JCP APIs successfully.", getId()));

            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
                log.warn(String.format("Error register JCP GW '%s' to JCP APIs'.", getId()), e);
                isPrinted = false;
            }

            update();
        }

    }

    private void update() {
        if (!jcpAPIsGWs.getClient().isConnected()) {
            log.warn(String.format("Can't update JCP GW '%s' because JCP APIs not available", getId()));
            return;
        }

        try {
            jcpAPIsGWs.postStatus(getGWStatus(), getId());
            log.trace(String.format("JCP GW '%s' updated to JCP APIs successfully.", getId()));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            log.warn(String.format("Can't update JCP GW '%s' status to JCP APIs.", getId()), e);
        }
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final ServerStateListener serverStateListener_GWRegister = new ServerStateListener() {

        @Override
        public void onStart(Server server) {
            tryRegisterAndUpdate();
            deregisterCountDown = new CountDownLatch(1);
        }

        @Override
        public void onStop(Server server) {
            deregister();
            deregisterCountDown.countDown();
        }

        @Override
        public void onFail(Server server, String failMsg, Throwable exception) {
            log.warn(String.format("JCP GW '%s' failed: %s (%s)", this, failMsg, exception));
        }

    };

    @SuppressWarnings("FieldCanBeLocal")
    private final JCPClient2.ConnectionListener jcpAPIsListener_GWRegister = new JCPClient2.ConnectionListener() {

        @Override
        public void onConnected(JCPClient2 jcpClient) {
            tryRegisterAndUpdate();
        }

        @Override
        public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
        }

        @Override
        public void onAuthenticationFailed(JCPClient2 jcpClient, Throwable t) {
        }

        @Override
        public void onDisconnected(JCPClient2 jcpClient) {
        }

    };

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        springStarted = true;
        tryRegisterAndUpdate();
    }


    // GW Status helpers

    protected void increaseClient() {
        gwStatus.clients++;
        gwStatus.lastClientConnectedAt = JavaDate.getNowDate();
        update();
    }

    protected void decreaseClient() {
        gwStatus.clients--;
        gwStatus.lastClientDisconnectedAt = JavaDate.getNowDate();
        update();
    }


    // APIGWsGWsController

    public void addClientCertificate(String certId, Certificate clientCert) throws AbsCustomTrustManager.UpdateException {
        getServer().getTrustManager().addCertificate(certId, clientCert);
    }

    public AccessInfo getAccessInfo() throws CertificateEncodingException {
        byte[] certBytes = getServer().getPublicCertificate().getEncoded();
        if (getType() == GWType.Obj2Srv)
            return new O2SAccessInfo(getPublicAddress(), getGWPort(), certBytes);
        else
            return new S2OAccessInfo(getPublicAddress(), getGWPort(), certBytes);
    }


    // GWsController

    public GWsStatus.Server getJCPAPIsStatus() {
        GWsStatus.Server gwStatus = new GWsStatus.Server();
        gwStatus.id = getId();
        gwStatus.type = getType();
        gwStatus.status = getServer().getState().toString();
        gwStatus.internalAddress = getInternalAddress();
        gwStatus.publicAddress = getPublicAddress();
        gwStatus.gwPort = getGWPort();
        gwStatus.apisPort = getAPIsPort();
        gwStatus.clientsCount = getServer().getClients().size();
        gwStatus.maxClientsCount = getMaxClient();

        gwStatus.clientsList = new ArrayList<>();
        for (ServerClient c : getServer().getClients())
            gwStatus.clientsList.add(new GWsStatus.Client(c.getRemoteId(), c.getState().isConnected(), c.getConnectionInfo().getLocalInfo().toString(), c.getConnectionInfo().getRemoteInfo().toString()));
        return gwStatus;
    }

}
