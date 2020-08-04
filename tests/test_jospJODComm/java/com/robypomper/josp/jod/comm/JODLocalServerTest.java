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

package com.robypomper.josp.jod.comm;

import com.robypomper.communication.client.Client;
import com.robypomper.communication.server.ClientInfo;
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

public class JODLocalServerTest {

    // Class constants

    protected static final String TEST_FILES_PREFIX = "tmp/tests/";
    protected static final String PUB_CERT_PATH = String.format("%s.crt", JODLocalServerTest.class.getSimpleName());
    protected static final String JOD_PUB_CERT_PATH = TEST_FILES_PREFIX + PUB_CERT_PATH;
    protected static final InetAddress LOCALHOST = InetAddress.getLoopbackAddress();

    // Internal vars

    protected static Logger log = LogManager.getLogger();
    protected static int port = 1234;


    // Test config

    @BeforeEach
    public void setUp() {
        log.debug(Mrk_Test.TEST_SPACER, "########## ########## ########## ########## ##########");
        log.debug(Mrk_Test.TEST_METHODS, "setUp");

        // Create test dir
        File testDirFiles = new File(TEST_FILES_PREFIX);
        //noinspection ResultOfMethodCallIgnored
        testDirFiles.mkdirs();

        port += 2;

        log.debug(Mrk_Test.TEST_METHODS, "test");
    }

    @AfterEach
    public void tearDown() {
        File testDirFiles = new File(TEST_FILES_PREFIX);
        for (String s : Objects.requireNonNull(testDirFiles.list())) {
            File currentFile = new File(testDirFiles.getPath(), s);
            //noinspection ResultOfMethodCallIgnored
            currentFile.delete();
        }
        //noinspection ResultOfMethodCallIgnored
        testDirFiles.delete();
    }


    // Start and stop

    @Test
    public void testLocalStartAndStop() throws Server.ListeningException {
        JODLocalServer jodServer = new JODLocalServer(new MockJODCommunication(), new MockJODObjectInfo(), new MockJODPermissions(), port, JOD_PUB_CERT_PATH);
        Assertions.assertFalse(jodServer.isRunning());

        System.out.println("\nJOD LOCAL SERVER START");
        jodServer.start();
        Assertions.assertTrue(jodServer.isRunning());

        System.out.println("\nJOD LOCAL SERVER STOP");
        jodServer.stop();
        Assertions.assertFalse(jodServer.isRunning());
    }

    @Test
    public void testLocalDoubleStart_FAIL() throws Server.ListeningException {
        JODLocalServer jodServer = new JODLocalServer(new MockJODCommunication(), new MockJODObjectInfo(), new MockJODPermissions(), port, JOD_PUB_CERT_PATH);
        Assertions.assertFalse(jodServer.isRunning());

        System.out.println("\nJOD LOCAL SERVER START");
        jodServer.start();
        Assertions.assertTrue(jodServer.isRunning());

        JODLocalServer jodServer2 = new JODLocalServer(new MockJODCommunication(), new MockJODObjectInfo(), new MockJODPermissions(), port, JOD_PUB_CERT_PATH);
        Assertions.assertFalse(jodServer2.isRunning());

        System.out.println("\nJOD LOCAL SERVER 2nd START");
        Assertions.assertThrows(Server.ListeningException.class, jodServer2::start);
        Assertions.assertFalse(jodServer2.isRunning());

        System.out.println("\nJOD LOCAL SERVER STOP");
        jodServer.stop();
        Assertions.assertFalse(jodServer.isRunning());
    }


    // Client and connections status

