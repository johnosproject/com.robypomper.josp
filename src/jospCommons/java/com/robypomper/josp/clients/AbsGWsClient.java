package com.robypomper.josp.clients;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.client.AbsClientWrapper;
import com.robypomper.communication.client.Client;
import com.robypomper.communication.client.events.ClientMessagingEvents;
import com.robypomper.communication.client.events.ClientServerEvents;
import com.robypomper.communication.client.events.DefaultClientEvents;
import com.robypomper.communication.trustmanagers.DynAddTrustManager;
import com.robypomper.java.JavaEnum;
import com.robypomper.java.JavaThreads;
import com.robypomper.josp.params.jospgws.AccessInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.states.GWClientState;
import com.robypomper.josp.states.StateException;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings({"UnnecessaryReturnStatement", "unused"})
public abstract class AbsGWsClient<AI extends AccessInfo> extends AbsClientWrapper {

    // Class constants

    public static final String NAME_PROTO = "josp-cloud";
    public static final String NAME_SERVER_O2S = "JOSP GWs O2S Server";
    public static final String NAME_SERVER_S2O = "JOSP GWs S2O Server";
    public static final String CLIENT_TYPE_O2S = "JOD";
    public static final String CLIENT_TYPE_S2O = "JSL";
    public static final String CERT_ALIAS = "%s-Cert-Cloud";
    public static final String JCP_CERT_ALIAS = "JCP-Cert-Cloud";
    public static final String TIMER_CONNECTION_NAME = "_CONN_JOSP_";
    public static final int TIMER_DELAY_MS = 30 * 1000;


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JavaEnum.SynchronizableState<GWClientState> state = new JavaEnum.SynchronizableState<>(GWClientState.DISCONNECTED, log);
    // Configs
    private final JCPClient2 jcpClient;
    private final DynAddTrustManager clientTrustManager;
    private String sslCtxClientId = "";
    private SSLContext sslCtx;
    private Certificate clientCert;
    // Connection timer
    private Timer connectionTimer = null;


    // Constructor

    /**
     * Generate the SSL context to use for O2S/S2O GWs client.
     * It use the object/service's id as certificate id and load the O2S/S2O Gw certificate
     * to the {@link javax.net.ssl.TrustManager} used for the SSL context.
     *
     * @param clientId  object/service's id used to initialized this client.
     *                  It will used to process data received from the O2S Gw.
     * @param jcpClient the info of the represented service.
     */
    public AbsGWsClient(String clientId, JCPClient2 jcpClient) throws GWsClientException {
        super(clientId);
        this.jcpClient = jcpClient;
        this.clientTrustManager = new DynAddTrustManager();
    }


    // Getter state

    /**
     * {@inheritDoc}
     */
    @Override
    public Client.State getState() {
        switch (state.get()) {
            case CONNECTED:
                return State.CONNECTED;
            case CONNECTING:
                return State.CONNECTING;
            case CONNECTING_WAITING_JCP_APIS:
            case CONNECTING_WAITING_JCP_GWS:
                return State.CONNECTING_WAITING_SERVER;
            case DISCONNECTED:
                return State.DISCONNECTED;
            case DISCONNECTING:
                return State.DISCONNECTING;
        }
        return State.DISCONNECTED;
    }

    public GWClientState getGWS2OState() {
        return state.get();
    }


    // Getter configs

    protected abstract String getClientType();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProtocolName() {
        return NAME_PROTO;
    }


    // Getter SSL

    protected Certificate getClientCert() {
        return clientCert;
    }

    protected DynAddTrustManager getClientTrustManager() {
        return clientTrustManager;
    }

