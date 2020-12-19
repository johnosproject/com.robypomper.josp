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

package com.robypomper.communication.client.standard;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.client.AbsSSLClient;
import com.robypomper.communication.client.AbsSSLClientCertSharing;
import com.robypomper.communication.client.events.ClientLocalEvents;
import com.robypomper.communication.client.events.ClientMessagingEvents;
import com.robypomper.communication.client.events.ClientServerEvents;


/**
 * Default implementation of {@link AbsSSLClient}.
 */
@SuppressWarnings("unused")
public class DefaultSSLClientCertSharing extends AbsSSLClientCertSharing {

    // Internal vars

    private final String protoName;
    private final String serverName;


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
    public DefaultSSLClientCertSharing(String clientId, String serverAddr, int serverPort,
                                       String certAlias,
                                       ClientMessagingEvents clientMessagingEventsListener,
                                       String protoName, String serverName)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertSharingClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
        this(clientId, serverAddr, serverPort, null, null, certAlias, null, null, null, clientMessagingEventsListener, protoName, serverName);
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
    public DefaultSSLClientCertSharing(String clientId, String serverAddr, int serverPort,
                                       String certAlias,
                                       ClientLocalEvents clientLocalEventsListener,
                                       ClientServerEvents clientServerEventsListener,
                                       ClientMessagingEvents clientMessagingEventsListener,
                                       String protoName, String serverName)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertSharingClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
        this(clientId, serverAddr, serverPort, null, null, certAlias, null, clientLocalEventsListener, clientServerEventsListener, clientMessagingEventsListener, protoName, serverName);
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
    public DefaultSSLClientCertSharing(String clientId, String serverAddr, int serverPort,
                                       String keyStorePath, String keyStorePass, String certAlias,
                                       ClientMessagingEvents clientMessagingEventsListener,
                                       String protoName, String serverName)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertSharingClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
        this(clientId, serverAddr, serverPort, keyStorePath, keyStorePass, certAlias, null, null, null, clientMessagingEventsListener, protoName, serverName);
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
    public DefaultSSLClientCertSharing(String clientId, String serverAddr, int serverPort,
                                       String keyStorePath, String keyStorePass, String certAlias,
                                       ClientLocalEvents clientLocalEventsListener,
                                       ClientServerEvents clientServerEventsListener,
                                       ClientMessagingEvents clientMessagingEventsListener,
                                       String protoName, String serverName)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertSharingClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
        this(clientId, serverAddr, serverPort, keyStorePath, keyStorePass, certAlias, null, clientLocalEventsListener, clientServerEventsListener, clientMessagingEventsListener, protoName, serverName);
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
    public DefaultSSLClientCertSharing(String clientId, String serverAddr, int serverPort,
                                       String certAlias, String certPubPath,
                                       ClientMessagingEvents clientMessagingEventsListener,
                                       String protoName, String serverName)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertSharingClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
        this(clientId, serverAddr, serverPort, null, null, certAlias, certPubPath, null, null, clientMessagingEventsListener, protoName, serverName);
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
    public DefaultSSLClientCertSharing(String clientId, String serverAddr, int serverPort,
                                       String certAlias, String certPubPath,
                                       ClientLocalEvents clientLocalEventsListener,
                                       ClientServerEvents clientServerEventsListener,
                                       ClientMessagingEvents clientMessagingEventsListener,
                                       String protoName, String serverName)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertSharingClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
        this(clientId, serverAddr, serverPort, null, null, certAlias, certPubPath, clientLocalEventsListener, clientServerEventsListener, clientMessagingEventsListener, protoName, serverName);
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
    public DefaultSSLClientCertSharing(String clientId, String serverAddr, int serverPort,
                                       String keyStorePath, String keyStorePass, String certAlias, String certPubPath,
                                       ClientMessagingEvents clientMessagingEventsListener,
                                       String protoName, String serverName)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertSharingClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
        this(clientId, serverAddr, serverPort, keyStorePath, keyStorePass, certAlias, certPubPath, null, null, clientMessagingEventsListener, protoName, serverName);
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
    public DefaultSSLClientCertSharing(String clientId, String serverAddr, int serverPort,
                                       String keyStorePath, String keyStorePass, String certAlias, String certPubPath,
                                       ClientLocalEvents clientLocalEventsListener,
                                       ClientServerEvents clientServerEventsListener,
                                       ClientMessagingEvents clientMessagingEventsListener,
                                       String protoName, String serverName)
            throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, SSLCertSharingClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsJKS.StoreException {
        super(clientId, serverAddr, serverPort, keyStorePath, keyStorePass, certAlias, certPubPath, clientLocalEventsListener, clientServerEventsListener, clientMessagingEventsListener);
        this.protoName = protoName;
        this.serverName = serverName;
    }


    // Getter configs

    @Override
    public String getProtocolName() {
        return protoName;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

}