    @Test
    public void testLocalConnectionAndDisconnection() throws Server.ListeningException, Client.ConnectionException {
        System.out.println("\nJOD LOCAL SERVER START");
        JODLocalServer jodServer = new JODLocalServer(new MockJODCommunication(), new MockJODObjectInfo(), new MockJODPermissions(), port, JOD_PUB_CERT_PATH);
        jodServer.start();

        System.out.println("\nJSL LOCAL CLIENT CONNECT");
        JSLLocalClient client = new JSLLocalClient(new MockJSLCommunication(), "srvId/usrId/instId", LOCALHOST, port,
                TEST_FILES_PREFIX + "jslCl-" + PUB_CERT_PATH);
        client.connect();

        switchThread();
        Assertions.assertEquals(1, getClientsCount(jodServer));
        Assertions.assertEquals(1, getClientsConnectedCount(jodServer));
        Assertions.assertEquals(1, getLocConnCount(jodServer));
        Assertions.assertEquals(1, getLocConnConnectedCount(jodServer));

        System.out.println("\nJSL LOCAL CLIENT DISCONNECT");
        client.disconnect();

        switchThread();
        Assertions.assertEquals(0, getClientsCount(jodServer));
        Assertions.assertEquals(0, getClientsConnectedCount(jodServer));
        Assertions.assertEquals(1, getLocConnCount(jodServer));
        Assertions.assertEquals(0, getLocConnConnectedCount(jodServer));

        System.out.println("\nJOD LOCAL SERVER STOP");
        jodServer.stop();
        Assertions.assertFalse(jodServer.isRunning());
    }

    @Test
    public void testLocalConnectionAndDisconnectionTwoClients() throws Server.ListeningException, Client.ConnectionException {
        System.out.println("\nJOD LOCAL SERVER START");
        JODLocalServer jodServer = new JODLocalServer(new MockJODCommunication(), new MockJODObjectInfo(), new MockJODPermissions(), port, JOD_PUB_CERT_PATH);
        jodServer.start();

        System.out.println("\nJSL LOCAL CLIENT CONNECT (x2)");
        JSLLocalClient client = new JSLLocalClient(new MockJSLCommunication(), "srvId/usrId/instId", LOCALHOST, port,
                TEST_FILES_PREFIX + "jslCl-" + PUB_CERT_PATH);
        client.connect();
        JSLLocalClient client2 = new JSLLocalClient(new MockJSLCommunication(), "srvId/usrId_2/instId", LOCALHOST, port,
                TEST_FILES_PREFIX + "jslCl2-" + PUB_CERT_PATH);
        client2.connect();

        switchThread();
        Assertions.assertEquals(2, getClientsCount(jodServer));
        Assertions.assertEquals(2, getClientsConnectedCount(jodServer));
        Assertions.assertEquals(2, getLocConnCount(jodServer));
        Assertions.assertEquals(2, getLocConnConnectedCount(jodServer));

        System.out.println("\nJSL LOCAL CLIENT DISCONNECT (1st)");
        client.disconnect();

        switchThread();
        Assertions.assertEquals(1, getClientsCount(jodServer));
        Assertions.assertEquals(1, getClientsConnectedCount(jodServer));
        Assertions.assertEquals(2, getLocConnCount(jodServer));
        Assertions.assertEquals(1, getLocConnConnectedCount(jodServer));


        System.out.println("\nJSL LOCAL CLIENT DISCONNECT (2nd)");
        client2.disconnect();

        switchThread();
        Assertions.assertEquals(0, getClientsCount(jodServer));
        Assertions.assertEquals(0, getClientsConnectedCount(jodServer));
        Assertions.assertEquals(2, getLocConnCount(jodServer));
        Assertions.assertEquals(0, getLocConnConnectedCount(jodServer));

        System.out.println("\nJOD LOCAL SERVER STOP");
        jodServer.stop();
        Assertions.assertFalse(jodServer.isRunning());
    }

