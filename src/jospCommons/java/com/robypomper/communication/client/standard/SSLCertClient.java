package com.robypomper.communication.client.standard;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.client.DefaultClient;
import com.robypomper.communication.client.events.ClientMessagingEvents;
import com.robypomper.communication.client.events.ClientServerEvents;
import com.robypomper.communication.client.events.DefaultClientEvents;
import com.robypomper.communication.client.events.LogClientLocalEventsListener;
import com.robypomper.communication.server.standard.SSLCertServer;
import com.robypomper.communication.trustmanagers.AbsCustomTrustManager;
import com.robypomper.log.Markers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;


/**
 * SSL sharing Certificate client.
 * <p>
 * This is a {@link DefaultClient} implementation that allow client to share
 * SSL certificates with the server. When the client connects to the server, it
 * send his public certificate (only if <code>certPubPath</code> was not null
 * during SSLCertClient initialization. To the other side server send, always,
 * his public certificate to the client when the connection is setup. Then the
 * client store this certificate to the associated TrustManager.
 * <p>
 * The TrustManager managed by this class can be used to initialize a
 * {@link javax.net.ssl.SSLContext} so a client can connect to a SSL server
 * without previously key sharing.
 */
public class SSLCertClient extends DefaultClient {

    // Internal vars

    protected static final Logger log = LogManager.getLogger();
    private final File certPubFile;
    private final AbsCustomTrustManager certTrustManager;
    private byte[] serverCertBuffer = new byte[0];
    private final SSLCertClientListener listener;


    // Constructor

    /**
     * Default constructor for SSLCertClient instance that don't share his
     * certificate (client side) with the server.
     *
     * @param clientId         the client id.
     * @param serverAddr       the server's address.
     * @param serverPort       the server's port.
     * @param certTrustManager instance of TrustManager where client's keys will
     *                         be stored.
     */
    protected SSLCertClient(String clientId, InetAddress serverAddr, int serverPort,
                            AbsCustomTrustManager certTrustManager) throws SSLCertClientException {
        this(clientId, serverAddr, serverPort, null, certTrustManager, null);
    }

    /**
     * Default constructor for SSLCertClient instance that don't share his
     * certificate (client side) with the server.
     *
     * @param clientId         the client id.
     * @param serverAddr       the server's address.
     * @param serverPort       the server's port.
     * @param certPubPath      server's public certificate file's path, if
     *                         <code>null</code> then the {@link SSLCertClient}
     *                         don't send his certificate to the server.
     * @param certTrustManager instance of TrustManager where client's keys will
     *                         be stored.
     */
    protected SSLCertClient(String clientId, InetAddress serverAddr, int serverPort, String certPubPath,
                            AbsCustomTrustManager certTrustManager) throws SSLCertClientException {
        this(clientId, serverAddr, serverPort, certPubPath, certTrustManager, null);
    }

    /**
     * Default constructor for SSLCertClient instance that don't share his
     * certificate (client side) with the server.
     *
     * @param clientId         the client id.
     * @param serverAddr       the server's address.
     * @param serverPort       the server's port.
     * @param certTrustManager instance of TrustManager where client's keys will
     *                         be stored.
     * @param listener         listener for certificate events (send and stored).
     */
    protected SSLCertClient(String clientId, InetAddress serverAddr, int serverPort,
                            AbsCustomTrustManager certTrustManager, SSLCertClientListener listener) throws SSLCertClientException {
        this(clientId, serverAddr, serverPort, null, certTrustManager, listener);
    }

    /**
     * Default constructor.
     *
     * @param clientId         the client id.
     * @param serverAddr       the server's address.
     * @param serverPort       the server's port.
     * @param certPubPath      server's public certificate file's path, if
     *                         <code>null</code> then the {@link SSLCertClient}
     *                         don't send his certificate to the server.
     * @param certTrustManager instance of TrustManager where client's keys will
     *                         be stored.
     * @param listener         listener for certificate events (send and stored).
     */
    public SSLCertClient(String clientId, InetAddress serverAddr, int serverPort, String certPubPath,
                         AbsCustomTrustManager certTrustManager, SSLCertClientListener listener) throws SSLCertClientException {
        super(clientId, serverAddr, serverPort,
                new LogClientLocalEventsListener(),
                new SSLCertClientServerEventsListener(),
                new SSLCertClientMessagingEventsListener());

        if (certPubPath != null) {
            this.certPubFile = new File(certPubPath);
            if (!certPubFile.exists())
                throw new SSLCertClientException(String.format("Public cert file '%s' not found.", certPubPath));
        } else
            this.certPubFile = null;
        this.certTrustManager = certTrustManager;
        this.listener = listener;
    }


    // Clients's events override

    /**
     * This method is executed on event
     * {@link SSLCertClientServerEventsListener#onServerConnection()}
     * <p>
     * Send client's public certificate to the connected server.
     * <p>
     * Read certificate file and tx to the server.
     */
    private void sendClientCertificate() {
        if (certPubFile == null)
            return;

        try {
            FileInputStream in = new FileInputStream(certPubFile);
            byte[] dataRead = new byte[in.available()];

            while (in.read(dataRead) != -1) {
                //dataRead = trim(dataRead);
                sendData(dataRead);
            }
            log.debug(Markers.COMM_SSL_CERTCLI, String.format("Client send local certificate to server '%s'", getServerInfo().getServerId()));
            if (listener != null) listener.onCertificateSend();
            Thread.sleep(100);

        } catch (ServerNotConnectedException e) {
            log.warn(Markers.COMM_SSL_CERTCLI, String.format("Client disconnected, can't transmit local certificate to server '%s'", getServerInfo().getServerId()));

        } catch (IOException e) {
            log.warn(Markers.COMM_SSL_CERTCLI, String.format("Can't read public certificate '%s' because %s", certPubFile, e.getMessage()));

        } catch (InterruptedException ignore) {
        }
    }

