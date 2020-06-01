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
import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.params.jospgws.S2OAccessInfo;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
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
    private final JSLCommunication_002 communication;
    private final DefaultSSLClient client;


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
    public JSLGwS2OClient(JSLCommunication_002 communication, JSLServiceInfo srvInfo, JCPCommSrv jcpComm) throws JSLCommunication.CloudCommunicationException {
        this.communication = communication;

        // Create ssl context
        Certificate clientCert;
        DynAddTrustManager clientTrustManager;
        SSLContext sslCtx;
        try {
            log.trace(Mrk_JSL.JSL_COMM_SUB, "Generating ssl context for service's cloud client");
            KeyStore clientKeyStore = UtilsJKS.generateKeyStore(srvInfo.getSrvId(), "", CERT_ALIAS);
            clientCert = UtilsJKS.extractCertificate(clientKeyStore, CERT_ALIAS);
            clientTrustManager = new DynAddTrustManager();
            sslCtx = UtilsSSL.generateSSLContext(clientKeyStore, "", clientTrustManager);

        } catch (UtilsSSL.GenerationException | UtilsJKS.GenerationException e) {
            log.warn(Mrk_JSL.JSL_COMM_SUB, String.format("Error on generating ssl context for service's cloud client because %s", e.getMessage()), e);
            throw new JSLCommunication.CloudCommunicationException("Error on generating ssl context for service's cloud client", e);
        }

        S2OAccessInfo s2oAccess;
        try {
            log.trace(Mrk_JSL.JSL_COMM_SUB, "Getting JOSP Gw S2O access info for service's cloud client");
            s2oAccess = jcpComm.getS2OAccessInfo(clientCert);
        } catch (JCPClient.ConnectionException | JCPClient.RequestException e) {
            log.warn(Mrk_JSL.JSL_COMM_SUB, String.format("Error on getting JOSP GW S2O access info for service's cloud client because %s", e.getMessage()), e);
            throw new JSLCommunication.CloudCommunicationException("Error on getting JOSP GW S2O access info for service's cloud client", e);
        } catch (CertificateEncodingException e) {
            log.warn(Mrk_JSL.JSL_COMM_SUB, String.format("Error on request JOSP GW S2O access info for service's cloud client because %s", e.getMessage()), e);
            throw new JSLCommunication.CloudCommunicationException("Error on request JOSP GW S2O access info for service's cloud client", e);
        }

        Certificate gwCertificate;
        try {
            log.trace(Mrk_JSL.JSL_COMM_SUB, "Registering JOSP Gw S2O certificate for service's cloud client");
            gwCertificate = UtilsJKS.loadCertificateFromBytes(s2oAccess.gwCertificate);
            clientTrustManager.addCertificate(JCP_CERT_ALIAS, gwCertificate);

        } catch (AbsCustomTrustManager.UpdateException | UtilsJKS.LoadingException e) {
            log.warn(Mrk_JSL.JSL_COMM_SUB, String.format("Error on registering JOSP GW S2O certificate to object's cloud client because %s", e.getMessage()), e);
            throw new JSLCommunication.CloudCommunicationException("Error on registering JOSP GW S2O certificate to object's cloud client", e);
        }

        // Init SSL client
        client = new DefaultSSLClient(sslCtx, srvInfo.getFullId(), s2oAccess.gwAddress, s2oAccess.gwPort,
                null, null, new GwS2OClientMessagingEventsListener());

        log.info(Mrk_JSL.JSL_COMM_SUB, String.format("Initialized JSLGwS2OClient for service '%s'", srvInfo.getSrvId()));
        log.debug(Mrk_JSL.JSL_COMM_SUB, String.format("                           connected to GW's '%s:%d'", s2oAccess.gwAddress, s2oAccess.gwPort));
        log.debug(Mrk_JSL.JSL_COMM_SUB, String.format("                           with certificate '%d'", gwCertificate.hashCode()));
    }


    // Processing incoming data

    /**
     * Forward received data to the {@link JSLCommunication}
     * instance.
     *
     * @param readData the message string received from the S2O Gw.
     * @return always true.
     */
    public boolean onDataReceived(String readData) {
        log.warn(Mrk_JSL.JSL_COMM_SUB, "Not implemented");
        //communication.forwardUpdate(readData);
        // ToDo: fix JSLGwS2OClient.onDataReceived method
        return true;
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
