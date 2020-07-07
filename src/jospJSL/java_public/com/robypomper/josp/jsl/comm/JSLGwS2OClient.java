package com.robypomper.josp.jsl.comm;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.client.Client;
import com.robypomper.communication.client.DefaultSSLClient;
import com.robypomper.communication.client.ServerInfo;
import com.robypomper.communication.client.events.ClientMessagingEvents;
import com.robypomper.communication.client.events.DefaultClientEvents;
import com.robypomper.communication.trustmanagers.AbsCustomTrustManager;
import com.robypomper.communication.trustmanagers.DynAddTrustManager;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jcp.apis.params.jospgws.S2OAccessInfo;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.net.InetAddress;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;


/**
 * Client implementation for Gateway Service2Object connection.
 * <p>
 * This class provide a SSLClient to connect to the S2O Gw.
 */
public class JSLGwS2OClient implements Client {

    // Class constants

    public static final String CERT_ALIAS = "JSL-Cert-Cloud";
    public static final String JCP_CERT_ALIAS = "JCP-Cert-Cloud";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JSLSettings_002 locSettings;
    private final JSLCommunication_002 communication;
    private final JSLServiceInfo srvInfo;
    private final JCPClient_Service jcpClient;
    private final JCPCommSrv jcpComm;
    private final Certificate clientCert;
    private final DynAddTrustManager clientTrustManager;
    private final SSLContext sslCtx;
    private DefaultSSLClient client;


    // Constructor

    /**
     * Generate the SSL context to use for O2S Gw connection.
     * It use the object's id as certificate id and load the S2O Gw certificate
     * to the {@link javax.net.ssl.TrustManager} used for the SSL context.
     *
     * @param communication instance of the {@link JSLCommunication}
     *                      that initialized this client. It will used to
     *                      process data received from the O2S Gw.
     * @param srvInfo       the info of the represented service.
     * @param jcpComm       the APIs JOSP GWs's requests object.
     */
    public JSLGwS2OClient(JSLSettings_002 settings, JSLCommunication_002 communication, JSLServiceInfo srvInfo, JCPClient_Service jcpClient, JCPCommSrv jcpComm) throws JSLCommunication.CloudCommunicationException {
        this.locSettings = settings;
        this.communication = communication;
        this.srvInfo = srvInfo;
        this.jcpClient = jcpClient;
        this.jcpComm = jcpComm;

        try {
            log.trace(Mrk_JSL.JSL_COMM_SUB, "Generating ssl context for service's cloud client");
            KeyStore clientKeyStore = UtilsJKS.generateKeyStore(srvInfo.getFullId(), "", CERT_ALIAS);
            clientCert = UtilsJKS.extractCertificate(clientKeyStore, CERT_ALIAS);
            clientTrustManager = new DynAddTrustManager();
            sslCtx = UtilsSSL.generateSSLContext(clientKeyStore, "", clientTrustManager);

        } catch (UtilsSSL.GenerationException | UtilsJKS.GenerationException e) {
            log.warn(Mrk_JSL.JSL_COMM_SUB, String.format("Error on generating ssl context for service's cloud client because %s", e.getMessage()), e);
            throw new JSLCommunication.CloudCommunicationException("Error on generating ssl context for service's cloud client", e);
        }

        log.info(Mrk_JSL.JSL_COMM_SUB, String.format("Initialized JSLGwS2OClient %s his instance of DefaultSSLClient for service '%s'", client != null ? "and" : "but NOT", srvInfo.getSrvId()));
        if (isConnected()) {
            log.debug(Mrk_JSL.JSL_COMM_SUB, String.format("                           connected to GW's '%s:%d'", getServerAddr(), getServerPort()));
        }
    }


    // Init listener

    private boolean isInit = false;

