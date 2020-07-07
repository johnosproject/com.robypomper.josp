package com.robypomper.josp.jod.comm;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.client.Client;
import com.robypomper.communication.client.DefaultSSLClient;
import com.robypomper.communication.client.ServerInfo;
import com.robypomper.communication.client.events.ClientMessagingEvents;
import com.robypomper.communication.client.events.ClientServerEvents;
import com.robypomper.communication.client.events.DefaultClientEvents;
import com.robypomper.communication.trustmanagers.AbsCustomTrustManager;
import com.robypomper.communication.trustmanagers.DynAddTrustManager;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jcp.apis.params.jospgws.O2SAccessInfo;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.net.InetAddress;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;


/**
 * Client implementation for Gateway Object2Service connection.
 * <p>
 * This class provide a SSLClient to connect to the O2S Gw
 */
public class JODGwO2SClient implements Client {

    // Class constants

    public static final String CERT_ALIAS = "JOD-Cert-Cloud";
    public static final String JCP_CERT_ALIAS = "JCP-Cert-Cloud";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JODSettings_002 locSettings;
    private final JODCommunication_002 communication;
    private final JODObjectInfo objInfo;
    private final JCPCommObj jcpComm;
    private final JCPClient_Object jcpClient;
    private final Certificate clientCert;
    private final DynAddTrustManager clientTrustManager;
    private final SSLContext sslCtx;
    private DefaultSSLClient client;


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
    public JODGwO2SClient(JODSettings_002 settings, JODCommunication_002 communication, JODObjectInfo objInfo, JCPClient_Object jcpClient, JCPCommObj jcpComm) throws JODCommunication.CloudCommunicationException {
        this.locSettings = settings;
        this.communication = communication;
        this.objInfo = objInfo;
        this.jcpClient = jcpClient;
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

        log.info(Mrk_JOD.JOD_COMM_SUB, String.format("Initialized JSLGwO2SClient %s his instance of DefaultSSLClient for service '%s'", client != null ? "and" : "but NOT", objInfo.getObjId()));
        if (isConnected()) {
            log.debug(Mrk_JOD.JOD_COMM_SUB, String.format("                           connected to GW's '%s:%d'", getServerAddr(), getServerPort()));
        }
    }


    // Init listener

    private boolean isInit = false;

    private void initConnection() throws ConnectionException {
        O2SAccessInfo o2sAccess;
        try {
            log.debug(Mrk_JOD.JOD_COMM_SUB, "Getting object GW client access info");
            log.trace(Mrk_JOD.JOD_COMM_SUB, "Getting JOSP Gw O2S access info for object's cloud client");
            o2sAccess = jcpComm.getO2SAccessInfo(clientCert);
            log.debug(Mrk_JOD.JOD_COMM_SUB, "Object GW client access info got");

            if (isInitializing()) {
                jcpClient.removeConnectListener(initListener);
                isInit = false;
            }

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | CertificateEncodingException | JCPClient2.ResponseException e) {
            log.warn(Mrk_JOD.JOD_COMM_SUB, String.format("Error on initializing object GW client because %s", e.getMessage()));
            if (!isInitializing()) {
                jcpClient.addConnectListener(initListener);
                isInit = true;
            }
            return;
        }

        try {
            log.debug(Mrk_JOD.JOD_COMM_SUB, "Initializing object GW client");
            log.trace(Mrk_JOD.JOD_COMM_SUB, "Registering JOSP Gw O2S certificate for object's cloud client");
            Certificate gwCertificate = UtilsJKS.loadCertificateFromBytes(o2sAccess.gwCertificate);
            clientTrustManager.addCertificate(JCP_CERT_ALIAS, gwCertificate);

            // Init SSL client
            client = new DefaultSSLClient(sslCtx, objInfo.getObjId(), o2sAccess.gwAddress, o2sAccess.gwPort,
                    null, new GwO2SClientServerEventsListener(), new GwO2SClientMessagingEventsListener());
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
        return isInit;
    }

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
     * Send to O2S gateway object's presentation messages.
     */
    public void onServerConnection() {
        try {
            communication.sendToCloud(JOSPProtocol_ObjectToService.createObjectInfoMsg(objInfo.getObjId(), objInfo.getObjName(), objInfo.getJODVersion(), objInfo.getOwnerId(), objInfo.getModel(), objInfo.getBrand(), objInfo.getLongDescr()));
            communication.sendToCloud(JOSPProtocol_ObjectToService.createObjectStructMsg(objInfo.getObjId(), objInfo.getStructForJSL()));
            communication.sendToCloud(JOSPProtocol_ObjectToService.createObjectPermsMsg(objInfo.getObjId(), objInfo.getPermsForJSL()));

        } catch (JODStructure.ParsingException e) {
            log.warn(Mrk_JOD.JOD_COMM_SUB, String.format("Error on serialize object's structure to cloud service because %s", e.getMessage()), e);
        } catch (JODCommunication.CloudNotConnected e) {
            log.warn(Mrk_JOD.JOD_COMM_SUB, String.format("Error on sending object's presentation to cloud because %s", e.getMessage()), e);
        }
    }

    /**
     * Forward received data to the {@link JODCommunication}
     * instance.
     *
     * @param readData the message string received from the O2S Gw.
     * @return always true.
     */
    public boolean onDataReceived(String readData) {
        return communication.processFromServiceMsg(readData, JOSPPerm.Connection.LocalAndCloud);
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
     * Link the {@link #onServerConnection()} event to
     * {@link JODGwO2SClient#onServerConnection()} ()} method.
     */
    private class GwO2SClientServerEventsListener extends DefaultClientEvents implements ClientServerEvents {

        /**
         * {@inheritDoc}
         * <p>
         * Link to the {@link JODGwO2SClient#onDataReceived(String)} method.
         */
        @Override
        public void onServerConnection() {
            JODGwO2SClient.this.onServerConnection();
        }

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onServerDisconnection() {}

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onServerClientDisconnected() {}

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onServerGoodbye() {}

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onServerTerminated() {}

        /**
         * {@inheritDoc}
         * <p>
         * Does nothing.
         */
        @Override
        public void onServerError(Throwable e) {}

    }

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
