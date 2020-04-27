package com.robypomper.communication.server.standard;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.server.Server;
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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SSLCertServerTest {

    // Class constants

    final static String ID_SERVER = "TestSSLCertServer";
    final static int PORT = 1234;
    final static String ID_CLIENT = "TestCustomClient";
    final static InetAddress LOCALHOST = InetAddress.getLoopbackAddress();
    final static String SRV_CERT_PUB_PATH = String.format("server-%s.crt", SSLCertServerTest.class.getSimpleName());
    final static String CLI_CERT_PUB_PATH = String.format("client-%s.crt", SSLCertServerTest.class.getSimpleName());


    // Internal vars

    protected static Logger log = LogManager.getLogger();
    protected Server serverSSLCert = null;
    protected DynAddTrustManager serverCertTrustManager = null;


    // Test configurations

    @BeforeAll
    public static void setUpAll() throws UtilsJKS.GenerationException {
        log.debug(Markers.TEST_SPACER, "########## ########## ########## ########## ##########");
        log.debug(Markers.TEST_METHODS, "setUpAll");

        log.debug(Markers.TEST, String.format("Generation of server keystore and export certificate to file '%s'", SRV_CERT_PUB_PATH));
        KeyStore serverKs = UtilsJKS.generateKeyStore(ID_SERVER, "ksPass", "serverCertificateAlias");
        UtilsJKS.exportCertificate(serverKs, SRV_CERT_PUB_PATH, "serverCertificateAlias");

        log.debug(Markers.TEST, String.format("Generation of client keystore and export certificate to file '%s'", CLI_CERT_PUB_PATH));
        KeyStore clientKs = UtilsJKS.generateKeyStore(ID_CLIENT, "ksPass", "clientCertificateAlias");
        UtilsJKS.exportCertificate(clientKs, CLI_CERT_PUB_PATH, "clientCertificateAlias");
    }

    @AfterAll
    public static void tearDownAll() {
        log.debug(Markers.TEST_METHODS, "tearDownAll");

        if (new File(SRV_CERT_PUB_PATH).delete())
            log.debug(Markers.TEST, String.format("Public certificate file '%s' delete successfully", SRV_CERT_PUB_PATH));
        else
            log.debug(Markers.TEST, String.format("Error on deleting public certificate file '%s'", SRV_CERT_PUB_PATH));

        if (new File(CLI_CERT_PUB_PATH).delete())
            log.debug(Markers.TEST, String.format("Public certificate file '%s' delete successfully", CLI_CERT_PUB_PATH));
        else
            log.debug(Markers.TEST, String.format("Error on deleting public certificate file '%s'", CLI_CERT_PUB_PATH));
    }

    @BeforeEach
    public void setUp() throws SSLCertServer.SSLCertServerException {
        log.debug(Markers.TEST_SPACER, "########## ########## ########## ########## ##########");
        log.debug(Markers.TEST_METHODS, "setUp");

        // Init test server
        serverCertTrustManager = new DynAddTrustManager();
        serverSSLCert = new SSLCertServer(ID_SERVER, PORT, SRV_CERT_PUB_PATH, serverCertTrustManager);

        log.debug(Markers.TEST_METHODS, "test");
    }

    @AfterEach
    public void tearDown() {
        log.debug(Markers.TEST_METHODS, "tearDown");

        // If still running, stop test server
        if (serverSSLCert.isRunning())
            serverSSLCert.stop();
        serverCertTrustManager = null;
    }


    // isRunning, start and stop

    @Test
    public void testSSLCert() throws Server.ListeningException, InterruptedException, IOException {
        // Start server and connect client
        startServer(serverSSLCert);
        Socket s = connectClient(LOCALHOST, PORT, null);

        // Wait for server data
        DataInputStream in = new DataInputStream(s.getInputStream());
        int availablesBytes = 0;
        while (availablesBytes == 0)
            availablesBytes = in.available();
        Assertions.assertNotEquals(availablesBytes, 0);

        // Read all data from server
        byte[] allDataRead = new byte[0];
        while (in.available() > 0) {
            byte[] dataRead = new byte[in.available()];
            //noinspection ResultOfMethodCallIgnored
            in.read(dataRead);
            allDataRead = concatenate(allDataRead, dataRead);
        }
        s.close();
        Assertions.assertNotEquals(0, allDataRead.length);

        // Load and validate certificate
        Certificate serverPubCert = null;
        try {
            serverPubCert = UtilsJKS.loadCertificateFromBytes(allDataRead);
        } catch (UtilsJKS.LoadingException ignore) {}
        Assertions.assertNotNull(serverPubCert);

        // Stop test server
        stopServer(serverSSLCert);
    }

    @Test
    public void testSSLCertClientAuthentication() throws Server.ListeningException, InterruptedException, IOException {
        // Start server and connect client
        startServer(serverSSLCert);
        Socket s = connectClient(LOCALHOST, PORT, null);

//        // Wait for server data
//        DataInputStream in = new DataInputStream(s.getInputStream());
//        while (in.available()==0) ;
//        Assertions.assertNotEquals(in.available(),0);
//
//        // Read all data from server
//        byte[] allDataRead = new byte[0];
//        while(in.available()>0) {
//            byte[] dataRead = new byte[in.available()];
//            in.read(dataRead);
//            allDataRead = concatenate(allDataRead,dataRead);
//        }
//        s.close();
//        Assertions.assertNotEquals(0,allDataRead.length);
//
//        // Load and validate certificate
//        Certificate serverPubCert = null;
//        try {
//            serverPubCert = UtilsJKS.loadCertificateFromBytes(allDataRead);
//        } catch (UtilsJKS.LoadingException ignore) {
//            ignore.printStackTrace();
//        }
//        Assertions.assertNotNull(serverPubCert);


        Assertions.assertEquals(0, serverCertTrustManager.getAcceptedIssuers().length);

        // Read from file and send to server
        InputStream clientCertIn = new FileInputStream(CLI_CERT_PUB_PATH);
        OutputStream out = s.getOutputStream();
        int byteReadTx;
        //noinspection unused
        int countTx = 0;
        while ((byteReadTx = clientCertIn.read()) != -1) {
            out.write(byteReadTx);
            countTx++;
        }
        //System.out.println(String.format("Tx %d bytes", countTx));

        // Wait that server read all send bytes and close connection
        Thread.sleep(100);
        s.close();

        // Wait that server store certificate and check added certificate
        Thread.sleep(100);
        Assertions.assertEquals(1, serverCertTrustManager.getAcceptedIssuers().length);

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

    @SuppressWarnings("SameParameterValue")
    private Socket connectClient(InetAddress localhost, int port, CountDownLatch latchOnConnection) throws IOException, InterruptedException {
        Socket s = new Socket(localhost, port);
        //noinspection RedundantIfStatement
        if (latchOnConnection != null)
            assert latchOnConnection.await(1, TimeUnit.SECONDS);
        return s;
    }

    private byte[] concatenate(byte[] firstArray, byte[] secondArray) {
        byte[] concatenated = new byte[firstArray.length + secondArray.length];
        System.arraycopy(firstArray, 0, concatenated, 0, firstArray.length);
        System.arraycopy(secondArray, 0, concatenated, firstArray.length, secondArray.length);
        return concatenated;
    }

}