    private void initConnection() throws ConnectionException {
        S2OAccessInfo s2oAccess;
        try {
            log.debug(Mrk_JSL.JSL_COMM_SUB, "Getting service GW client access info");
            log.trace(Mrk_JSL.JSL_COMM_SUB, "Getting JOSP Gw S2O access info for service's cloud client");
            s2oAccess = jcpComm.getS2OAccessInfo(clientCert);
            log.debug(Mrk_JSL.JSL_COMM_SUB, "Service GW client access info got");

            if (isInitializing()) {
                jcpClient.removeConnectListener(initListener);
                isInit = false;
            }

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | CertificateEncodingException | JCPClient2.ResponseException e) {
            log.warn(Mrk_JSL.JSL_COMM_SUB, String.format("Error on initializing service GW client because %s", e.getMessage()));
            if (!isInitializing()) {
                jcpClient.addConnectListener(initListener);
                isInit = true;
            }
            return;
        }

        try {
            log.debug(Mrk_JSL.JSL_COMM_SUB, "Initializing service GW client");
            log.trace(Mrk_JSL.JSL_COMM_SUB, "Registering JOSP Gw S2O certificate for service's cloud client");
            Certificate gwCertificate = UtilsJKS.loadCertificateFromBytes(s2oAccess.gwCertificate);
            clientTrustManager.addCertificate(JCP_CERT_ALIAS, gwCertificate);

            // Init SSL client
            client = new DefaultSSLClient(sslCtx, srvInfo.getFullId(), s2oAccess.gwAddress, s2oAccess.gwPort,
                    null, null, new GwS2OClientMessagingEventsListener());
            log.debug(Mrk_JSL.JSL_COMM_SUB, "Service GW client initialized");

        } catch (UtilsJKS.LoadingException | AbsCustomTrustManager.UpdateException e) {
            log.warn(Mrk_JSL.JSL_COMM_SUB, String.format("Error on initializing service GW client because %s", e.getMessage()), e);
            throw new ConnectionException("Error on initializing service GW client");
        }


        try {
            log.debug(Mrk_JSL.JSL_COMM_SUB, "Connecting service GW client");
            client.connect();
            log.debug(Mrk_JSL.JSL_COMM_SUB, "Service GW client connected");

        } catch (ConnectionException e) {
            log.warn(Mrk_JSL.JSL_COMM_SUB, String.format("Error on connecting service GW client because %s", e.getMessage()), e);
            throw new ConnectionException("Error on connecting service GW client");
        }
    }

    private boolean isInitializing() {
        return isInit;
    }

    @SuppressWarnings("Convert2Lambda")
    private final JCPClient2.ConnectListener initListener = new JCPClient2.ConnectListener() {
        @Override
        public void onConnected(JCPClient2 jcpClient) {
            try {
                initConnection();
            } catch (ConnectionException ignore) {/*Exception in timer's thread*/}
        }

        @Override
        public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {}

    };


    // Processing incoming data

    /**
     * Forward received data to the {@link JSLCommunication}
     * instance.
     *
     * @param readData the message string received from the S2O Gw.
     * @return always true.
     */
    public boolean onDataReceived(String readData) {
        return communication.processFromObjectMsg(readData, JOSPPerm.Connection.LocalAndCloud);
    }


    // Client's wrapping methods

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getServerAddr() {
        return client != null ? client.getServerAddr() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getServerPort() {
        return client != null ? client.getServerPort() : -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServerInfo getServerInfo() {
        return client != null ? client.getServerInfo() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getClientAddr() {
        return client != null ? client.getClientAddr() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getClientPort() {
        return client != null ? client.getClientPort() : -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientId() {
        return client != null ? client.getClientId() : srvInfo.getSrvId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws ConnectionException {
        initConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        if (isInitializing()) {
            jcpClient.removeConnectListener(initListener);
            isInit = false;
        }

        if (client != null)
            client.disconnect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(byte[] data) throws ServerNotConnectedException {
        if (client == null)
            throw new ServerNotConnectedException(getClientId());

        client.sendData(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(String msg) throws ServerNotConnectedException {
        if (client == null)
            throw new ServerNotConnectedException(getClientId());

        client.sendData(msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSrvByeMsg(byte[] data) {
        if (client == null)
            return false;

        return client.isSrvByeMsg(data);
    }


    // Client events listener

    /**
     * Link the {@link #onDataReceived(String)} event to
     * {@link JSLGwS2OClient#onDataReceived(String)} ()} method.
     */
    private class GwS2OClientMessagingEventsListener extends DefaultClientEvents implements ClientMessagingEvents {

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onDataSend(byte[] writtenData) {}

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onDataSend(String writtenData) {}

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
         * {@inheritDoc}
         * <p>
         * Link to the {@link JSLGwS2OClient#onDataReceived(String)} method.
         */
        @Override
        public boolean onDataReceived(String readData) {
            return JSLGwS2OClient.this.onDataReceived(readData);
        }
    }

}
