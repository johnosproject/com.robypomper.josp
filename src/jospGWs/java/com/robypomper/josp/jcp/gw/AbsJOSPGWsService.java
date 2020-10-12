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

package com.robypomper.josp.jcp.gw;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.server.DefaultSSLServer;
import com.robypomper.communication.server.events.ServerClientEvents;
import com.robypomper.communication.server.events.ServerLocalEvents;
import com.robypomper.communication.server.events.ServerMessagingEvents;
import com.robypomper.communication.trustmanagers.AbsCustomTrustManager;
import com.robypomper.communication.trustmanagers.DynAddTrustManager;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.*;


public abstract class AbsJOSPGWsService {

    // Class constants

    private static final String CERT_ALIAS = "O2SGW-Cert-Cloud";
    private static final String ONLY_SERVER_ID = "mainServer";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private static final List<AbsJOSPGWsService> jospGWs = new ArrayList<>();           // static because shared among all JOSPGWs
    private static final List<Integer> boundedPorts = new ArrayList<>();                // static because shared among all JOSPGWs
    private static final Random rnd = new Random();
    private final InetAddress hostAddr;
    private final Map<String, Server> servers = new HashMap<>();
    private final Map<String, Server> clients = new HashMap<>();


    // Constructor

    /**
     * Initialize JOSPGWsService with only one internal server.
     */
    public AbsJOSPGWsService(final String hostName) {
        try {
            servers.put(ONLY_SERVER_ID, initServer(ONLY_SERVER_ID));

        } catch (UtilsJKS.GenerationException | UtilsSSL.GenerationException | com.robypomper.communication.server.Server.ListeningException e) {
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Error on initializing internal JOSP GWs because %s", e.getMessage()), e);
        }

        jospGWs.add(this);

        InetAddress tpmHostAddr;
        try {
            tpmHostAddr = InetAddress.getByName(hostName);
        } catch (UnknownHostException e) {
            try {
                tpmHostAddr = InetAddress.getLocalHost();
            } catch (UnknownHostException unknownHostException) {
                tpmHostAddr = InetAddress.getLoopbackAddress();
            }
        }
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("Internal JOSP GWs use '%s' as public address", tpmHostAddr));
        this.hostAddr = tpmHostAddr;

        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("Initialized JOSPGWsService instance with %d pre-initialized servers", servers.size()));
    }


    // Internal JOSP GWs initializer

    private Server initServer(String idServer) throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, com.robypomper.communication.server.Server.ListeningException {
        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("Generating and starting internal JOSP GWs '%s' server", idServer));
        String alias = String.format("%s@%s", idServer, CERT_ALIAS);
        int port = generateRandomPort();

        log.trace(Mrk_Commons.COMM_SRV_IMPL, String.format("Preparing internal JOSP GWs '%s' keystore", idServer));
        KeyStore ks = UtilsJKS.generateKeyStore(idServer, null, alias);
        Certificate publicCertificate = UtilsJKS.extractCertificate(ks, alias);

        log.trace(Mrk_Commons.COMM_SRV_IMPL, String.format("Preparing internal JOSP GWs '%s' SSL context", idServer));
        SSLContext sslCtx;
        DynAddTrustManager trustmanager = new DynAddTrustManager();
        sslCtx = UtilsSSL.generateSSLContext(ks, null, trustmanager);

        log.trace(Mrk_Commons.COMM_SRV_IMPL, String.format("Creating and starting internal JOSP GWs '%s' SSL context", idServer));
        Server s = new Server(this, sslCtx, idServer, port, trustmanager, publicCertificate);
        s.start();
        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("Internal JOSP GWs '%s' server generated and started", idServer));

        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("Internal JOSP GWs '%s' server started on port '%d'", idServer, port));
        return s;
    }


    // Client's utils

    private Server associateOrGetServer(String idClient) {
        if (clients.get(idClient) == null) {
            log.trace(Mrk_Commons.COMM_SRV_IMPL, String.format("Adding client '%s' internal JOSP GWs '%s' server", idClient, ONLY_SERVER_ID));
            Server newServer = servers.get(ONLY_SERVER_ID);
            clients.put(idClient, newServer);
        }

        return clients.get(idClient);
    }

    public InetAddress getPublicAddress(String idClient) {
//        Server server = associateOrGetServer(idClient);
//        return server.getPublicAddress();
        return hostAddr;
    }

    public int getPort(String idClient) {
        Server server = associateOrGetServer(idClient);
        return server.getPort();
    }

    public Certificate getPublicCertificate(String idClient) {
        Server server = associateOrGetServer(idClient);
        return server.getPublicCertificate();
    }

    public boolean addClientCertificate(String idClient, Certificate certClient) {
        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("Adding client '%s' certificate to internal JOSP GWs", idClient));
        try {
            Server server = associateOrGetServer(idClient);
            server.getTrustManager().addCertificate(idClient, certClient);
            log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("Client '%s' certificate added to internal JOSP GWs", idClient));
            return true;

        } catch (AbsCustomTrustManager.UpdateException e) {
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Error on adding client certificate to internal JOSP GWs TrustStore because %s", e.getMessage()), e);
            return false;
        }
    }


    // Ports generator

    private int generateRandomPort() {
        int port = rnd.nextInt(getMaxPort() - getMinPort() + 1) + getMinPort();
        while (boundedPorts.contains(port))
            port = rnd.nextInt(getMaxPort() - getMinPort() + 1) + getMinPort();

        boundedPorts.add(port);
        return port;
    }


    // Sub-classes getters

    public List<AbsJOSPGWsService> getJOSPGWs() {
        return jospGWs;
    }

    public Map<String, Server> getJOSPServers() {
        return servers;
    }


    // Sub-classes methods

    abstract int getMinPort();

    abstract int getMaxPort();

    abstract protected ServerLocalEvents getServerEventsListener();

    abstract protected ServerClientEvents getClientEventsListener();

    abstract protected ServerMessagingEvents getMessagingEventsListener();


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
        public Server(AbsJOSPGWsService gwService, SSLContext sslCtx, String idServer, int port, AbsCustomTrustManager trustManager, Certificate publicCertificate) {
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

}
