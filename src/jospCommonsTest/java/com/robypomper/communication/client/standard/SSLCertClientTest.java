package com.robypomper.communication.client.standard;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.client.Client;
import com.robypomper.communication.client.events.LatchSSLCertClientListener;
import com.robypomper.communication.server.Server;
import com.robypomper.communication.server.standard.SSLCertServer;
import com.robypomper.communication.trustmanagers.DynAddTrustManager;
import com.robypomper.log.Markers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.InetAddress;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;


public class SSLCertClientTest {

    // Class constants

    final static String TEST_FILES_PREFIX = "tmp/tests/";
    final static String ID_CLIENT = "TestCustomClient";
    final static InetAddress LOCALHOST = InetAddress.getLoopbackAddress();
    final static int PORT = 1234;
    final static String ID_SERVER = "TestSSLCertServer";
    final static String CLI_CERT_PUB_PATH = TEST_FILES_PREFIX + String.format("client-%s.crt", SSLCertClientTest.class.getSimpleName());
    final static String SRV_CERT_PUB_PATH = TEST_FILES_PREFIX + String.format("server-%s.crt", SSLCertClientTest.class.getSimpleName());


    // Internal vars

    protected static Logger log = LogManager.getLogger();
    protected LatchSSLCertClientListener latchSSLCertClient;
    protected Client clientSSLCert = null;
    protected Client clientSSLCertSelfSend = null;
    protected DynAddTrustManager clientCertTrustManager = null;
    protected Server serverSSLCert = null;
    protected DynAddTrustManager serverCertTrustManager = null;


    // Test configurations

    @BeforeAll
    public static void setUpAll() throws UtilsJKS.GenerationException {
        log.debug(Markers.TEST_SPACER, "########## ########## ########## ########## ##########");
        log.debug(Markers.TEST_METHODS, "setUpAll");

        log.debug(Markers.TEST, String.format("Generation of client keystore and export certificate to file '%s'", CLI_CERT_PUB_PATH));
        KeyStore clientKs = UtilsJKS.generateKeyStore(ID_CLIENT, "ksPass", "clientCertificateAlias");
        UtilsJKS.exportCertificate(clientKs, CLI_CERT_PUB_PATH, "clientCertificateAlias");

        log.debug(Markers.TEST, String.format("Generation of server keystore and export certificate to file '%s'", SRV_CERT_PUB_PATH));
        KeyStore serverKs = UtilsJKS.generateKeyStore(ID_SERVER, "ksPass", "serverCertificateAlias");
        UtilsJKS.exportCertificate(serverKs, SRV_CERT_PUB_PATH, "serverCertificateAlias");
    }

    @AfterAll
    public static void tearDownAll() {
        log.debug(Markers.TEST_METHODS, "tearDownAll");

        if (new File(CLI_CERT_PUB_PATH).delete())
            log.debug(Markers.TEST, String.format("Public certificate file '%s' delete successfully", CLI_CERT_PUB_PATH));
        else
            log.debug(Markers.TEST, String.format("Error on deleting public certificate file '%s'", CLI_CERT_PUB_PATH));

        if (new File(SRV_CERT_PUB_PATH).delete())
            log.debug(Markers.TEST, String.format("Public certificate file '%s' delete successfully", SRV_CERT_PUB_PATH));
        else
            log.debug(Markers.TEST, String.format("Error on deleting public certificate file '%s'", SRV_CERT_PUB_PATH));
    }

    @BeforeEach
    public void setUp() throws SSLCertServer.SSLCertServerException, SSLCertClient.SSLCertClientException {
        log.debug(Markers.TEST_SPACER, "########## ########## ########## ########## ##########");
        log.debug(Markers.TEST_METHODS, "setUp");

        // Init test client
        clientCertTrustManager = new DynAddTrustManager();
        latchSSLCertClient = new LatchSSLCertClientListener();
        clientSSLCert = new SSLCertClient(ID_CLIENT, LOCALHOST, PORT, clientCertTrustManager, latchSSLCertClient);
        clientSSLCertSelfSend = new SSLCertClient(ID_CLIENT, LOCALHOST, PORT, CLI_CERT_PUB_PATH, clientCertTrustManager, latchSSLCertClient);

        // Init test server
        serverCertTrustManager = new DynAddTrustManager();
        serverSSLCert = new SSLCertServer(ID_SERVER, PORT, SRV_CERT_PUB_PATH, serverCertTrustManager);

        log.debug(Markers.TEST_METHODS, "test");
    }

    @AfterEach
    public void tearDown() {
        log.debug(Markers.TEST_METHODS, "tearDown");

        // If still connected, disconnect test clients
        if (clientSSLCert.isConnected())
            clientSSLCert.disconnect();
        if (clientSSLCertSelfSend.isConnected())
            clientSSLCertSelfSend.disconnect();

        // If still running, stop test server
        if (serverSSLCert.isRunning())
            serverSSLCert.stop();
        serverCertTrustManager = null;
    }


    // isRunning, start and stop

    @Test
    public void testSSLCert() throws Server.ListeningException, InterruptedException, Client.ConnectionException {
        // Start server and connect client
        startServer(serverSSLCert);

        clientSSLCert.connect();

        Assertions.assertFalse(latchSSLCertClient.onCertificateSend.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(latchSSLCertClient.onCertificateStored.await(1, TimeUnit.SECONDS));

        Assertions.assertEquals(1, clientCertTrustManager.getAcceptedIssuers().length);
        Assertions.assertEquals(0, serverCertTrustManager.getAcceptedIssuers().length);

        //clientSSLCert.disconnect();
        Assertions.assertFalse(clientSSLCert.isConnected());

        // Stop test server
        stopServer(serverSSLCert);
    }

    @Test
    public void testSSLCertClientSendSelf() throws Server.ListeningException, InterruptedException, Client.ConnectionException {
        // Start server and connect client
        startServer(serverSSLCert);

        clientSSLCertSelfSend.connect();

        Assertions.assertTrue(latchSSLCertClient.onCertificateSend.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(latchSSLCertClient.onCertificateStored.await(1, TimeUnit.SECONDS));

        Thread.sleep(100);

        Assertions.assertEquals(1, clientCertTrustManager.getAcceptedIssuers().length);
        Assertions.assertEquals(1, serverCertTrustManager.getAcceptedIssuers().length);

        //clientSSLCert.disconnect();
        Assertions.assertFalse(clientSSLCert.isConnected());

        // Stop test server
        stopServer(serverSSLCert);
    }


    // Utils methods

    private void startServer(Server server) throws Server.ListeningException {
        server.start();
        assert server.isRunning();
    }

    private void stopServer(Server server) {
        server.stop();
        assert !server.isRunning();
    }

}