    @Test
    public void testLocalConnectionAndDisconnectionTwoClientsSameIds() throws Server.ListeningException, Client.ConnectionException {
        System.out.println("\nJOD LOCAL SERVER START");
        JODLocalServer jodServer = new JODLocalServer(new MockJODCommunication(), new MockJODObjectInfo(), new MockJODPermissions(), port, JOD_PUB_CERT_PATH);
        jodServer.start();

        System.out.println("\nJSL LOCAL CLIENT CONNECT");
        JSLLocalClient client = new JSLLocalClient(new MockJSLCommunication(), "srvId/usrId/instId", LOCALHOST, port,
                TEST_FILES_PREFIX + "jslCl-" + PUB_CERT_PATH);
        client.connect();
        JSLLocalClient client2 = new JSLLocalClient(new MockJSLCommunication(), "srvId/usrId/instId", LOCALHOST, port,
                TEST_FILES_PREFIX + "jslCl2-" + PUB_CERT_PATH);
        client2.connect();

        switchThread();
        Assertions.assertEquals(1, getClientsCount(jodServer));          // JODLocalServer, DON't close clients of duplicated connections; and DefaultServer remove closed connections
        Assertions.assertEquals(1, getClientsConnectedCount(jodServer));
        Assertions.assertEquals(1, getLocConnCount(jodServer));          // JODLocalServer update connection from same client
        Assertions.assertEquals(1, getLocConnConnectedCount(jodServer));
        Assertions.assertTrue(client.isConnected());
        Assertions.assertFalse(client2.isConnected());

        System.out.println("\nJSL LOCAL CLIENT DISCONNECT");
        client2.disconnect();

        switchThread();
        Assertions.assertEquals(1, getClientsCount(jodServer));
        Assertions.assertEquals(1, getClientsConnectedCount(jodServer));
        Assertions.assertEquals(1, getLocConnCount(jodServer));
        Assertions.assertEquals(1, getLocConnConnectedCount(jodServer));

        System.out.println("\nJOD LOCAL SERVER STOP");
        jodServer.stop();
        Assertions.assertFalse(jodServer.isRunning());
    }

    @Test
    public void testLocalConnectionAndDisconnectionTwoClientsSameIdsDiffInstances() throws Server.ListeningException, Client.ConnectionException {
        System.out.println("\nJOD LOCAL SERVER START");
        JODLocalServer jodServer = new JODLocalServer(new MockJODCommunication(), new MockJODObjectInfo(), new MockJODPermissions(), port, JOD_PUB_CERT_PATH);
        jodServer.start();

        System.out.println("\nJSL LOCAL CLIENT CONNECT");
        JSLLocalClient client = new JSLLocalClient(new MockJSLCommunication(), "srvId/usrId/instId_A", LOCALHOST, port,
                TEST_FILES_PREFIX + "jslCl-" + PUB_CERT_PATH);
        client.connect();
        JSLLocalClient client2 = new JSLLocalClient(new MockJSLCommunication(), "srvId/usrId/instId_B", LOCALHOST, port,
                TEST_FILES_PREFIX + "jslCl2-" + PUB_CERT_PATH);
        client2.connect();

        switchThread();
        Assertions.assertEquals(2, getClientsCount(jodServer));          // JODLocalServer, close clients of duplicated connections; and DefaultServer remove closed connections
        Assertions.assertEquals(2, getClientsConnectedCount(jodServer));
        Assertions.assertEquals(2, getLocConnCount(jodServer));          // JODLocalServer update connection from same client
        Assertions.assertEquals(2, getLocConnConnectedCount(jodServer));
        Assertions.assertTrue(client.isConnected());
        Assertions.assertTrue(client2.isConnected());

        System.out.println("\nJSL LOCAL CLIENT DISCONNECT");
        client2.disconnect();

        switchThread();
        Assertions.assertEquals(1, getClientsCount(jodServer));
        Assertions.assertEquals(1, getClientsConnectedCount(jodServer));
        Assertions.assertEquals(2, getLocConnCount(jodServer));
        Assertions.assertEquals(1, getLocConnConnectedCount(jodServer));

        System.out.println("\nJOD LOCAL SERVER STOP");
        jodServer.stop();
        Assertions.assertFalse(jodServer.isRunning());
    }

