package com.robypomper.josp.jcp.gw;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.server.DefaultSSLServer;
import com.robypomper.communication.server.events.ServerClientEvents;
import com.robypomper.communication.server.events.ServerLocalEvents;
import com.robypomper.communication.server.events.ServerMessagingEvents;
import com.robypomper.communication.trustmanagers.AbsCustomTrustManager;
import com.robypomper.communication.trustmanagers.DynAddTrustManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.net.InetAddress;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public abstract class AbsJOSPGWsService {

    // Class constants

    private static final String CERT_ALIAS = "O2SGW-Cert-Cloud";
    private static final String ONLY_SERVER_ID = "mainServer";


    // Internal vars

    private final static List<AbsJOSPGWsService> jospGWs = new ArrayList<>();          // static because shared among all JOSPGWs
    private final static List<Integer> boundedPorts = new ArrayList<>();            // static because shared among all JOSPGWs
    private final static Random rnd = new Random();
    private static final Logger log = LogManager.getLogger();
    private final Map<String, Server> servers = new HashMap<>();
    private final Map<String, Server> clients = new HashMap<>();


    // Constructor

    /**
     * Initialize JOSPGWsService with only one internal server.
     */
    public AbsJOSPGWsService() {
        try {
            servers.put(ONLY_SERVER_ID, initServer(ONLY_SERVER_ID));

        } catch (UtilsJKS.GenerationException | UtilsSSL.GenerationException | com.robypomper.communication.server.Server.ListeningException e) {
            log.warn(String.format("Error on initializing internal JOSP GWs because %s", e.getMessage()), e);
        }

        jospGWs.add(this);

        log.info(String.format("Initialized JOSPGWsService instance with %d pre-initialized servers", servers.size()));
    }


    // Internal JOSP GWs initializer

    private Server initServer(String idServer) throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, com.robypomper.communication.server.Server.ListeningException {
        log.debug(String.format("Generating and starting internal JOSP GWs '%s' server", idServer));
        String alias = String.format("%s@%s", idServer, CERT_ALIAS);
        int port = generateRandomPort();

        log.trace(String.format("Preparing internal JOSP GWs '%s' keystore", idServer));
        KeyStore ks = UtilsJKS.generateKeyStore(idServer, null, alias);
        Certificate publicCertificate = UtilsJKS.extractCertificate(ks, alias);

        log.trace(String.format("Preparing internal JOSP GWs '%s' SSL context", idServer));
        SSLContext sslCtx;
        DynAddTrustManager trustmanager = new DynAddTrustManager();
        sslCtx = UtilsSSL.generateSSLContext(ks, null, trustmanager);

        log.trace(String.format("Creating and starting internal JOSP GWs '%s' SSL context", idServer));
        Server s = new Server(this, sslCtx, idServer, port, trustmanager, publicCertificate);
        s.start();
        log.debug(String.format("Internal JOSP GWs '%s' server generated and started", idServer));

        log.info(String.format("Internal JOSP GWs '%s' server started on port '%d'", idServer, port));
        return s;
    }


    // Client's utils

    private Server associateOrGetServer(String idClient) {
        if (clients.get(idClient) == null) {
            log.trace(String.format("Adding client '%s' internal JOSP GWs '%s' server", idClient, ONLY_SERVER_ID));
            Server newServer = servers.get(ONLY_SERVER_ID);
            clients.put(idClient, newServer);
        }

        return clients.get(idClient);
    }

    public InetAddress getPublicAddress(String idClient) {
        Server server = associateOrGetServer(idClient);
        return server.getPublicAddress();
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
        log.debug(String.format("Adding client '%s' certificate to internal JOSP GWs", idClient));
        try {
            Server server = associateOrGetServer(idClient);
            server.getTrustManager().addCertificate(idClient, certClient);
            log.debug(String.format("Client '%s' certificate added to internal JOSP GWs", idClient));
            return true;

        } catch (AbsCustomTrustManager.UpdateException e) {
            log.warn(String.format("Error on adding client certificate to internal JOSP GWs TrustStore because %s", e.getMessage()), e);
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

        public InetAddress getPublicAddress() {
            return getAddress();
        }

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
