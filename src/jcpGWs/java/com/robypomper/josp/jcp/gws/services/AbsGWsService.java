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
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.*;


public abstract class AbsGWsService {

    // Class constants

    private static final String CERT_ALIAS = "O2SGW-Cert-Cloud";
    private static final String ONLY_SERVER_ID = "mainServer";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private static final List<AbsGWsService> jospGWs = new ArrayList<>();               // static because shared among all GWs
    private static final List<Integer> boundedPorts = new ArrayList<>();                // static because shared among all GWs
    private static final Random rnd = new Random();
    private final InetAddress hostAddr;
    private final int port;
    private final Server server;
    private final List<String> clients = new ArrayList<>();


    // Constructor

    /**
     * Initialize JOSPGWsService with only one internal server.
     */
    public AbsGWsService(final String hostName, int port) {
        this.port = port;

        try {
            server = initServer(ONLY_SERVER_ID);

        } catch (UtilsJKS.GenerationException | UtilsSSL.GenerationException | com.robypomper.communication.server.Server.ListeningException e) {
            throw new RuntimeException(String.format("Error on initializing internal GWs because %s", e.getMessage()), e);
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
        this.hostAddr = tpmHostAddr;

        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("Initialized GWsService instance with %s public address", hostAddr));
    }

    @PreDestroy
    public void destroy() {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("Halt server %s", server.getServerId()));
        server.stop();

        if (server.isRunning())
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }

        if (server.isRunning())
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Gateway %s not halted, force it.", this.getClass().getSimpleName()));
        else
            log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("Halted %s gateway", this.getClass().getSimpleName()));
    }


    // Internal JOSP GWs initializer

    private Server initServer(String idServer) throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, com.robypomper.communication.server.Server.ListeningException {
        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("Generating and starting internal JOSP GWs '%s' server", idServer));
        String alias = String.format("%s@%s", idServer, CERT_ALIAS);

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

    public InetAddress getPublicAddress() {
        return hostAddr;
    }

    public int getPort() {
        return server.getPort();
    }

    public Certificate getPublicCertificate() {
        return server.getPublicCertificate();
    }

    public boolean addClientCertificate(String idClient, Certificate certClient) {
        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("Adding client '%s' certificate to internal JOSP GWs", idClient));
        try {
            server.getTrustManager().addCertificate(idClient, certClient);
            log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("Client '%s' certificate added to internal JOSP GWs", idClient));
            return true;

        } catch (AbsCustomTrustManager.UpdateException e) {
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Error on adding client certificate to internal JOSP GWs TrustStore because %s", e.getMessage()), e);
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


    protected String generateGWId(String publicHostName, int port) {
        String addr = String.format("%s-%d", publicHostName, port);
        return addr.hashCode() + "-" + addr;
    }

}