    @Test
    public void testLocalConnectionAndServerStop() throws Server.ListeningException, Client.ConnectionException {
        System.out.println("\nJOD LOCAL SERVER START");
        JODLocalServer jodServer = new JODLocalServer(new MockJODCommunication(), new MockJODObjectInfo(), new MockJODPermissions(), port, JOD_PUB_CERT_PATH);
        jodServer.start();

        System.out.println("\nJSL LOCAL CLIENT CONNECT");
        JSLLocalClient client = new JSLLocalClient(new MockJSLCommunication(), "srvId/usrId/instId", LOCALHOST, port,
                TEST_FILES_PREFIX + "jslCl-" + PUB_CERT_PATH);
        client.connect();

        switchThread();
        Assertions.assertEquals(1, getClientsCount(jodServer));
        Assertions.assertEquals(1, getClientsConnectedCount(jodServer));
        Assertions.assertEquals(1, getLocConnCount(jodServer));
        Assertions.assertEquals(1, getLocConnConnectedCount(jodServer));

        System.out.println("\nJOD LOCAL SERVER STOP");
        jodServer.stop();
        Assertions.assertFalse(jodServer.isRunning());

        switchThread();
        Assertions.assertEquals(0, getClientsCount(jodServer));
        Assertions.assertEquals(0, getClientsConnectedCount(jodServer));
        Assertions.assertEquals(1, getLocConnCount(jodServer));
        Assertions.assertEquals(0, getLocConnConnectedCount(jodServer));
    }

    @Test
    public void testLocalConnectionAndServerStopTwoClients() throws Server.ListeningException, Client.ConnectionException {
        System.out.println("\nJOD LOCAL SERVER START");
        JODLocalServer jodServer = new JODLocalServer(new MockJODCommunication(), new MockJODObjectInfo(), new MockJODPermissions(), port, JOD_PUB_CERT_PATH);
        jodServer.start();

        System.out.println("\nJSL LOCAL CLIENT CONNECT (x2)");
        JSLLocalClient client = new JSLLocalClient(new MockJSLCommunication(), "srvId/usrId/instId", LOCALHOST, port,
                TEST_FILES_PREFIX + "jslCl-" + PUB_CERT_PATH);
        client.connect();
        JSLLocalClient client2 = new JSLLocalClient(new MockJSLCommunication(), "srvId/usrId_2/instId", LOCALHOST, port,
                TEST_FILES_PREFIX + "jslCl2-" + PUB_CERT_PATH);
        client2.connect();

        switchThread();
        Assertions.assertEquals(2, getClientsCount(jodServer));
        Assertions.assertEquals(2, getClientsConnectedCount(jodServer));
        Assertions.assertEquals(2, getLocConnCount(jodServer));
        Assertions.assertEquals(2, getLocConnConnectedCount(jodServer));

        System.out.println("\nJOD LOCAL SERVER STOP");
        jodServer.stop();
        Assertions.assertFalse(jodServer.isRunning());

        switchThread();
        Assertions.assertEquals(0, getClientsCount(jodServer));
        Assertions.assertEquals(0, getClientsConnectedCount(jodServer));
        Assertions.assertEquals(2, getLocConnCount(jodServer));
        Assertions.assertEquals(0, getLocConnConnectedCount(jodServer));
    }


    // Utils methods

    private int getClientsCount(JODLocalServer server) {
        return server.getClients().size();
    }

    private int getClientsConnectedCount(JODLocalServer server) {
        int count = 0;
        for (ClientInfo cli : server.getClients())
            if (cli.isConnected())
                count++;
        return count;
    }

    private int getLocConnCount(JODLocalServer server) {
        return server.getLocalClientsInfo().size();
    }

    private int getLocConnConnectedCount(JODLocalServer server) {
        int count = 0;
        for (JODLocalClientInfo conn : server.getLocalClientsInfo())
            if (conn.isConnected())
                count++;
        return count;
    }

    private void switchThread() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignore) {}
    }

}