    private void renewNewSSLContext() throws GWsClientException {
        log.trace(Mrk_JSL.JSL_COMM_SUB, "Generating ssl context for service's cloud client");

        SSLContext sslCtxTmp;
        Certificate clientCertTmp;
        try {
            log.debug(Mrk_JSL.JSL_COMM_SUB, String.format("Generating ssl context for service's cloud client '%s'", getClientId()));
            KeyStore clientKeyStore = UtilsJKS.generateKeyStore(getClientId(), "", String.format(CERT_ALIAS, getClientId()));
            clientCertTmp = UtilsJKS.extractCertificate(clientKeyStore, String.format(CERT_ALIAS, getClientId()));
            sslCtxTmp = UtilsSSL.generateSSLContext(clientKeyStore, "", clientTrustManager);

        } catch (UtilsSSL.GenerationException | UtilsJKS.GenerationException e) {
            log.warn(Mrk_JSL.JSL_COMM_SUB, String.format("Error on generating ssl context for service's cloud client '%s' because %s", getClientId(), e.getMessage()), e);
            throw new GWsClientException(String.format("Error on generating ssl context for service's cloud client '%s'", getClientId()), e);
        }

        sslCtxClientId = getClientId();
        sslCtx = sslCtxTmp;
        clientCert = clientCertTmp;
    }


    // Client connection methods - Client's wrapping override methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws StateException {
        doConnect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() throws StateException {
        doDisconnect();
    }


    // Client connection methods - GW Client states manager

    private void doConnect() throws StateException {
        if (state.enumEquals(GWClientState.CONNECTED))
            return; // Already done

        else if (state.get().isCONNECTING())
            return; // Already in progress

        else if (state.enumEquals(GWClientState.DISCONNECTED))
            initConnection();

        else if (state.enumEquals(GWClientState.DISCONNECTING)) {
            if (stopDisconnecting())
                initConnection();
            else
                throw new StateException("Can't connect GW Client because is disconnecting, try again later");
        }
    }

    private void doDisconnect() throws StateException {
        if (state.enumEquals(GWClientState.CONNECTED))
            closeConnection();

        else if (state.get().isCONNECTING()) {
            if (stopConnecting()) {
                if (state.enumEquals(GWClientState.CONNECTED))
                    closeConnection();
            } else
                throw new StateException("Can't disconnect GW Client because is connecting, try again later");

        } else if (state.enumEquals(GWClientState.DISCONNECTED))
            return; // Already done

        else if (state.enumEquals(GWClientState.DISCONNECTING))
            return; // Already in progress
    }

