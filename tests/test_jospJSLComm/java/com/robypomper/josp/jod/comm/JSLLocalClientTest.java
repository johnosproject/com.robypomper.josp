package com.robypomper.josp.jod.comm;

import com.robypomper.communication.client.Client;
import com.robypomper.communication.server.Server;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.test.mocks.jod.MockJODCommunication;
import com.robypomper.josp.test.mocks.jod.MockJODObjectInfo;
import com.robypomper.josp.test.mocks.jod.MockJODPermissions;
import com.robypomper.josp.test.mocks.jsl.MockJSLCommunication;
import com.robypomper.log.Mrk_Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.InetAddress;
import java.util.Objects;

public class JSLLocalClientTest {

    // Class constants

    protected static final String TEST_FILES_PREFIX = "tmp/tests/";
    protected static final String KS_FILE = String.format("%s.p12", JSLLocalClientTest.class.getSimpleName());
    protected static final String PUB_CERT_PATH = String.format("%s.crt", JSLLocalClientTest.class.getSimpleName());
    protected static final String JSL_PUB_CERT_PATH = TEST_FILES_PREFIX + PUB_CERT_PATH;
    protected static final InetAddress LOCALHOST = InetAddress.getLoopbackAddress();


    // Internal vars

    protected static Logger log = LogManager.getLogger();
    protected static JODLocalServer jodServer;
    protected static int port = 1234;


    // Test config

    @BeforeEach
    public void setUp() throws Server.ListeningException {
        log.debug(Mrk_Test.TEST_SPACER, "########## ########## ########## ########## ##########");
        log.debug(Mrk_Test.TEST_METHODS, "setUp");

        // Create test dir
        File testDirFiles = new File(TEST_FILES_PREFIX);
        //noinspection ResultOfMethodCallIgnored
        testDirFiles.mkdirs();

        port++;

        // Start server
        jodServer = new JODLocalServer(new MockJODCommunication(), new MockJODObjectInfo(), new MockJODPermissions(), port,
                TEST_FILES_PREFIX + "jodSr-" + PUB_CERT_PATH);
        jodServer.start();

        log.debug(Mrk_Test.TEST_METHODS, "test");
    }

    @AfterEach
    public void tearDown() {
        // Stop JOD servers
        if (jodServer.isRunning())
            jodServer.stop();

        // Empty test dir
        File testDirFiles = new File(TEST_FILES_PREFIX);
        for (String s : Objects.requireNonNull(testDirFiles.list())) {
            File currentFile = new File(testDirFiles.getPath(), s);
            //noinspection ResultOfMethodCallIgnored
            currentFile.delete();
        }
        //noinspection ResultOfMethodCallIgnored
        testDirFiles.delete();
    }


    // Connect and disconnect

    @Test
    public void testLocalConnectAndDisconnect() throws Client.ConnectionException {
        System.out.println("\nJSL LOCAL CLIENT CONNECT");
        JSLLocalClient client = new JSLLocalClient(new MockJSLCommunication(), "srvId/usrId/instId", LOCALHOST, port,
                JSL_PUB_CERT_PATH);
        client.connect();
        Assertions.assertTrue(client.isConnected());
        Assertions.assertEquals(jodServer.getServerId(), client.getObjId());

        System.out.println("\nJSL LOCAL CLIENT DISCONNECT");
        client.disconnect();
        Assertions.assertFalse(client.isConnected());
    }

    @Test
    public void testLocalConnectAndServerStop() throws Client.ConnectionException {
        System.out.println("\nJSL LOCAL CLIENT CONNECT");
        JSLLocalClient client = new JSLLocalClient(new MockJSLCommunication(), "srvId/usrId/instId", LOCALHOST, port,
                JSL_PUB_CERT_PATH);
        client.connect();
        Assertions.assertTrue(client.isConnected());

        Assertions.assertEquals(jodServer.getServerId(), client.getObjId());

        System.out.println("\nJOD LOCAL SERVER STOP");
        jodServer.stop();
        Assertions.assertFalse(client.isConnected());
    }

}
