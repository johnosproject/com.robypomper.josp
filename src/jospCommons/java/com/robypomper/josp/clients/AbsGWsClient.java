package com.robypomper.josp.clients;

import com.robypomper.comm.client.Client;
import com.robypomper.comm.client.ClientWrapper;
import com.robypomper.comm.connection.ConnectionState;
import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.PeerException;
import com.robypomper.comm.peer.DisconnectionReason;
import com.robypomper.comm.peer.Peer;
import com.robypomper.comm.peer.PeerConnectionListener;
import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;
import com.robypomper.comm.trustmanagers.DynAddTrustManager;
import com.robypomper.java.JavaAssertions;
import com.robypomper.java.JavaJKS;
import com.robypomper.java.JavaSSL;
import com.robypomper.java.JavaTimers;
import com.robypomper.josp.params.jospgws.AccessInfo;
import com.robypomper.josp.protocol.JOSPProtocol;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Timer;

public abstract class AbsGWsClient extends ClientWrapper {

    // Class constants

    public static final String CLIENT_TYPE_O2S = "JOD";
    public static final String CLIENT_TYPE_S2O = "JSL";
    public static final String KS_PASS = "654321";
    public static final String TH_RE_CONNECT_NAME = "RE_CONNECT";


    // Internal vars

    // JCP APIs access
    private final JCPClient2 jcpClient;
    private JCPClient2.ConnectionListener jcpConnectionListener = null;
    // Client SSL stuff
    private DynAddTrustManager trustManager;
    private Certificate localCertificate;
    private SSLContext sslCtx;
    // behaviors
    private Timer reConnectTimer = null;


    // Constructors

    protected AbsGWsClient(String clientId, String serverId, JCPClient2 jcpClient) {
        super(clientId, serverId, JOSPProtocol.JOSP_PROTO_NAME);
        this.jcpClient = jcpClient;

        trustManager = new DynAddTrustManager();
        try {
            KeyStore clientKeyStore = JavaJKS.generateKeyStore(getLocalId(), KS_PASS, getLocalId() + "-CloudCert");
            localCertificate = JavaJKS.extractCertificate(clientKeyStore, getLocalId() + "-CloudCert");
            sslCtx = JavaSSL.generateSSLContext(clientKeyStore, KS_PASS, trustManager);

        } catch (JavaJKS.GenerationException | JavaSSL.GenerationException e) {
            assert false : String.format("JKS and SSL generation are standard and should not throw exception [%s] %s", e.getClass().getSimpleName(), e.getMessage());
        }

        //addListener(new PeerAbs.PeerConnectionLogger("AbsGWsClient"));
        addListener(autoReConnectListener);
    }


    // Connection methods

    @Override
    public void connect() throws PeerConnectionException {
        if (getState().isConnected())
            return;

        if (getState().isConnecting())
            return;

        doConnect(false);
    }

    public void doConnect(boolean preventReConnecting) throws PeerConnectionException {
        if (getState().isConnected()) {
            JavaAssertions.makeAssertionFailed("Can't call AbsGWsClient::doConnect() when already connected");
            return;
        }

        if (!getState().isConnecting())
            emitOnConnecting();

        resetWrapper();

        if (!jcpClient.isConnected()) {
            if (!preventReConnecting && getAutoReConnectConfigs().isEnable())
                tryAddJCPConnectionListener();
            else
                emitOnDisconnect();
            throw new PeerConnectionException(this, String.format("Error on AbsGWsClient '%s' because JCP APIs not connected", this));
        }

        AccessInfo accessInfo;
        try {
            accessInfo = getAccessInfo(localCertificate);

        } catch (Throwable e) {
            if (!preventReConnecting) {
                if (getAutoReConnectConfigs().isEnable())
                    //&& e instanceof PeerConnectionException && ((PeerConnectionException) e).isRemotePeerNotAvailable())
                    scheduleReConnecting();
                else
                    emitOnDisconnect();
            }
            throw new PeerConnectionException(this, e, String.format("Error on AbsGWsClient '%s' because error on get JCP GWs's access info from JCP APIs", this));
        }

        try {
            Certificate gwCertificate = JavaJKS.loadCertificateFromBytes(accessInfo.gwCertificate);
            trustManager.addCertificate("JCPGWsCert", gwCertificate);

        } catch (JavaJKS.LoadingException | AbsCustomTrustManager.UpdateException e) {
            if (!preventReConnecting) {
                if (getAutoReConnectConfigs().isEnable())
                    //&& e instanceof PeerConnectionException && ((PeerConnectionException) e).isRemotePeerNotAvailable())
                    scheduleReConnecting();
                else
                    emitOnDisconnect();
            }
            throw new PeerConnectionException(this, e, String.format("Error on AbsGWsClient '%s' because JCP APIs returned invalid JCP GWs's certificate", this));
        }

        try {
            Client client = initGWsClient(accessInfo, sslCtx);
            client.getAutoReConnectConfigs().enable(false);
            setWrapper(client, false);
        } catch (Throwable e) {
            if (!preventReConnecting) {
                if (getAutoReConnectConfigs().isEnable())
                    //&& e instanceof PeerConnectionException && ((PeerConnectionException) e).isRemotePeerNotAvailable())
                    scheduleReConnecting();
                else
                    emitOnDisconnect();
            }
            throw new PeerConnectionException(this, e, String.format("Error on AbsGWsClient '%s' because %s", this, e.getMessage()));
        }

        super.doConnect();
        stopReConnecting();
    }