    private void initConnection() {
        assert state.enumEquals(GWClientState.DISCONNECTED)
                || state.get().isCONNECTING() :
                "Method initConnection() can be called only from DISCONNECTED or CONNECTING_ state";

        synchronized (state) {
            if (!state.get().isCONNECTING())
                state.set(GWClientState.CONNECTING);

            // Check JCP APIs
            if (!jcpClient.isConnected()) {
                if (state.enumNotEquals(GWClientState.CONNECTING_WAITING_JCP_APIS)) {
                    log.warn("GW Client can't connect, add JCP APIs's connection listener; state = CONNECTING_WAITING_JCP_APIS");
                    state.set(GWClientState.CONNECTING_WAITING_JCP_APIS);
                    jcpClient.addConnectionListener(jcpConnectionListener);
                }
                return;
            }

            // Update sslCtx, if necessary
            if (sslCtxClientId.compareTo(getClientId()) != 0) {
                try {
                    renewNewSSLContext();

                } catch (GWsClientException e) {
                    log.warn(String.format("GW Client can't connect, can't renew SSL Context for '%s' client because %s.", getClientId(), e.getMessage()));
                    if (state.enumEquals(GWClientState.CONNECTING_WAITING_JCP_APIS)) {
                        log.warn("GW Client connect, remove JCP APIs's connection listener");
                        jcpClient.removeConnectionListener(jcpConnectionListener);
                    }
                    if (state.enumEquals(GWClientState.CONNECTING_WAITING_JCP_GWS)) {
                        log.warn("GW Client connect, stop JCP GWs's connection timer");
                        stopConnectionTimer();
                    }
                    state.set(GWClientState.DISCONNECTED);
                    log.warn(String.format("GW Client can't connect, client '%s' disconnected.", getClientId()));
                    return;
                }
            }

            // Connect to GWs Server
            AI accessInfo = getAccessInfo();
            if (accessInfo == null) {
                if (state.enumNotEquals(GWClientState.CONNECTING_WAITING_JCP_GWS)) {
                    log.warn("GW Client can't connect, start JCP GWs's connection timer; state = CONNECTING_WAITING_JCP_GWS");
                    state.set(GWClientState.CONNECTING_WAITING_JCP_GWS);
                    startConnectionTimer();
                }
                return;
            }
            Client client = initGWClient(sslCtx, accessInfo);
            if (client == null) {
                if (state.enumNotEquals(GWClientState.CONNECTING_WAITING_JCP_GWS)) {
                    log.warn("GW Client can't connect, start JCP GWs's connection timer; state = CONNECTING_WAITING_JCP_GWS");
                    state.set(GWClientState.CONNECTING_WAITING_JCP_GWS);
                    startConnectionTimer();
                }
                return;
            }
            if (!connectGWClient(client, accessInfo)) {
                if (state.enumNotEquals(GWClientState.CONNECTING_WAITING_JCP_GWS)) {
                    log.warn("GW Client can't connect, start JCP GWs's connection timer; state = CONNECTING_WAITING_JCP_GWS");
                    state.set(GWClientState.CONNECTING_WAITING_JCP_GWS);
                    startConnectionTimer();
                }
                return;
            }
            setWrappedClient(client);

            // Clean up connection waiting stuff
            if (state.enumEquals(GWClientState.CONNECTING_WAITING_JCP_APIS)) {
                log.warn("GW Client connect, remove JCP APIs's connection listener");
                jcpClient.removeConnectionListener(jcpConnectionListener);
            }
            if (state.enumEquals(GWClientState.CONNECTING_WAITING_JCP_GWS)) {
                log.warn("GW Client connect, stop JCP GWs's connection timer");
                stopConnectionTimer();
            }

            state.set(GWClientState.CONNECTED);
            emit_ClientConnected();
        }
    }

    private boolean stopConnecting() {
        assert state.get().isCONNECTING() :
                "Method stopConnecting() can be called only from CONNECTING_ state";

        // If connecting (1st attempt)
        if (state.enumEquals(GWClientState.CONNECTING)) {
            JavaThreads.softSleep(1000);
            return !state.get().isCONNECTING();
        }

        synchronized (state) {
            // Clean up connection waiting stuff
            if (state.enumEquals(GWClientState.CONNECTING_WAITING_JCP_APIS)) {
                log.warn("GW Client disconnect, remove JCP APIs's connection listener");
                jcpClient.removeConnectionListener(jcpConnectionListener);
            }
            if (state.enumEquals(GWClientState.CONNECTING_WAITING_JCP_GWS)) {
                log.warn("GW Client disconnect, stop JCP GWs's connection timer");
                stopConnectionTimer();
            }

            state.set(GWClientState.DISCONNECTED);
            return true;
        }
    }

    private void closeConnection() {
        assert state.enumEquals(GWClientState.CONNECTED) :
                "Method closeConnection() can be called only from CONNECTED state";

        synchronized (state) {
            state.set(GWClientState.DISCONNECTING);

            try {
                super.disconnect();
            } catch (StateException ignore) {
                assert false : "This method should be called only when wrapped client is in CONNECTED state";
            }
            resetWrappedClient();

            state.set(GWClientState.DISCONNECTED);
            emit_ClientDisconnected();
        }
    }

    private boolean stopDisconnecting() {
        assert state.enumEquals(GWClientState.DISCONNECTING) :
                "Method stopDisconnecting() can be called only from DISCONNECTING state";

        // If disconnecting (1st attempt)
        JavaThreads.softSleep(1000);
        return state.enumNotEquals(GWClientState.DISCONNECTING);
    }


