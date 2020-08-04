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

package com.robypomper.communication.server;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.server.events.ServerClientEvents;
import com.robypomper.communication.server.events.ServerLocalEvents;
import com.robypomper.communication.server.events.ServerMessagingEvents;
import com.robypomper.communication.server.standard.SSLCertServer;
import com.robypomper.communication.trustmanagers.AbsCustomTrustManager;
import com.robypomper.communication.trustmanagers.DynAddTrustManager;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.net.InetAddress;
import java.security.KeyStore;
import java.util.List;


/**
 * SSL Server with certificate sharing implementation of Server interface.
 * <p>
 * This implementation provide a SSL based server and a {@link SSLCertServer}
 * instance used to share certificates between client and server.
 */
public class CertSharingSSLServer implements Server {

    // Internal vars

    protected static final Logger log = LogManager.getLogger();
    private final String keyStorePath;
    private final String keyStorePass;
    private final DynAddTrustManager trustManager = new DynAddTrustManager();
    private final KeyStore serverKeyStore;
    private final DefaultSSLServer sslServer;
    private final SSLCertServer certServer;


    // Constructor

    /**
     * CertSharingSSLServer full constructor.
     *
     * @param serverId                      the server id.
     * @param port                          the server's port.
     * @param certAlias                     the server's certificate alias.
     * @param certPubPath                   the server's public certificate export path.
     * @param requireAuth                   if set to <code>true</code> the server require
     *                                      authentication from clients (client must provide their
     *                                      certificate).
     * @param serverMessagingEventsListener the tx and rx messaging listener.
     */
    public CertSharingSSLServer(String serverId, int port,
                                String certAlias, String certPubPath, boolean requireAuth,
                                ServerMessagingEvents serverMessagingEventsListener) throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertServer.SSLCertServerException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
        this(serverId, port, null, null, certAlias, certPubPath, requireAuth, null, null, serverMessagingEventsListener);
    }

    /**
     * CertSharingSSLServer full constructor.
     *
     * @param serverId                      the server id.
     * @param port                          the server's port.
     * @param certAlias                     the server's certificate alias.
     * @param certPubPath                   the server's public certificate export path.
     * @param requireAuth                   if set to <code>true</code> the server require
     *                                      authentication from clients (client must provide their
     *                                      certificate).
     * @param serverLocalEventsListener     the local server events listener.
     * @param serverClientEventsListener    the clients events listener.
     * @param serverMessagingEventsListener the tx and rx messaging listener.
     */
    public CertSharingSSLServer(String serverId, int port,
                                String certAlias, String certPubPath, boolean requireAuth,
                                ServerLocalEvents serverLocalEventsListener,
                                ServerClientEvents serverClientEventsListener,
                                ServerMessagingEvents serverMessagingEventsListener) throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertServer.SSLCertServerException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
        this(serverId, port, null, null, certAlias, certPubPath, requireAuth, serverLocalEventsListener, serverClientEventsListener, serverMessagingEventsListener);
    }

    /**
     * CertSharingSSLServer full constructor.
     *
     * @param serverId                      the server id.
     * @param port                          the server's port.
     * @param keyStorePath                  the server's keystore file path.
     * @param keyStorePass                  the server's keystore password.
     * @param certAlias                     the server's certificate alias.
     * @param certPubPath                   the server's public certificate export path.
     * @param requireAuth                   if set to <code>true</code> the server require
     *                                      authentication from clients (client must provide their
     *                                      certificate).
     * @param serverMessagingEventsListener the tx and rx messaging listener.
     */
    public CertSharingSSLServer(String serverId, int port,
                                String keyStorePath, String keyStorePass, String certAlias, String certPubPath, boolean requireAuth,
                                ServerMessagingEvents serverMessagingEventsListener) throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertServer.SSLCertServerException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
        this(serverId, port, keyStorePath, keyStorePass, certAlias, certPubPath, requireAuth, null, null, serverMessagingEventsListener);
    }

    /**
     * CertSharingSSLServer full constructor.
     *
     * @param serverId                      the server id.
     * @param port                          the server's port.
     * @param keyStorePath                  the server's keystore file path.
     * @param keyStorePass                  the server's keystore password.
     * @param certAlias                     the server's certificate alias.
     * @param certPubPath                   the server's public certificate export path.
     * @param requireAuth                   if set to <code>true</code> the server require
     *                                      authentication from clients (client must provide their
     *                                      certificate).
     * @param serverLocalEventsListener     the local server events listener.
     * @param serverClientEventsListener    the clients events listener.
     * @param serverMessagingEventsListener the tx and rx messaging listener.
     */
    public CertSharingSSLServer(String serverId, int port,
                                String keyStorePath, String keyStorePass, String certAlias, String certPubPath, boolean requireAuth,
                                ServerLocalEvents serverLocalEventsListener,
                                ServerClientEvents serverClientEventsListener,
                                ServerMessagingEvents serverMessagingEventsListener) throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertServer.SSLCertServerException, UtilsJKS.LoadingException, UtilsJKS.StoreException {

        this.keyStorePath = keyStorePath;
        this.keyStorePass = keyStorePass;

        // (Load) or (generate and [store])
        File keyStoreFile = null;
        if (keyStorePath != null)
            keyStoreFile = new File(keyStorePath);
        if (keyStoreFile != null && keyStoreFile.exists()) {
            serverKeyStore = UtilsJKS.loadKeyStore(keyStorePath, keyStorePass);
            UtilsJKS.copyCertsFromKeyStoreToTrustManager(serverKeyStore, trustManager);
        } else {
            serverKeyStore = UtilsJKS.generateKeyStore(serverId, keyStorePass, certAlias);
            UtilsJKS.copyCertsFromKeyStoreToTrustManager(serverKeyStore, trustManager);
            if (keyStoreFile != null)
                updateAndStoreKeyStore();
        }

        // Export certPath
        UtilsJKS.exportCertificate(serverKeyStore, certPubPath, certAlias);

        // Init ssl instances
        SSLContext sslCtx = UtilsSSL.generateSSLContext(serverKeyStore, keyStorePass, trustManager);

        SSLCertServer.SSLCertServerListener certListener = new SSLCertServer.SSLCertServerListener() {
            @Override
            public void onCertificateSend(ClientInfo client) {
                log.debug(Mrk_Commons.COMM_SSL_CERTSRV, String.format("Server '%s' send certificate to client '%s'", getServerId(), client.getClientId()));
            }

            @Override
            public void onCertificateStored(AbsCustomTrustManager certTrustManager, ClientInfo client) {
                log.debug(Mrk_Commons.COMM_SSL_CERTSRV, String.format("Server '%s' stored certificate from client '%s'", getServerId(), client.getClientId()));
                try {
                    updateAndStoreKeyStore();
                } catch (UtilsJKS.LoadingException | UtilsJKS.StoreException e) {
                    log.warn(Mrk_Commons.COMM_SSL_CERTSRV, String.format("Server '%s' can't store keystore on file '%s'", getServerId(), keyStorePath));
                }
            }
        };
        this.sslServer = new DefaultSSLServer(sslCtx, serverId, port, requireAuth,
                serverLocalEventsListener,
                serverClientEventsListener,
                serverMessagingEventsListener);
        this.certServer = new SSLCertServer("_CERT_" + serverId, port + 1, certPubPath, trustManager, certListener);
    }


    // Utils methods

    /**
     * Copy al certificates stored in the internal {@link #trustManager} to the
     * KeyStore (if was set during initialization) and store to the file.
     */
    private void updateAndStoreKeyStore() throws UtilsJKS.LoadingException, UtilsJKS.StoreException {
        if (keyStorePath == null)
            return;

        UtilsJKS.copyCertsFromTrustManagerToKeyStore(serverKeyStore, trustManager);
        UtilsJKS.storeKeyStore(serverKeyStore, keyStorePath, keyStorePass);
    }


    // Server getter

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getAddress() {
        return sslServer.getAddress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort() {
        return sslServer.getPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerId() {
        return sslServer.getServerId();
    }


    // Server listening methods

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRunning() {
        return sslServer.isRunning();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws ListeningException {
        certServer.start();
        sslServer.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        sslServer.stop();
        certServer.stop();
    }


    // Messages methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(String clientId, byte[] data) throws ServerStoppedException, ClientNotFoundException, ClientNotConnectedException {
        sslServer.sendData(clientId, data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(ClientInfo client, byte[] data) throws ServerStoppedException, ClientNotConnectedException {
        sslServer.sendData(client, data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(String clientId, String data) throws ServerStoppedException, ClientNotFoundException, ClientNotConnectedException {
        sslServer.sendData(clientId, data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(ClientInfo client, String data) throws ServerStoppedException, ClientNotConnectedException {
        sslServer.sendData(client, data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCliByeMsg(byte[] data) {
        return sslServer.isCliByeMsg(data);
    }


    // Clients mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ClientInfo> getClients() {
        return sslServer.getClients();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientInfo findClientById(String clientId) {
        return sslServer.findClientById(clientId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientInfo getClientById(String clientId) throws ClientNotFoundException {
        return sslServer.getClientById(clientId);
    }

}
