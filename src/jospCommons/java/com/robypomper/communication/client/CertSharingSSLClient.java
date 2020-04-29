package com.robypomper.communication.client;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.client.events.ClientLocalEvents;
import com.robypomper.communication.client.events.ClientMessagingEvents;
import com.robypomper.communication.client.events.ClientServerEvents;
import com.robypomper.communication.client.standard.SSLCertClient;
import com.robypomper.communication.trustmanagers.AbsCustomTrustManager;
import com.robypomper.communication.trustmanagers.DynAddTrustManager;
import com.robypomper.log.Markers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.net.InetAddress;
import java.security.KeyStore;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * SSL Client with certificate sharing implementation of Client interface.
 * <p>
 * This implementation provide a SSL based client and a {@link SSLCertClient}
 * instance used to share certificates between client and server.
 */
public class CertSharingSSLClient implements Client {

    // Internal vars

    protected static final Logger log = LogManager.getLogger();
    private final String keyStorePath;
    private final String keyStorePass;
    private final DynAddTrustManager trustManager = new DynAddTrustManager();
    private final KeyStore clientKeyStore;
    private final DefaultSSLClient sslClient;
    private final SSLCertClient certClient;
    private final CountDownLatch certStoredLatch = new CountDownLatch(1);


    // Constructor

