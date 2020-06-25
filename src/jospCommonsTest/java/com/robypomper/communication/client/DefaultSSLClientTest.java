package com.robypomper.communication.client;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.client.events.LatchClientLocalEventsListener;
import com.robypomper.communication.client.events.LatchClientMessagingEventsListener;
import com.robypomper.communication.client.events.LatchClientServerEventsListener;
import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.DefaultSSLServer;
import com.robypomper.communication.server.DefaultServer;
import com.robypomper.communication.server.DefaultServerTest_Base;
import com.robypomper.communication.server.Server;
import com.robypomper.communication.server.events.LatchServerClientEventsListener;
import com.robypomper.communication.server.events.LatchServerLocalEventsListener;
import com.robypomper.communication.server.events.LatchServerMessagingEventsListener;
import com.robypomper.communication.server.standard.SSLCertServerTest;
import com.robypomper.communication.trustmanagers.AbsCustomTrustManager;
import com.robypomper.communication.trustmanagers.DynAddTrustManager;
import com.robypomper.log.Mrk_Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.net.InetAddress;
import java.security.KeyStore;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class DefaultSSLClientTest {

    // Class constants

    final static String TEST_FILES_PREFIX = "tmp/tests/";
    final static String ID_CLIENT = "TestClient";
    final static String ID_SERVER = "TestServer";
    final static int PORT = 1234;
    final static InetAddress LOCALHOST = InetAddress.getLoopbackAddress();

    final static String SRV_ID_CERTIFICATE = "TestSSLCertServer";
    final static String SRV_CERT_PUB_PATH = TEST_FILES_PREFIX + String.format("server-%s.crt", SSLCertServerTest.class.getSimpleName());
    final static String SRV_CERT_ALIAS = "certAliasServer";
    final static String SRV_KS_PASS = "ksPassServer";
    final static String CLI_ID_CERTIFICATE = "TestSSLCertClient";
    final static String CLI_CERT_PUB_PATH = TEST_FILES_PREFIX + String.format("client-%s.crt", SSLCertServerTest.class.getSimpleName());
    final static String CLI_CERT_ALIAS = "certAliasClient";
    final static String CLI_KS_PASS = "ksPassClient";


    // Internal vars

    protected static Logger log = LogManager.getLogger();

    protected SSLContext serverSSLContext = null;
    protected SSLContext serverSSLContextWithCliCert = null;
    protected SSLContext clientSSLContext = null;
    protected SSLContext clientSSLContextWithCliKeyStore = null;

    protected Client clientLatch = null;
    protected Client clientLatchWithCliKeyStore = null;
    protected LatchClientLocalEventsListener latchCLE = null;
    protected LatchClientServerEventsListener latchCSE = null;
    protected LatchClientMessagingEventsListener latchCME = null;

    protected Server serverLatch = null;
    protected Server serverLatchWithAuth = null;
    protected LatchServerLocalEventsListener latchSLE = null;
    protected LatchServerClientEventsListener latchSCE = null;
    protected LatchServerMessagingEventsListener latchSME = null;


    // Test configurations

    @BeforeEach
    public void setUp() throws UtilsJKS.GenerationException, UtilsSSL.GenerationException, AbsCustomTrustManager.UpdateException {
        log.debug(Mrk_Test.TEST_SPACER, "########## ########## ########## ########## ##########");
        log.debug(Mrk_Test.TEST_METHODS, "setUp");

        // Init KeyStores (server and client)
        KeyStore serverKeyStore = UtilsJKS.generateKeyStore(SRV_ID_CERTIFICATE, SRV_KS_PASS, SRV_CERT_ALIAS);
        UtilsJKS.exportCertificate(serverKeyStore, SRV_CERT_PUB_PATH, SRV_CERT_ALIAS);
        KeyStore clientKeyStore = UtilsJKS.generateKeyStore(CLI_ID_CERTIFICATE, CLI_KS_PASS, CLI_CERT_ALIAS);
        UtilsJKS.exportCertificate(clientKeyStore, CLI_CERT_PUB_PATH, CLI_CERT_ALIAS);

        // Generate TrustManagers and SSLContexts for servers
        DynAddTrustManager serverTrustManager = new DynAddTrustManager();
        serverSSLContext = UtilsSSL.generateSSLContext(serverKeyStore, SRV_KS_PASS, serverTrustManager);
        DynAddTrustManager serverTrustManagerWithCliCert = new DynAddTrustManager();
        serverTrustManagerWithCliCert.addCertificate(CLI_CERT_ALIAS, new File(CLI_CERT_PUB_PATH));
        serverSSLContextWithCliCert = UtilsSSL.generateSSLContext(serverKeyStore, SRV_KS_PASS, serverTrustManagerWithCliCert);

        // Generate TrustManagers and SSLContexts for clients
        DynAddTrustManager clientTrustManager = new DynAddTrustManager();
        clientTrustManager.addCertificate(SRV_CERT_ALIAS, new File(SRV_CERT_PUB_PATH));
        clientSSLContext = UtilsSSL.generateSSLContext(null, CLI_KS_PASS, clientTrustManager);
        clientSSLContextWithCliKeyStore = UtilsSSL.generateSSLContext(clientKeyStore, CLI_KS_PASS, clientTrustManager);

        // Init test clients
        latchCLE = new LatchClientLocalEventsListener();
        latchCSE = new LatchClientServerEventsListener();
        latchCME = new LatchClientMessagingEventsListener();
        clientLatch = new DefaultSSLClient(clientSSLContext, ID_CLIENT, LOCALHOST, PORT, latchCLE, latchCSE, latchCME);
        clientLatchWithCliKeyStore = new DefaultSSLClient(clientSSLContextWithCliKeyStore, ID_CLIENT, LOCALHOST, PORT, latchCLE, latchCSE, latchCME);

        // Init test server
        latchSLE = new LatchServerLocalEventsListener();
        latchSCE = new LatchServerClientEventsListener();
        latchSME = new LatchServerMessagingEventsListener();
        serverLatch = new DefaultSSLServer(serverSSLContext, ID_SERVER, PORT, false, latchSLE, latchSCE, latchSME);
        serverLatchWithAuth = new DefaultSSLServer(serverSSLContextWithCliCert, ID_SERVER, PORT, true, latchSLE, latchSCE, latchSME);

        log.debug(Mrk_Test.TEST_METHODS, "test");
    }

    @AfterEach
    public void tearDown() {
        log.debug(Mrk_Test.TEST_METHODS, "tearDown");

        if (new File(SRV_CERT_PUB_PATH).delete())
            log.debug(Mrk_Test.TEST, String.format("Public server certificate file '%s' delete successfully", SRV_CERT_PUB_PATH));
        else
            log.debug(Mrk_Test.TEST, String.format("Error on deleting public server certificate file '%s'", SRV_CERT_PUB_PATH));

        if (new File(CLI_CERT_PUB_PATH).delete())
            log.debug(Mrk_Test.TEST, String.format("Public client certificate file '%s' delete successfully", CLI_CERT_PUB_PATH));
        else
            log.debug(Mrk_Test.TEST, String.format("Error on deleting public client certificate file '%s'", CLI_CERT_PUB_PATH));

        // If still running, stop test server
        if (serverLatch.isRunning())
            serverLatch.stop();
        if (serverLatchWithAuth.isRunning())
            serverLatch.stop();
    }


    // Clients

    @Test
    public void testClientConnectionAndDisconnection() throws Server.ListeningException, InterruptedException, Client.ConnectionException {
        // Start test server
        System.out.println("\nSERVER START");
        DefaultServerTest_Base.startServer(serverLatch);

        // Connect client
        System.out.println("\nCLIENT CONNECTION");
        clientLatch.connect();

        // Check connection events on both side
        System.out.println("\nCHECKS");
        Assertions.assertTrue(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(latchCSE.onServerConnection.await(1, TimeUnit.SECONDS));

        // Check client list
        List<ClientInfo> clients = serverLatch.getClients();
        DefaultServerTest_Base.printClientInfoList(clients);
        Assertions.assertEquals(1, clients.size());

        // Check client status
        ClientInfo client = clients.get(0);
        Assertions.assertTrue(client.isConnected());
        String calculatedClientId = String.format(DefaultServer.ID_CLI_FORMAT, clientLatch.getServerInfo().getLocalAddress(), clientLatch.getServerInfo().getLocalPort());
        Assertions.assertEquals(calculatedClientId, client.getClientId());

        // Check server status
        Assertions.assertTrue(clientLatch.getServerInfo().isConnected());
        Assertions.assertEquals(SRV_ID_CERTIFICATE, clientLatch.getServerInfo().getServerId());

        // Disconnect client
        System.out.println("\nCLIENT DISCONNECTION");
        clientLatch.disconnect();
        Assertions.assertTrue(latchSCE.onClientDisconnection.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(latchCSE.onServerDisconnection.await(1, TimeUnit.SECONDS));

        // Check client list
        clients = serverLatch.getClients();
        DefaultServerTest_Base.printClientInfoList(clients);
        Assertions.assertEquals(0, clients.size());

        // Check client status
        Assertions.assertFalse(client.isConnected());

        // Stop test server
        System.out.println("\nSERVER STOP");
        DefaultServerTest_Base.stopServer(serverLatch);
    }

    @Test
    public void testClientConnectionAndDisconnectionWithAuth() throws Server.ListeningException, InterruptedException, Client.ConnectionException {
        // Start test server
        DefaultServerTest_Base.startServer(serverLatchWithAuth);

        // Connect client
        clientLatchWithCliKeyStore.connect();

        // Check connection events on both side
        Assertions.assertTrue(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(latchCSE.onServerConnection.await(1, TimeUnit.SECONDS));

        // Check client list
        List<ClientInfo> clients = serverLatchWithAuth.getClients();
        DefaultServerTest_Base.printClientInfoList(clients);
        Assertions.assertEquals(1, clients.size());

        // Check client status
        ClientInfo client = clients.get(0);
        Assertions.assertTrue(client.isConnected());
        Assertions.assertEquals(CLI_ID_CERTIFICATE, client.getClientId());

        // Check server status
        Assertions.assertTrue(clientLatchWithCliKeyStore.getServerInfo().isConnected());
        Assertions.assertEquals(SRV_ID_CERTIFICATE, clientLatchWithCliKeyStore.getServerInfo().getServerId());//SRV-localhost/127.0.0.1:1234

        // Disconnect client
        clientLatchWithCliKeyStore.disconnect();
        Assertions.assertTrue(latchSCE.onClientDisconnection.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(latchCSE.onServerDisconnection.await(1, TimeUnit.SECONDS));

        // Check client list
        clients = serverLatchWithAuth.getClients();
        DefaultServerTest_Base.printClientInfoList(clients);
        Assertions.assertEquals(0, clients.size());

        // Check client status
        Assertions.assertFalse(client.isConnected());

        // Stop test server
        DefaultServerTest_Base.stopServer(serverLatchWithAuth);
    }

    @Test
    public void testClientConnectionAndDisconnectionWithAuthOnlyReq_FAIL() throws Server.ListeningException, InterruptedException {
        // Start test server
        DefaultServerTest_Base.startServer(serverLatchWithAuth);

        // Connect client
        Assertions.assertThrows(Client.ConnectionException.class, clientLatch::connect);

        // Check connection events on both side
        Assertions.assertFalse(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));
        Assertions.assertFalse(latchCSE.onServerConnection.await(1, TimeUnit.SECONDS));
        Assertions.assertFalse(clientLatch.isConnected());

        // Check client list still empty
        List<ClientInfo> clients = serverLatchWithAuth.getClients();
        DefaultServerTest_Base.printClientInfoList(clients);
        Assertions.assertEquals(0, clients.size());

        // Stop test server
        DefaultServerTest_Base.stopServer(serverLatchWithAuth);
    }

}