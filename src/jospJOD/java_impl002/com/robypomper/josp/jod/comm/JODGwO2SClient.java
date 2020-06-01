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
    private final JODCommunication_002 communication;
    private final JODObjectInfo objInfo;
    private final DefaultSSLClient client;


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
    public JODGwO2SClient(JODCommunication_002 communication, JODObjectInfo objInfo, JCPCommObj jcpComm) throws JODCommunication.CloudCommunicationException {
        this.communication = communication;
        this.objInfo = objInfo;

        Certificate clientCert;
        DynAddTrustManager clientTrustManager;
        SSLContext sslCtx;
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

        O2SAccessInfo o2sAccess;
        try {
            log.trace(Mrk_JOD.JOD_COMM_SUB, "Getting JOSP Gw O2S access info for object's cloud client");
            o2sAccess = jcpComm.getO2SAccessInfo(clientCert);
        } catch (JCPClient.ConnectionException | JCPClient.RequestException e) {
            log.warn(Mrk_JOD.JOD_COMM_SUB, String.format("Error on getting JOSP Gw O2S access info for object's cloud client because %s", e.getMessage()), e);
            throw new JODCommunication.CloudCommunicationException("Error on getting JOSP Gw O2S access info for object's cloud client", e);
        } catch (CertificateEncodingException e) {
            log.warn(Mrk_JOD.JOD_COMM_SUB, String.format("Error on request JOSP Gw O2S access info for object's cloud client because %s", e.getMessage()), e);
            throw new JODCommunication.CloudCommunicationException("Error on request JOSP Gw O2S access info for object's cloud client", e);
        }

        Certificate gwCertificate;
        try {
            log.trace(Mrk_JOD.JOD_COMM_SUB, "Registering JOSP Gw O2S certificate for object's cloud client");
            gwCertificate = UtilsJKS.loadCertificateFromBytes(o2sAccess.gwCertificate);
            clientTrustManager.addCertificate(JCP_CERT_ALIAS, gwCertificate);

        } catch (AbsCustomTrustManager.UpdateException | UtilsJKS.LoadingException e) {
            log.warn(Mrk_JOD.JOD_COMM_SUB, String.format("Error on registering JOSP Gw O2S certificate to object's cloud client because %s", e.getMessage()), e);
            throw new JODCommunication.CloudCommunicationException("Error on registering JOSP Gw O2S certificate to object's cloud client", e);
        }

        // Init SSL client
        client = new DefaultSSLClient(sslCtx, objInfo.getObjId(), o2sAccess.gwAddress, o2sAccess.gwPort,
                null, null, new GwO2SClientMessagingEventsListener());

        log.info(Mrk_JSL.JSL_COMM_SUB, String.format("Initialized JSLGwS2OClient for service '%s'", objInfo.getObjId()));
        log.debug(Mrk_JSL.JSL_COMM_SUB, String.format("                           connected to GW's '%s:%d'", o2sAccess.gwAddress, o2sAccess.gwPort));
        log.debug(Mrk_JSL.JSL_COMM_SUB, String.format("                           with certificate '%d'", gwCertificate.hashCode()));
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
        return client.getServerAddr();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getServerPort() {
        return client.getServerPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServerInfo getServerInfo() {
        return client.getServerInfo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getClientAddr() {
        return client.getClientAddr();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getClientPort() {
        return client.getClientPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientId() {
        return client.getClientId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return client.isConnected();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws ConnectionException {
        client.connect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        client.disconnect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(byte[] data) throws ServerNotConnectedException {
        client.sendData(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(String data) throws ServerNotConnectedException {
        client.sendData(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSrvByeMsg(byte[] data) {
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
