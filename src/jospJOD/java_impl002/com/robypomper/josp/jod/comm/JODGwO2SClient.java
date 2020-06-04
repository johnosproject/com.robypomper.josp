package com.robypomper.josp.jod.comm;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.client.Client;
import com.robypomper.communication.client.DefaultSSLClient;
import com.robypomper.communication.client.ServerInfo;
import com.robypomper.communication.client.events.ClientMessagingEvents;
import com.robypomper.communication.client.events.DefaultClientEvents;
import com.robypomper.communication.trustmanagers.AbsCustomTrustManager;
import com.robypomper.communication.trustmanagers.DynAddTrustManager;
import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.params.jospgws.O2SAccessInfo;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.log.Mrk_JOD;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.net.InetAddress;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Client implementation for Gateway Object2Service connection.
 * <p>
 * This class provide a SSLClient to connect to the O2S Gw
 */
public class JODGwO2SClient implements Client {

    // Class constants

    public static final String CERT_ALIAS = "JOD-Cert-Cloud";
    public static final String JCP_CERT_ALIAS = "JCP-Cert-Cloud";
    public static final String TH_INIT_NAME = "_JCPCONNECTION_";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JODSettings_002 locSettings;
    private final JODCommunication_002 communication;
    private final JODObjectInfo objInfo;
    private final JCPCommObj jcpComm;
    private final Certificate clientCert;
    private final DynAddTrustManager clientTrustManager;
    private final SSLContext sslCtx;
    private DefaultSSLClient client;
    private Timer initTimer = null;


    // Constructor

    /**
     * Default constructor for JOSP GW O2S client.
     * <p>
     * Generate the SSL context, request the GW's access info and  to use for O2S Gw connection.
     * It use the object's id as certificate id and load the O2S Gw certificate
     * to the {@link javax.net.ssl.TrustManager} used for the SSL context.
     *
     * @param communication instance of the {@link JODCommunication}
     *                      that initialized this client. It will used to
     *                      process data received from the O2S Gw.
     * @param objInfo       the info of the represented object.
     * @param jcpComm       the APIs JOSP GWs's requests object.
     */
    public JODGwO2SClient(JODSettings_002 settings, JODCommunication_002 communication, JODObjectInfo objInfo, JCPCommObj jcpComm) throws JODCommunication.CloudCommunicationException {
        this.locSettings = settings;
        this.communication = communication;
        this.objInfo = objInfo;
        this.jcpComm = jcpComm;

        try {
            log.trace(Mrk_JOD.JOD_COMM_SUB, "Generating ssl context for object's cloud client");
            KeyStore clientKeyStore = UtilsJKS.generateKeyStore(objInfo.getObjId(), "", CERT_ALIAS);
            clientCert = UtilsJKS.extractCertificate(clientKeyStore, CERT_ALIAS);
            clientTrustManager = new DynAddTrustManager();
            sslCtx = UtilsSSL.generateSSLContext(clientKeyStore, "", clientTrustManager);

        } catch (UtilsSSL.GenerationException | UtilsJKS.GenerationException e) {
            log.warn(Mrk_JOD.JOD_COMM_SUB, String.format("Error on generating ssl context for object's cloud client because %s", e.getMessage()), e);
            throw new JODCommunication.CloudCommunicationException("Error on generating ssl context for object's cloud client", e);
        }

        log.info(Mrk_JSL.JSL_COMM_SUB, String.format("Initialized JSLGwS2OClient %s his instance of DefaultSSLClient for service '%s'", client != null ? "and" : "but NOT", objInfo.getObjId()));
        if (isConnected()) {
            log.debug(Mrk_JSL.JSL_COMM_SUB, String.format("                           connected to GW's '%s:%d'", getServerAddr(), getServerPort()));
        }
    }


    // Init timer