    @Override
    protected void doDisconnect() throws PeerDisconnectionException {
        if (getState().isDisconnected()) {
            JavaAssertions.makeAssertionFailed("Can't call AbsGWsClient::doDisconnect() when already disconnected");
            return;
        }

        if (!getState().isDisconnecting())
            emitOnDisconnecting();

        if (getState() == ConnectionState.WAITING_SERVER) {         // waiting JCP APIs or GWs's Info
            tryRemoveJCPConnectionListener();
            stopReConnecting();     // or tryStopReConnecting()
            emitOnDisconnect();
            return;
        }

        super.doDisconnect();
    }


    // GWsClient sub classing

    protected abstract AccessInfo getAccessInfo(Certificate localCertificate) throws Throwable;

    protected abstract Client initGWsClient(AccessInfo accessInfo, SSLContext sslCtx) throws Throwable;


    // JCP APIs listener

    private void tryAddJCPConnectionListener() {
        if (jcpConnectionListener == null) {
            jcpConnectionListener = new JCPClient2.ConnectionListener() {

                @Override
                public void onConnected(JCPClient2 jcpClient) {
                    try {
                        doConnect(true);

                    } catch (PeerException e) { /* ignored because exec via JCP APIs listener timer */ }
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

            jcpClient.addConnectionListener(jcpConnectionListener);
        }
        emitOnConnecting_Waiting();
    }

    private void tryRemoveJCPConnectionListener() {
        if (jcpConnectionListener != null) {
            jcpClient.removeConnectionListener(jcpConnectionListener);
            jcpConnectionListener = null;
        }
    }


    // Re-Connecting

    protected boolean isReConnecting() {
        return reConnectTimer != null;
    }

    protected void scheduleReConnecting() {
        //System.out.println("$$$$ AbsGWsClient::scheduleReConnecting");
        reConnectTimer = JavaTimers.initAndStart(new ClientReConnect(), true, TH_RE_CONNECT_NAME, getLocalId(), getAutoReConnectConfigs().getDelay(), getAutoReConnectConfigs().getDelay());
        emitOnConnecting_Waiting();
    }

    protected void stopReConnecting() {
        if (reConnectTimer == null)
            return;
        reConnectTimer.cancel();
        reConnectTimer = null;
    }

    private class ClientReConnect implements Runnable {

        @Override
        public void run() {
            try {
                doConnect(true);

            } catch (PeerException e) {
                emitOnFail("Error re-connecting", e);
            }
        }

    }

    private PeerConnectionListener autoReConnectListener = new PeerConnectionListener() {

        @Override
        public void onConnecting(Peer peer) {
        }

        @Override
        public void onWaiting(Peer peer) {
        }

        @Override
        public void onConnect(Peer peer) {
        }

        @Override
        public void onDisconnecting(Peer peer) {
        }

        @Override
        public void onDisconnect(Peer peer) {
            //resetWrapper();

            if (!getAutoReConnectConfigs().isEnable())
                return;

            if (getWrapper().getDisconnectionReason() == DisconnectionReason.LOCAL_REQUEST)     // Check on wrapper because wrapper connectionInfo not yet updated because PeerConnectionListeners order
                return;

            try {
                doConnect(false);

            } catch (PeerException e) {
                emitOnFail("Error re-connecting", e);
            }
        }

        @Override
        public void onFail(Peer peer, String failMsg, Throwable exception) {
        }

    };

}