    /**
     * CertSharingSSLClient full constructor.
     *
     * @param clientId                      the client id.
     * @param serverAddr                    the server's address.
     * @param serverPort                    the server's port.
     * @param certAlias                     the client's certificate alias.
     * @param clientMessagingEventsListener the tx and rx messaging listener.
     */
    public CertSharingSSLClient(String clientId, InetAddress serverAddr, int serverPort,
                                String certAlias,
                                ClientMessagingEvents clientMessagingEventsListener)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
        this(clientId, serverAddr, serverPort, null, null, certAlias, null, null, null, clientMessagingEventsListener);
    }

    /**
     * CertSharingSSLClient full constructor.
     *
     * @param clientId                      the client id.
     * @param serverAddr                    the server's address.
     * @param serverPort                    the server's port.
     * @param certAlias                     the client's certificate alias.
     * @param clientLocalEventsListener     the local client events listener.
     * @param clientServerEventsListener    the server events listener.
     * @param clientMessagingEventsListener the tx and rx messaging listener.
     */
    public CertSharingSSLClient(String clientId, InetAddress serverAddr, int serverPort,
                                String certAlias,
                                ClientLocalEvents clientLocalEventsListener,
                                ClientServerEvents clientServerEventsListener,
                                ClientMessagingEvents clientMessagingEventsListener)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
        this(clientId, serverAddr, serverPort, null, null, certAlias, null, clientLocalEventsListener, clientServerEventsListener, clientMessagingEventsListener);
    }

    /**
     * CertSharingSSLClient full constructor.
     *
     * @param clientId                      the client id.
     * @param serverAddr                    the server's address.
     * @param serverPort                    the server's port.
     * @param keyStorePath                  the client's keystore file path.
     * @param keyStorePass                  the client's keystore password.
     * @param certAlias                     the client's certificate alias.
     * @param clientMessagingEventsListener the tx and rx messaging listener.
     */
    public CertSharingSSLClient(String clientId, InetAddress serverAddr, int serverPort,
                                String keyStorePath, String keyStorePass, String certAlias,
                                ClientMessagingEvents clientMessagingEventsListener)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
        this(clientId, serverAddr, serverPort, keyStorePath, keyStorePass, certAlias, null, null, null, clientMessagingEventsListener);
    }

    /**
     * CertSharingSSLClient full constructor.
     *
     * @param clientId                      the client id.
     * @param serverAddr                    the server's address.
     * @param serverPort                    the server's port.
     * @param keyStorePath                  the client's keystore file path.
     * @param keyStorePass                  the client's keystore password.
     * @param certAlias                     the client's certificate alias.
     * @param clientLocalEventsListener     the local client events listener.
     * @param clientServerEventsListener    the server events listener.
     * @param clientMessagingEventsListener the tx and rx messaging listener.
     */
    public CertSharingSSLClient(String clientId, InetAddress serverAddr, int serverPort,
                                String keyStorePath, String keyStorePass, String certAlias,
                                ClientLocalEvents clientLocalEventsListener,
                                ClientServerEvents clientServerEventsListener,
                                ClientMessagingEvents clientMessagingEventsListener)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
        this(clientId, serverAddr, serverPort, keyStorePath, keyStorePass, certAlias, null, clientLocalEventsListener, clientServerEventsListener, clientMessagingEventsListener);
    }

    /**
     * CertSharingSSLClient full constructor.
     *
     * @param clientId                      the client id.
     * @param serverAddr                    the server's address.
     * @param serverPort                    the server's port.
     * @param certAlias                     the client's certificate alias.
     * @param certPubPath                   the client's public certificate export path.
     * @param clientMessagingEventsListener the tx and rx messaging listener.
     */
    public CertSharingSSLClient(String clientId, InetAddress serverAddr, int serverPort,
                                String certAlias, String certPubPath,
                                ClientMessagingEvents clientMessagingEventsListener)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
        this(clientId, serverAddr, serverPort, null, null, certAlias, certPubPath, null, null, clientMessagingEventsListener);
    }

    /**
     * CertSharingSSLClient full constructor.
     *
     * @param clientId                      the client id.
     * @param serverAddr                    the server's address.
     * @param serverPort                    the server's port.
     * @param certAlias                     the client's certificate alias.
     * @param certPubPath                   the client's public certificate export path.
     * @param clientLocalEventsListener     the local client events listener.
     * @param clientServerEventsListener    the server events listener.
     * @param clientMessagingEventsListener the tx and rx messaging listener.
     */
    public CertSharingSSLClient(String clientId, InetAddress serverAddr, int serverPort,
                                String certAlias, String certPubPath,
                                ClientLocalEvents clientLocalEventsListener,
                                ClientServerEvents clientServerEventsListener,
                                ClientMessagingEvents clientMessagingEventsListener)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
        this(clientId, serverAddr, serverPort, null, null, certAlias, certPubPath, clientLocalEventsListener, clientServerEventsListener, clientMessagingEventsListener);
    }

    /**
     * CertSharingSSLClient full constructor.
     *
     * @param clientId                      the client id.
     * @param serverAddr                    the server's address.
     * @param serverPort                    the server's port.
     * @param keyStorePath                  the client's keystore file path.
     * @param keyStorePass                  the client's keystore password.
     * @param certAlias                     the client's certificate alias.
     * @param certPubPath                   the client's public certificate export path.
     * @param clientMessagingEventsListener the tx and rx messaging listener.
     */
    public CertSharingSSLClient(String clientId, InetAddress serverAddr, int serverPort,
                                String keyStorePath, String keyStorePass, String certAlias, String certPubPath,
                                ClientMessagingEvents clientMessagingEventsListener)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
        this(clientId, serverAddr, serverPort, keyStorePath, keyStorePass, certAlias, certPubPath, null, null, clientMessagingEventsListener);
    }

    /**
     * CertSharingSSLClient full constructor.
     *
     * @param clientId                      the client id.
     * @param serverAddr                    the server's address.
     * @param serverPort                    the server's port.
     * @param keyStorePath                  the client's keystore file path.
     * @param keyStorePass                  the client's keystore password.
     * @param certAlias                     the client's certificate alias.
     * @param certPubPath                   the client's public certificate export path.
     * @param clientLocalEventsListener     the local client events listener.
     * @param clientServerEventsListener    the server events listener.
     * @param clientMessagingEventsListener the tx and rx messaging listener.
     */
    public CertSharingSSLClient(String clientId, InetAddress serverAddr, int serverPort,
                                String keyStorePath, String keyStorePass, String certAlias, String certPubPath,
                                ClientLocalEvents clientLocalEventsListener,
                                ClientServerEvents clientServerEventsListener,
                                ClientMessagingEvents clientMessagingEventsListener)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {

        this.keyStorePath = keyStorePath;
        this.keyStorePass = keyStorePass;

        // (Load) or (generate and [store])
        File keyStoreFile = null;
        if (keyStorePath != null)
            keyStoreFile = new File(keyStorePath);
        if (keyStoreFile != null && keyStoreFile.exists()) {
            clientKeyStore = UtilsJKS.loadKeyStore(keyStorePath, keyStorePass);
            UtilsJKS.copyCertsFromKeyStoreToTrustManager(clientKeyStore, trustManager);
        } else {
            clientKeyStore = UtilsJKS.generateKeyStore(clientId, keyStorePass, certAlias);
            updateAndStoreKeyStore();
        }

        // Export certPath
        if (certPubPath != null && !new File(certPubPath).exists())
            UtilsJKS.exportCertificate(clientKeyStore, certPubPath, certAlias);

        // Init ssl instances
        SSLContext sslCtx = UtilsSSL.generateSSLContext(clientKeyStore, keyStorePass, trustManager);

        SSLCertClient.SSLCertClientListener certListener = new SSLCertClient.SSLCertClientListener() {
            @Override
            public void onCertificateSend() {
                log.debug(Markers.COMM_SSL_CERTSRV, String.format("Client '%s' send certificate to server '%s:%d'", getClientId(), getServerAddr(), getServerPort()));
            }

            @Override
            public void onCertificateStored(AbsCustomTrustManager certTrustManager) {
                log.debug(Markers.COMM_SSL_CERTSRV, String.format("Client '%s' stored certificate from server '%s:%d'", getClientId(), getServerAddr(), getServerPort()));
                try {
                    updateAndStoreKeyStore();
                } catch (UtilsJKS.LoadingException | UtilsJKS.StoreException e) {
                    log.warn(Markers.COMM_SSL_CERTSRV, String.format("Client '%s' can't store keystore on file '%s'", getClientId(), keyStorePath));
                }
                certStoredLatch.countDown();
            }
        };
        this.sslClient = new DefaultSSLClient(sslCtx, clientId, serverAddr, serverPort,
                clientLocalEventsListener,
                clientServerEventsListener,
                clientMessagingEventsListener);
        this.certClient = new SSLCertClient(clientId + "-CERT", serverAddr, serverPort + 1, certPubPath, trustManager, certListener);
    }


    // Utils methods

    /**
     * Copy al certificates stored in the internal {@link #trustManager} to the
     * KeyStore (if was set during initialization) and store to the file.
     */
    private void updateAndStoreKeyStore() throws UtilsJKS.LoadingException, UtilsJKS.StoreException {
        if (keyStorePath == null)
            return;

        UtilsJKS.copyCertsFromTrustManagerToKeyStore(clientKeyStore, trustManager);
        UtilsJKS.storeKeyStore(clientKeyStore, keyStorePath, keyStorePass);
    }


    // Client getter

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getServerAddr() {
        return sslClient.getServerAddr();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getServerPort() {
        return sslClient.getServerPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServerInfo getServerInfo() {
        return sslClient.getServerInfo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientId() {
        return sslClient.getClientId();
    }


    // Client connection methods

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return sslClient.isConnected();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws ConnectionException {
        try {
            sslClient.connect();
        } catch (ConnectionException ignore) {
            log.debug(Markers.COMM_SSL_CERTSRV, String.format("Client '%s' can't connect, sharing certificate with server '%s:%d'", getClientId(), getServerAddr(), getServerPort()));

            // Get server's cert
            certClient.connect();
            try {
                certStoredLatch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new ConnectionException("Can't get server public certificate");
            }

            // 2nd try connect
            sslClient.connect();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        sslClient.disconnect();
    }


    // Messages methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(byte[] data) throws ServerNotConnectedException {
        sslClient.sendData(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(String data) throws ServerNotConnectedException {
        sslClient.sendData(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSrvByeMsg(byte[] data) {
        return sslClient.isSrvByeMsg(data);
    }

}