    private void initConnection() throws ConnectionException {
        O2SAccessInfo o2sAccess;
        try {
            log.debug(Mrk_JOD.JOD_COMM_SUB, "Getting object GW client access info");
            log.trace(Mrk_JOD.JOD_COMM_SUB, "Getting JOSP Gw O2S access info for object's cloud client");
            o2sAccess = jcpComm.getO2SAccessInfo(clientCert);
            log.debug(Mrk_JOD.JOD_COMM_SUB, "Object GW client access info got");

            if (isInitializing())
                stopInitTimer();

        } catch (JCPClient.ConnectionException | JCPClient.RequestException | CertificateEncodingException e) {
            log.warn(Mrk_JOD.JOD_COMM_SUB, String.format("Error on initializing object GW client because %s", e.getMessage()));
            if (!isInitializing())
                startInitTimer();
            return;
        }

        try {
            log.debug(Mrk_JOD.JOD_COMM_SUB, "Initializing object GW client");
            log.trace(Mrk_JOD.JOD_COMM_SUB, "Registering JOSP Gw O2S certificate for object's cloud client");
            Certificate gwCertificate = UtilsJKS.loadCertificateFromBytes(o2sAccess.gwCertificate);
            clientTrustManager.addCertificate(JCP_CERT_ALIAS, gwCertificate);

            // Init SSL client
            client = new DefaultSSLClient(sslCtx, objInfo.getObjId(), o2sAccess.gwAddress, o2sAccess.gwPort,
                    null, null, new GwO2SClientMessagingEventsListener());
            log.debug(Mrk_JOD.JOD_COMM_SUB, "Object GW client initialized");

        } catch (UtilsJKS.LoadingException | AbsCustomTrustManager.UpdateException e) {
            log.warn(Mrk_JOD.JOD_COMM_SUB, String.format("Error on initializing object GW client because %s", e.getMessage()), e);
            throw new ConnectionException("Error on initializing object GW client");
        }


        try {
            log.debug(Mrk_JOD.JOD_COMM_SUB, "Connecting object GW client");
            client.connect();
            log.debug(Mrk_JOD.JOD_COMM_SUB, "Object GW client connected");

        } catch (ConnectionException e) {
            log.warn(Mrk_JOD.JOD_COMM_SUB, String.format("Error on connecting object GW client because %s", e.getMessage()), e);
            throw new ConnectionException("Error on connecting object GW client");
        }
    }

    private boolean isInitializing() {
        return initTimer != null;
    }

    private void startInitTimer() {
        if (isInitializing())
            return;

        log.debug(Mrk_JOD.JOD_COMM_SUB, "Starting object GW client init's timer");
        initTimer = new Timer(true);
        initTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Thread.currentThread().setName(TH_INIT_NAME);
                try {
                    initConnection();
                } catch (ConnectionException ignore) {/*Exception in timer's thread*/}
            }
        }, locSettings.getJCPRefreshTime() * 1000, locSettings.getJCPRefreshTime() * 1000);
        log.debug(Mrk_JOD.JOD_COMM_SUB, "Object GW client init's timer started");
    }

    private void stopInitTimer() {
        if (!isInitializing())
            return;

        log.debug(Mrk_JOD.JOD_COMM_SUB, "Stopping object GW client init's timer");
        initTimer.cancel();
        initTimer = null;
        log.debug(Mrk_JOD.JOD_COMM_SUB, "Object GW client init's timer stopped");
    }


    // Processing incoming data

    /**
     * Forward received data to the {@link JODCommunication}
     * instance.
     *
     * @param readData the message string received from the O2S Gw.
     * @return always true.
     */
    public boolean onDataReceived(String readData) {
        log.info(Mrk_JOD.JOD_COMM_SUB, String.format("Data '%s...' received from GWs O2S to '%s' object", readData.substring(0, readData.indexOf("\n")), objInfo.getObjId()));

        // Action requests
        if (communication.forwardAction(readData))
            return true;

        // Service requests
        String responseOrError = communication.processCloudRequest(readData);
        if (responseOrError != null) {
            log.debug(Mrk_JOD.JOD_COMM_SUB, String.format("Sending response for cloud request '%s...' to GWs O2S from '%s' object", readData.substring(0, 10), objInfo.getObjId()));
            try {
                sendData(responseOrError);
                log.debug(Mrk_JOD.JOD_COMM_SUB, String.format("Response for service request '%s...' send to GWs O2S from '%s' object", readData.substring(0, 10), objInfo.getObjId()));
                return true;

            } catch (ServerNotConnectedException e) {
                log.warn(Mrk_JOD.JOD_COMM_SUB, String.format("Error on sending response for service request '%s...' to GWs O2S from '%s' object because %s", readData.substring(0, 10), objInfo.getObjId(), e.getMessage()), e);
                return false;
            }
        }

        // Unknown request
        log.warn(Mrk_JOD.JOD_COMM_SUB, String.format("Error on processing received data '%s...' from GWs O2S to '%s' object because unknown request", readData.substring(0, 10), objInfo.getObjId()));
        return false;
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
        return client != null ? client.getClientId() : objInfo.getObjId();
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
        if (isInitializing())
            stopInitTimer();

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
    public void sendData(String data) throws ServerNotConnectedException {
        if (client == null)
            throw new ServerNotConnectedException(getClientId());

        client.sendData(data);
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
     * {@link JODGwO2SClient#onDataReceived(String)} ()} method.
     */
    private class GwO2SClientMessagingEventsListener extends DefaultClientEvents implements ClientMessagingEvents {

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
         * Does nothing and return <code>false</code>.
         */
        @Override
        public boolean onDataReceived(byte[] readData) {
            return false;
        }

        /**
         * {@inheritDoc}
         * <p>
         * Link to the {@link JODGwO2SClient#onDataReceived(String)} method.
         */
        @Override
        public boolean onDataReceived(String readData) {
            return JODGwO2SClient.this.onDataReceived(readData);
        }
    }

}