    /**
     * Data received from server are related to server's certificate.
     * <p>
     * Because it's possible that the server send his certificate in multiple
     * chucks, this function use a buffer where store all sequential chucks
     * received from the server. Then for each chuck received it try to load
     * received bytes as certificate and then store it in the managed
     * TrustManager. If it fails, then buffer received data and wait for next
     * chuck.
     *
     * @param readData the data tx from the client containing the client's
     *                 certificate.
     * @return always <code>true</code> as the data were processed.
     */
    private boolean bufferServerCertificate(byte[] readData) {
        serverCertBuffer = concatenate(serverCertBuffer, readData);
        //System.out.println(String.format("Rx %d bytes", readData.length));
        //System.out.println(String.format("%s -> %s", clientCertPath, new String(readData).substring(0, 10)));

        if (tryStoreServerCertificate()) {
            log.info(Markers.COMM_SSL_CERTCLI, String.format("Client '%s' added certificate from server '%s' to trust store, disconnect", getClientId(), getServerInfo().getServerId()));
            disconnect();
        }

        return true;
    }

    /**
     * Support method for {@link #bufferServerCertificate(byte[])}
     * <p>
     * Every time climent receive data from the server, this method is called
     * and try to load certificate and store in the TrustManager.
     */
    private boolean tryStoreServerCertificate() {
        if (serverCertBuffer.length == 0) {
            log.debug(Markers.COMM_SSL_CERTCLI, String.format("Client '%s' don't received certificate from server '%s'", getClientId(), getServerInfo().getServerId()));
            return false;
        }

        log.debug(Markers.COMM_SSL_CERTCLI, String.format("Client '%s' try add certificate from server '%s' to trust store", getClientId(), getServerInfo().getServerId()));

        try {
            certTrustManager.addCertificateByte(String.format("SRV@%s:%d", getServerAddr(), getServerPort()), serverCertBuffer);
            if (listener != null) listener.onCertificateStored(certTrustManager);
            return true;

        } catch (AbsCustomTrustManager.UpdateException | UtilsJKS.LoadingException e) {
            log.debug(Markers.COMM_SSL_CERTCLI, String.format("Client '%s' can't add certificate from server '%s' because %s", getClientId(), getServerInfo().getServerId(), e.getMessage()));
            return false;
        }
    }

    /**
     * Utils method to concatenate two byte arrays.
     * <p>
     * This method is a copy of {@link SSLCertServer#concatenate(byte[], byte[])}.
     *
     * @param firstArray  the first part of returned array.
     * @param secondArray the second part of returned array.
     * @return the byte array composed by firstArray and secondArray arrays.
     */
    @SuppressWarnings("JavadocReference")
    private byte[] concatenate(byte[] firstArray, byte[] secondArray) {
        byte[] concatenated = new byte[firstArray.length + secondArray.length];
        System.arraycopy(firstArray, 0, concatenated, 0, firstArray.length);
        System.arraycopy(secondArray, 0, concatenated, firstArray.length, secondArray.length);
        return concatenated;
    }


    // Client event listeners

    /**
     * Link the {@link #onServerConnection()} event to
     * {@link SSLCertClient#sendClientCertificate()} method.
     */
    private static class SSLCertClientServerEventsListener extends DefaultClientEvents implements ClientServerEvents {

        // Server connection events

        /**
         * {@inheritDoc}
         * <p>
         * Link to the {@link SSLCertClient#sendClientCertificate()} method.
         */
        @Override
        public void onServerConnection() {
            ((SSLCertClient) getClient()).sendClientCertificate();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServerDisconnection() { }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServerClientDisconnected() {

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServerGoodbye() {

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServerTerminated() {

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServerError(Throwable e) {

        }

    }

    /**
     * Link the {@link #onDataReceived(byte[])} ()} event to
     * {@link SSLCertClient#bufferServerCertificate(byte[])} method.
     */
    private static class SSLCertClientMessagingEventsListener extends DefaultClientEvents implements ClientMessagingEvents {

        // Data send events

        /**
         * {@inheritDoc}
         */
        @Override
        public void onDataSend(byte[] writtenData) {

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onDataSend(String writtenData) {

        }


        // Data received events

        /**
         * {@inheritDoc}
         * <p>
         * Link to the {@link SSLCertClient#bufferServerCertificate(byte[])}
         * method.
         */
        @Override
        public boolean onDataReceived(byte[] readData) throws Throwable {
            return ((SSLCertClient) getClient()).bufferServerCertificate(readData);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean onDataReceived(String readData) throws Throwable {
            return false;
        }

    }


    // SSLCertClient event listeners

    /**
     * Interface for SSLCertClient listeners.
     */
    public interface SSLCertClientListener {

        /**
         * Event triggered when the client send his certificate to the server.
         */
        void onCertificateSend();

        /**
         * Event triggered when the client received and stored the server's
         * certificate.
         *
         * @param certTrustManager the {@link javax.net.ssl.TrustManager} where
         *                         received certificate was stored.
         */
        void onCertificateStored(AbsCustomTrustManager certTrustManager);

    }

    // Exception

    /**
     * Exception thrown on SSLCertServer errors.
     */
    public static class SSLCertClientException extends Throwable {
        public SSLCertClientException(String msg) {
            super(msg);
        }
    }

}