    // Client connection methods - O2S/S2O Sub classing

    protected abstract AI getAccessInfo();

    protected abstract Client initGWClient(SSLContext sslCtx, AI accessInfo);

    protected abstract boolean connectGWClient(Client client, AI accessInfo);


    // Client connection methods - JCP APIs connection listener

    private final JCPClient2.ConnectionListener jcpConnectionListener = new JCPClient2.ConnectionListener() {
        @Override
        public void onConnected(JCPClient2 jcpClient) {
            initConnection();
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


    // Client connection methods - GWs re-connection timer

    private void startConnectionTimer() {
        assert state.enumEquals(GWClientState.CONNECTING_WAITING_JCP_GWS) :
                "Method startConnectionTimer() can be called only from CONNECTING_WAITING_JCP_GWS state";

        long waitMs = TIMER_DELAY_MS;
        connectionTimer = new Timer(true);
        connectionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                assert state.enumEquals(GWClientState.CONNECTING_WAITING_JCP_GWS) :
                        "Method startConnectionTimer() can be called only from CONNECTING_WAITING_JCP_GWS state";

                Thread.currentThread().setName(TIMER_CONNECTION_NAME);
                initConnection();
            }
        }, waitMs, waitMs);
    }

    private void stopConnectionTimer() {
        assert state.enumEquals(GWClientState.CONNECTING_WAITING_JCP_GWS) :
                "Method stopConnectionTimer() can be called only from CONNECTING_WAITING_JCP_GWS state";

        if (connectionTimer == null) return;

        connectionTimer.cancel();
        connectionTimer = null;
    }


    // Client connection methods - ClientServerEvents listener

    /**
     * Link the {@link #onServerDisconnection()} event to
     * {@link #doDisconnect()} and {@link #doConnect()} methods.
     */
    class GWsClientServerEventsListener extends DefaultClientEvents implements ClientServerEvents {

        @Override
        public void onServerConnection() {
        }

        @Override
        public void onServerDisconnection() {
            if (state.enumEquals(GWClientState.DISCONNECTING)
                    || state.enumEquals(GWClientState.DISCONNECTED))
                return;

            try {
                doDisconnect();
                doConnect();

            } catch (StateException e) {
                log.warn(String.format("Can't reconnect GWs Client because %s", e.getMessage()), e);
            }
        }

        @Override
        public void onServerClientDisconnected() {
        }

        @Override
        public void onServerGoodbye() {
        }

        @Override
        public void onServerTerminated() {
        }

        @Override
        public void onServerError(Throwable e) {
        }

    }

    protected ClientServerEvents clientServerEvents = new GWsClientServerEventsListener();


    // Messages methods - O2S/S2O Sub classing

    protected abstract boolean processData(String readData, JOSPPerm.Connection connType);


    // Messages methods ClientMessagingEvents listener

    /**
     * Link the {@link #onDataReceived(String)} event to
     * {@link #processData(String, JOSPPerm.Connection)} method.
     */
    private class GWsClientMessagingEventsListener extends DefaultClientEvents implements ClientMessagingEvents {

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onDataSend(byte[] writtenData) {
        }

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onDataSend(String writtenData) {
        }

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public boolean onDataReceived(byte[] readData) {
            return false;
        }

        /**
         * Forward received data to the {@link AbsGWsClient} implementation.
         *
         * @param readData the message string received from the S2O Gw.
         * @return always true.
         */
        @Override
        public boolean onDataReceived(String readData) {
            return processData(readData, JOSPPerm.Connection.LocalAndCloud);
        }

    }

    protected ClientMessagingEvents clientMessagingEvents = new GWsClientMessagingEventsListener();


    // Exceptions

    public static class GWsClientException extends Throwable {

        public GWsClientException(String msg) {
            super(msg);
        }

        public GWsClientException(String msg, Throwable cause) {
            super(msg, cause);
        }

    }

}
