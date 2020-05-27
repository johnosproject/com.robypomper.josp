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
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.net.InetAddress;
import java.security.KeyStore;


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
    private final DefaultSSLClient client;


    // Constructor

    /**
     * Generate the SSL context to use for O2S Gw connection.
     * It use the object's id as certificate id and load the O2S Gw certificate
     * to the {@link javax.net.ssl.TrustManager} used for the SSL context.
     *
     * @param communication     instance of the {@link JODCommunication}
     *                          that initialized this client. It will used to
     *                          process data received from the O2S Gw.
     * @param objInfo           the info of the represented object.
     * @param serverAddress     the O2S Gw address.
     * @param serverPort        the O2S Gw port.
     * @param clientPubCertFile the file path for current client's public certificate.
     * @param serverPubCertFile the file path for O2S Gw server's public certificate.
     */
    public JODGwO2SClient(JODCommunication_002 communication, JODObjectInfo objInfo,
                          InetAddress serverAddress, int serverPort,
                          String clientPubCertFile, String serverPubCertFile) {
        this.communication = communication;

        // Create ssl context
        SSLContext sslCtx;
        try {
            KeyStore clientKeyStore = UtilsJKS.generateKeyStore(objInfo.getObjId(), "", CERT_ALIAS);
            UtilsJKS.exportCertificate(clientKeyStore, clientPubCertFile, CERT_ALIAS);
            DynAddTrustManager clientTrustManager = new DynAddTrustManager();
            clientTrustManager.addCertificate(JCP_CERT_ALIAS, new File(serverPubCertFile));
            sslCtx = UtilsSSL.generateSSLContext(clientKeyStore, "", clientTrustManager);

        } catch (UtilsSSL.GenerationException | UtilsJKS.GenerationException | AbsCustomTrustManager.UpdateException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // Init SSL client
        client = new DefaultSSLClient(sslCtx, objInfo.getObjId(), serverAddress, serverPort,
                null, null, new GwO2SClientMessagingEventsListener());
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
        communication.forwardAction(readData);
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
