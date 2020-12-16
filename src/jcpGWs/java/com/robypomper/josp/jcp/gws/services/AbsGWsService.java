/* *****************************************************************************
 * The John Cloud Platform set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.josp.jcp.gws.services;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.server.DefaultSSLServer;
import com.robypomper.communication.server.events.ServerClientEvents;
import com.robypomper.communication.server.events.ServerLocalEvents;
import com.robypomper.communication.server.events.ServerMessagingEvents;
import com.robypomper.communication.trustmanagers.AbsCustomTrustManager;
import com.robypomper.communication.trustmanagers.DynAddTrustManager;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.clients.apis.gws.JCPAPIGWsClient;
import com.robypomper.josp.jcp.info.JCPGWsVersions;
import com.robypomper.josp.jcp.params.jcp.JCPGWsStartup;
import com.robypomper.josp.jcp.params.jcp.JCPGWsStatus;
import com.robypomper.josp.types.josp.gw.GWType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;


public abstract class AbsGWsService implements JCPClient2.ConnectListener {

    // Class constants

    private static final String CERT_ALIAS = "O2SGW-Cert-Cloud";
    private static final String ONLY_SERVER_ID = "mainServer";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private static final List<AbsGWsService> jospGWs = new ArrayList<>();               // static because shared among all GWs
    private final String hostAddrInternal;
    private final String hostAddrPublic;
    private final Server server;


    // Constructor

    /**
     * Initialize JOSPGWsService with only one internal server.
     */
    public AbsGWsService(final String hostnameInternal, final String hostnamePublic, int port) {
        try {
            log.debug(String.format("Generating and starting internal JOSP GWs '%s' server", ONLY_SERVER_ID));
            String alias = String.format("%s@%s", ONLY_SERVER_ID, CERT_ALIAS);

            log.trace(String.format("Preparing internal JOSP GWs '%s' keystore", ONLY_SERVER_ID));
            KeyStore ks = UtilsJKS.generateKeyStore(ONLY_SERVER_ID, null, alias);
            Certificate publicCertificate = UtilsJKS.extractCertificate(ks, alias);

            log.trace(String.format("Preparing internal JOSP GWs '%s' SSL context", ONLY_SERVER_ID));
            SSLContext sslCtx;
            DynAddTrustManager trustmanager = new DynAddTrustManager();
            sslCtx = UtilsSSL.generateSSLContext(ks, null, trustmanager);

            log.trace(String.format("Creating and starting internal JOSP GWs '%s' SSL context", ONLY_SERVER_ID));
            server = new Server(this, sslCtx, ONLY_SERVER_ID, port, trustmanager, publicCertificate);

        } catch (UtilsJKS.GenerationException | UtilsSSL.GenerationException e) {
            throw new RuntimeException(String.format("Error on initializing internal GWs because %s", e.getMessage()), e);
        }

        jospGWs.add(this);

        //this.hostAddrInternal = resolveStringHostname(hostnameInternal);
        //this.hostAddrPublic = resolveStringHostname(hostnamePublic);
        this.hostAddrPublic = hostnamePublic;
        this.hostAddrInternal = hostnameInternal;

        log.info(String.format("Initialized GWsService instance with '%s' internal and '%s' public address", hostAddrInternal, hostAddrPublic));
    }

    private static InetAddress resolveStringHostname(String hostName) {
        InetAddress tpmHostAddr;
        try {
            tpmHostAddr = InetAddress.getByName(hostName);
        } catch (UnknownHostException e) {
            try {
                log.warn("UnknownHostException resolving '%s' hostname, fallback on Localhost");
                tpmHostAddr = InetAddress.getLocalHost();

            } catch (UnknownHostException unknownHostException) {
                log.warn("UnknownHostException resolving '%s' Localhost, fallback on Loopback");
                tpmHostAddr = InetAddress.getLoopbackAddress();
            }
        }

        log.info(String.format("resolveStringHostname(%s) => %s", hostName, tpmHostAddr.getHostAddress()));
        return tpmHostAddr;
    }


    // Client's utils

    public AbsGWsService.Server getServer() {
        return server;
    }

    public String getInternalAddress() {
        return hostAddrInternal;
    }

    public String getPublicAddress() {
        return hostAddrPublic;
    }

    public int getPort() {
        return server.getPort();
    }

    public Certificate getPublicCertificate() {
        return server.getPublicCertificate();
    }

    public boolean addClientCertificate(String idClient, Certificate certClient) {
        log.debug(String.format("Adding client '%s' certificate to internal JOSP GWs", idClient));
        try {
            server.getTrustManager().addCertificate(idClient, certClient);
            log.debug(String.format("Client '%s' certificate added to internal JOSP GWs", idClient));
            return true;

        } catch (AbsCustomTrustManager.UpdateException e) {
            log.warn(String.format("Error on adding client certificate to internal JOSP GWs TrustStore because %s", e.getMessage()), e);
            return false;
        }
    }


    // Sub-classes getters

    public List<AbsGWsService> getGateways() {
        return jospGWs;
    }


    // Sub-classes methods

    abstract protected ServerLocalEvents getServerEventsListener();

    abstract protected ServerClientEvents getClientEventsListener();

    abstract protected ServerMessagingEvents getMessagingEventsListener();


    // JCP APIs GWs registration

    protected void register(JCPAPIGWsClient gwsAPI, String hostNameInternal, String hostNamePublic, int apisPort, int maxClients, GWType type) {
        String gwId = generateGWId(hostNameInternal, getServer().getPort());
        try {
            JCPGWsStartup gwStartup = new JCPGWsStartup(type, hostNamePublic, getServer().getPort(), hostNameInternal, apisPort, maxClients, JCPGWsVersions.VER_JCPGWs_S2O_2_0);
            if (!gwsAPI.getClient().isConnected()) {
                log.warn(String.format("Can't register JCP GW '%s' startup to JCP APIs because JCP APIs not available, it will registered on JCP APIs connection.", gwId));
                return;
            }

            gwsAPI.postStartup(gwStartup, gwId);
            log.info(String.format("JCP GW '%s' registered to JCP APIs successfully.", gwId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            log.warn(String.format("Can't register JCP GW '%s' to JCP APIs because '%s'.", gwId, e.getMessage()), e);
        }
    }

    protected void deregister(JCPAPIGWsClient gwsAPI, String hostNameInternal) {
        String gwId = generateGWId(hostNameInternal, getServer().getPort());
        try {
            if (!gwsAPI.getClient().isConnected()) {
                log.warn(String.format("Can't de-register JCP GW '%s' startup to JCP APIs because JCP APIs not available, it will de-registered on JCP APIs connection.", gwId));
                return;
            }

            gwsAPI.postShutdown(gwId);
            log.info(String.format("JCP GW '%s' de-registered to JCP APIs successfully.", gwId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            log.warn(String.format("Can't de-register JCP GW '%s' to JCP APIs because '%s'.", gwId, e.getMessage()), e);
        }
    }

    protected void update(JCPAPIGWsClient gwsAPI, String hostNameInternal, JCPGWsStatus gwStatus) {
        String gwId = generateGWId(hostNameInternal, getServer().getPort());
        try {
            if (!gwsAPI.getClient().isConnected()) {
                log.warn(String.format("Can't update JCP GW '%s' status to JCP APIs because JCP APIs not available, it will updated on JCP APIs connection.", gwId));
                return;
            }

            gwsAPI.postStatus(gwStatus, gwId);
            log.info(String.format("JCP GW '%s' updated to JCP APIs successfully.", gwId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            log.warn(String.format("Can't update JCP GW '%s' status to JCP APIs because '%s'.", gwId, e.getMessage()), e);
        }
    }


    // Structures

    protected static class Server extends DefaultSSLServer {

        // Internal vars

        private final int port;
        private final AbsCustomTrustManager trustManager;
        private final Certificate publicCertificate;


        // Constructor

        /**
         * {@inheritDoc}
         *
         * @param sslCtx
         * @param idServer
         * @param port
         */
        public Server(AbsGWsService gwService, SSLContext sslCtx, String idServer, int port, AbsCustomTrustManager trustManager, Certificate publicCertificate) {
            super(sslCtx, idServer, port, true, gwService.getServerEventsListener(), gwService.getClientEventsListener(), gwService.getMessagingEventsListener());
            this.port = port;
            this.trustManager = trustManager;
            this.publicCertificate = publicCertificate;
        }


        // Getters

        public int getPort() {
            return port;
        }

        public AbsCustomTrustManager getTrustManager() {
            return trustManager;
        }

        public Certificate getPublicCertificate() {
            return publicCertificate;
        }

    }


    // Utils

    protected String generateGWId(String hostnameInternal, int port) {
        String addr = String.format("%s-%d", hostnameInternal, port).replace('.', '-');
        return Integer.toString(addr.hashCode());// + "@" + addr;
    }

}
