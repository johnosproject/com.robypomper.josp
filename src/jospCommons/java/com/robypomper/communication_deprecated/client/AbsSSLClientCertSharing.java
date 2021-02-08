/* *****************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.communication_deprecated.client;

import com.robypomper.communication_deprecated.UtilsJKS;
import com.robypomper.communication_deprecated.UtilsSSL;
import com.robypomper.communication_deprecated.client.events.ClientLocalEvents;
import com.robypomper.communication_deprecated.client.events.ClientMessagingEvents;
import com.robypomper.communication_deprecated.client.events.ClientServerEvents;
import com.robypomper.communication_deprecated.client.standard.DefaultSSLClient;
import com.robypomper.communication_deprecated.client.standard.SSLCertSharingClient;
import com.robypomper.communication_deprecated.trustmanagers.AbsCustomTrustManager;
import com.robypomper.communication_deprecated.trustmanagers.DynAddTrustManager;
import com.robypomper.josp.states.StateException;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * SSL Client with certificate sharing implementation of Client interface.
 * <p>
 * This implementation provide a SSL based client and a {@link SSLCertSharingClient}
 * instance used to share certificates between client and server.
 */
@SuppressWarnings("unused")
public abstract class AbsSSLClientCertSharing extends AbsClientWrapper {

    // Internal vars

    protected static final Logger log = LogManager.getLogger();

    // Configs
    private final String certPubPath;
    private final String keyStorePath;
    private final String keyStorePass;
    // SSL
    private final DynAddTrustManager trustManager = new DynAddTrustManager();
    private final KeyStore clientKeyStore;
    private final CountDownLatch certStoredLatch = new CountDownLatch(1);
    private final SSLCertSharingClient certSharingClient;


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
    public AbsSSLClientCertSharing(String clientId, String serverAddr, int serverPort,
                                   String certAlias,
                                   ClientMessagingEvents clientMessagingEventsListener)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertSharingClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
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
    public AbsSSLClientCertSharing(String clientId, String serverAddr, int serverPort,
                                   String certAlias,
                                   ClientLocalEvents clientLocalEventsListener,
                                   ClientServerEvents clientServerEventsListener,
                                   ClientMessagingEvents clientMessagingEventsListener)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertSharingClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
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
    public AbsSSLClientCertSharing(String clientId, String serverAddr, int serverPort,
                                   String keyStorePath, String keyStorePass, String certAlias,
                                   ClientMessagingEvents clientMessagingEventsListener)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertSharingClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
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
    public AbsSSLClientCertSharing(String clientId, String serverAddr, int serverPort,
                                   String keyStorePath, String keyStorePass, String certAlias,
                                   ClientLocalEvents clientLocalEventsListener,
                                   ClientServerEvents clientServerEventsListener,
                                   ClientMessagingEvents clientMessagingEventsListener)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertSharingClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
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
    public AbsSSLClientCertSharing(String clientId, String serverAddr, int serverPort,
                                   String certAlias, String certPubPath,
                                   ClientMessagingEvents clientMessagingEventsListener)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertSharingClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
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
    public AbsSSLClientCertSharing(String clientId, String serverAddr, int serverPort,
                                   String certAlias, String certPubPath,
                                   ClientLocalEvents clientLocalEventsListener,
                                   ClientServerEvents clientServerEventsListener,
                                   ClientMessagingEvents clientMessagingEventsListener)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertSharingClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
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
    public AbsSSLClientCertSharing(String clientId, String serverAddr, int serverPort,
                                   String keyStorePath, String keyStorePass, String certAlias, String certPubPath,
                                   ClientMessagingEvents clientMessagingEventsListener)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertSharingClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
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
    public AbsSSLClientCertSharing(String clientId, String serverAddr, int serverPort,
                                   String keyStorePath, String keyStorePass, String certAlias, String certPubPath,
                                   ClientLocalEvents clientLocalEventsListener,
                                   ClientServerEvents clientServerEventsListener,
                                   ClientMessagingEvents clientMessagingEventsListener)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertSharingClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
        super(clientId);

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
            UtilsJKS.copyCertsFromKeyStoreToTrustManager(clientKeyStore, trustManager);
            if (keyStoreFile != null)
                updateAndStoreKeyStore();
        }

        // Export certPath
        if (certPubPath != null)
            UtilsJKS.exportCertificate(clientKeyStore, certPubPath, certAlias);
        this.certPubPath = certPubPath;

        // Init ssl instances
        SSLContext sslCtx = UtilsSSL.generateSSLContext(clientKeyStore, keyStorePass, trustManager);

        Client sslClient = new DefaultSSLClient(sslCtx, clientId, serverAddr, serverPort,
                clientLocalEventsListener, clientServerEventsListener, clientMessagingEventsListener,
                getProtocolName(), getServerName()
        );
        setWrappedClient(sslClient);
        this.certSharingClient = new SSLCertSharingClient("_CERT_" + getClientId(), getServerAddr(), getServerPort() + 1, certPubPath, trustManager,
                new SSLCertSharingClient.SSLCertClientListener() {
                    @Override
                    public void onCertificateSend() {
                        log.debug(Mrk_Commons.COMM_SSL_CERTCL, String.format("Client '%s' send certificate to server '%s:%d'", getClientId(), getServerAddr(), getServerPort()));
                    }

                    @Override
                    public void onCertificateStored(AbsCustomTrustManager certTrustManager) {
                        log.debug(Mrk_Commons.COMM_SSL_CERTCL, String.format("Client '%s' stored certificate from server '%s:%d'", getClientId(), getServerAddr(), getServerPort()));
                        try {
                            updateAndStoreKeyStore();
                        } catch (UtilsJKS.LoadingException | UtilsJKS.StoreException e) {
                            log.warn(Mrk_Commons.COMM_SSL_CERTCL, String.format("Client '%s' can't store keystore on file '%s'", getClientId(), keyStorePath));
                        }
                        certStoredLatch.countDown();
                    }
                }
        );
    }


    // Getter configs

    /**
     * @return client's public certificate path.
     */
    public String getCertPubPath() {
        return certPubPath;
    }


    // Client connection methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws IOException, AAAException, StateException {
        try {
            super.connect();

        } catch (SSLException ignore) {
            log.debug(Mrk_Commons.COMM_SSL_CERTCL, String.format("Client '%s' can't connect, register/retrieve certificates with Cert Sharing server '%s:%d'", getClientId(), getServerAddr(), getServerPort()));

            // Get server's cert
            certSharingClient.connect();
            boolean stored = false;
            try {
                stored = certStoredLatch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException ignore2) {
            }

            if (!stored)
                throw new AAAException(this, "Can't get server's public certificate from Cert Sharing server");

            // 2nd try connect
            super.disconnect();
            super.connect();
        }

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

}
