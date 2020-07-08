package com.robypomper.communication.server;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.UtilsSSL;
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
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.security.KeyStore;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DefaultSSLServerTest {

    // Class constants

    final static String TEST_FILES_PREFIX = "tmp/tests/";
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
    public void testClientConnectionAndDisconnection() throws Server.ListeningException, IOException, InterruptedException {
        // Start test server
        DefaultServerTest_Base.startServer(serverLatch);

        // Connect client
        SSLSocket s = (SSLSocket) clientSSLContext.getSocketFactory().createSocket(LOCALHOST, PORT);
        s.startHandshake();
        Assertions.assertTrue(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));

        // Check client list
        List<ClientInfo> clients = serverLatch.getClients();
        DefaultServerTest_Base.printClientInfoList(clients);
        Assertions.assertEquals(1, clients.size());

        // Check client status
        ClientInfo client = clients.get(0);
        Assertions.assertTrue(client.isConnected());
        String calculatedClientId = String.format(DefaultServer.ID_CLI_FORMAT, s.getLocalAddress(), s.getLocalPort());
        Assertions.assertEquals(calculatedClientId, client.getClientId());

        // Disconnect client
        s.close();
        Assertions.assertTrue(latchSCE.onClientDisconnection.await(1, TimeUnit.SECONDS));

        // Check client list
        clients = serverLatch.getClients();
        DefaultServerTest_Base.printClientInfoList(clients);
        Assertions.assertEquals(0, clients.size());

        // Check client status
        Assertions.assertFalse(client.isConnected());

        // Stop test server
        DefaultServerTest_Base.stopServer(serverLatch);
    }

    @Test
    public void testClientConnectionAndDisconnectionWithAuth() throws Server.ListeningException, IOException, InterruptedException {
        // Start test server
        DefaultServerTest_Base.startServer(serverLatchWithAuth);

        // Connect client
        SSLSocket s = (SSLSocket) clientSSLContextWithCliKeyStore.getSocketFactory().createSocket(LOCALHOST, PORT);
        s.startHandshake();
        Assertions.assertTrue(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));

        // Check client list
        List<ClientInfo> clients = serverLatchWithAuth.getClients();
        DefaultServerTest_Base.printClientInfoList(clients);
        Assertions.assertEquals(1, clients.size());

        // Check client status
        ClientInfo client = clients.get(0);
        Assertions.assertTrue(client.isConnected());
        Assertions.assertEquals(CLI_ID_CERTIFICATE, client.getClientId());

        // Disconnect client
        s.close();
        Assertions.assertTrue(latchSCE.onClientDisconnection.await(1, TimeUnit.SECONDS));

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
    public void testClientConnectionAndDisconnectionWithAuthNoHandshake_FAIL() throws Server.ListeningException, IOException, InterruptedException {
        // Start test server
        DefaultServerTest_Base.startServer(serverLatchWithAuth);

        // Connect client
        SSLSocket s = (SSLSocket) clientSSLContextWithCliKeyStore.getSocketFactory().createSocket(LOCALHOST, PORT);
        //s.startHandshake();
        Assertions.assertFalse(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(s.isConnected()); // Client result connected even the server closed connection on his side

        // Check client list still empty
        List<ClientInfo> clients = serverLatchWithAuth.getClients();
        DefaultServerTest_Base.printClientInfoList(clients);
        Assertions.assertEquals(0, clients.size());

        // Disconnect client
        s.close();

        // Stop test server
        DefaultServerTest_Base.stopServer(serverLatchWithAuth);
    }

    @Test
    public void testClientConnectionAndDisconnectionWithAuthOnlyReq_FAIL() throws Server.ListeningException, IOException, InterruptedException {
        // Start test server
        DefaultServerTest_Base.startServer(serverLatchWithAuth);

        // Connect client
        SSLSocket s = (SSLSocket) clientSSLContext.getSocketFactory().createSocket(LOCALHOST, PORT);
        Assertions.assertThrows(SSLHandshakeException.class, s::startHandshake);
        Assertions.assertFalse(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(s.isConnected()); // Client result connected even the server closed connection on his side

        // Check client list still empty
        List<ClientInfo> clients = serverLatchWithAuth.getClients();
        DefaultServerTest_Base.printClientInfoList(clients);
        Assertions.assertEquals(0, clients.size());

        // Disconnect client
        s.close();

        // Stop test server
        DefaultServerTest_Base.stopServer(serverLatchWithAuth);
    }

}