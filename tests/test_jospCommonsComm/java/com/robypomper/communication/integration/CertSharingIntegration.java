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

package com.robypomper.communication.integration;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.client.CertSharingSSLClient;
import com.robypomper.communication.client.Client;
import com.robypomper.communication.client.events.LatchClientLocalEventsListener;
import com.robypomper.communication.client.events.LatchClientMessagingEventsListener;
import com.robypomper.communication.client.events.LatchClientServerEventsListener;
import com.robypomper.communication.client.standard.SSLCertClient;
import com.robypomper.communication.server.CertSharingSSLServer;
import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.DefaultServer;
import com.robypomper.communication.server.Server;
import com.robypomper.communication.server.events.LatchServerClientEventsListener;
import com.robypomper.communication.server.events.LatchServerLocalEventsListener;
import com.robypomper.communication.server.events.LatchServerMessagingEventsListener;
import com.robypomper.communication.server.standard.SSLCertServer;
import com.robypomper.log.Mrk_Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class CertSharingIntegration {

    // Class constants

    static final String TEST_FILES_PREFIX = "tmp/tests/";
    final static String ID_SERVER = "TestServer";
    final static String ID_CLIENT = "TestClient";
    final static int PORT = 1234;
    final static InetAddress LOCALHOST = InetAddress.getLoopbackAddress();
    final static String SERVER_KS_PATH = TEST_FILES_PREFIX + String.format("server-%s.p12", CertSharingIntegration.class.getSimpleName());
    final static String SERVER_KS_PASS = "ksPass";
    final static String SERVER_CERT_ALIAS = "serverLocalCert";
    final static String SERVER_CERT_PATH = TEST_FILES_PREFIX + String.format("server-%s.crt", CertSharingIntegration.class.getSimpleName());
    final static String CLIENT_KS_PATH = TEST_FILES_PREFIX + String.format("client-%s.p12", CertSharingIntegration.class.getSimpleName());
    final static String CLIENT_KS_PASS = "ksPass";
    final static String CLIENT_CERT_ALIAS = "clientLocalCert";
    final static String CLIENT_CERT_PATH = TEST_FILES_PREFIX + String.format("client-%s.crt", CertSharingIntegration.class.getSimpleName());


    // Internal vars

    protected static Logger log = LogManager.getLogger();

    LatchServerLocalEventsListener latchSLE;
    LatchServerClientEventsListener latchSCE;
    LatchServerMessagingEventsListener latchSME;
    protected Server serverLatch = null;
    protected Server serverLatchAuth = null;
    protected Server serverLatchTmp = null;
    protected Server serverLatchTmpAuth = null;

    protected LatchClientLocalEventsListener latchCLE;
    protected LatchClientServerEventsListener latchCSE;
    protected LatchClientMessagingEventsListener latchCME;
    protected Client clientLatch = null;
    protected Client clientLatchAuth = null;
    protected Client clientLatchTmp = null;
    protected Client clientLatchTmpAuth = null;


    // Test configurations

    @BeforeEach
    public void setUp() throws UtilsJKS.GenerationException, SSLCertClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsSSL.GenerationException, SSLCertServer.SSLCertServerException, UtilsJKS.StoreException {
        log.debug(Mrk_Test.TEST_SPACER, "########## ########## ########## ########## ##########");
        log.debug(Mrk_Test.TEST_METHODS, "setUp");

        // Init test servers
        latchSLE = new LatchServerLocalEventsListener();
        latchSCE = new LatchServerClientEventsListener();
        latchSME = new LatchServerMessagingEventsListener();
        serverLatch = new CertSharingSSLServer(ID_SERVER, PORT,
                SERVER_KS_PATH + 1, SERVER_KS_PASS, SERVER_CERT_ALIAS, SERVER_CERT_PATH + 1, false,
                latchSLE, latchSCE, latchSME);
        serverLatchAuth = new CertSharingSSLServer(ID_SERVER, PORT,
                SERVER_KS_PATH + 2, SERVER_KS_PASS, SERVER_CERT_ALIAS, SERVER_CERT_PATH + 2, true,
                latchSLE, latchSCE, latchSME);
        serverLatchTmp = new CertSharingSSLServer(ID_SERVER, PORT,
                SERVER_CERT_ALIAS, SERVER_CERT_PATH + 3, false,
                latchSLE, latchSCE, latchSME);
        serverLatchTmpAuth = new CertSharingSSLServer(ID_SERVER, PORT,
                SERVER_CERT_ALIAS, SERVER_CERT_PATH + 4, true,
                latchSLE, latchSCE, latchSME);

        // Init test clients
        latchCLE = new LatchClientLocalEventsListener();
        latchCSE = new LatchClientServerEventsListener();
        latchCME = new LatchClientMessagingEventsListener();

        clientLatch = new CertSharingSSLClient(ID_CLIENT, LOCALHOST, PORT,
                CLIENT_KS_PATH + 1, CLIENT_KS_PASS, CLIENT_CERT_ALIAS,
                latchCLE, latchCSE, latchCME);
        clientLatchAuth = new CertSharingSSLClient(ID_CLIENT, LOCALHOST, PORT,
                CLIENT_KS_PATH + 2, CLIENT_KS_PASS, CLIENT_CERT_ALIAS, CLIENT_CERT_PATH + 2,
                latchCLE, latchCSE, latchCME);
        clientLatchTmp = new CertSharingSSLClient(ID_CLIENT, LOCALHOST, PORT,
                CLIENT_CERT_ALIAS,
                latchCLE, latchCSE, latchCME);
        clientLatchTmpAuth = new CertSharingSSLClient(ID_CLIENT, LOCALHOST, PORT,
                CLIENT_CERT_ALIAS, CLIENT_CERT_PATH + 4,
                latchCLE, latchCSE, latchCME);


        log.debug(Mrk_Test.TEST_METHODS, "test");
    }

    @AfterEach
    public void tearDown() {
        log.debug(Mrk_Test.TEST_METHODS, "tearDown");

        // If still connected, disconnect test clients
        if (clientLatch.isConnected())
            clientLatch.disconnect();
        if (clientLatchAuth.isConnected())
            clientLatchAuth.disconnect();
        if (clientLatchTmp.isConnected())
            clientLatchTmp.disconnect();

        // If still running, stop test servers
        if (serverLatch.isRunning())
            serverLatch.stop();
        if (serverLatchAuth.isRunning())
            serverLatchAuth.stop();
        if (serverLatchTmp.isRunning())
            serverLatchTmp.stop();

        // Delete exported certificate files
        for (int n = 1; n <= 2; n++)
            if (new File(SERVER_KS_PATH + n).delete())
                log.debug(Mrk_Test.TEST, String.format("Public client certificate file '%s' delete successfully", SERVER_KS_PATH + n));
            else
                log.debug(Mrk_Test.TEST, String.format("Error on deleting public client certificate file '%s'", SERVER_KS_PATH + n));

        for (int n = 1; n <= 4; n++)
            if (new File(SERVER_CERT_PATH + n).delete())
                log.debug(Mrk_Test.TEST, String.format("Public client certificate file '%s' delete successfully", SERVER_CERT_PATH + n));
            else
                log.debug(Mrk_Test.TEST, String.format("Error on deleting public client certificate file '%s'", SERVER_CERT_PATH + n));

        for (int n = 1; n <= 2; n++)
            if (new File(CLIENT_KS_PATH + n).delete())
                log.debug(Mrk_Test.TEST, String.format("Public client certificate file '%s' delete successfully", CLIENT_KS_PATH + n));
            else
                log.debug(Mrk_Test.TEST, String.format("Error on deleting public client certificate file '%s'", CLIENT_KS_PATH + n));

        for (int n = 2; n <= 4; n = n + 2)
            if (new File(CLIENT_CERT_PATH + n).delete())
                log.debug(Mrk_Test.TEST, String.format("Public client certificate file '%s' delete successfully", CLIENT_CERT_PATH + n));
            else
                log.debug(Mrk_Test.TEST, String.format("Error on deleting public client certificate file '%s'", CLIENT_CERT_PATH + n));
    }


    // Connection, client send data and disconnection
    // Normal, withAuth, Tmp and Tmp withAuth

    @Test
    public void testConnectionDisconnection() throws Server.ListeningException, Client.ConnectionException, InterruptedException, Client.ServerNotConnectedException {
        Server server = serverLatch;
        Client client = clientLatch;

        startAndConnect(server, client);

        // Check connection
        Assertions.assertTrue(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));

        // Check client registered in the server
        Assertions.assertEquals(2, server.getClients().size());
        ClientInfo clientInfo = server.getClients().get(1);
        Assertions.assertEquals(String.format(DefaultServer.ID_CLI_FORMAT, client.getServerInfo().getLocalAddress(), client.getServerInfo().getLocalPort()), clientInfo.getClientId());

        clientSendTestData(client);

        disconnectAndStop(server, client);
    }

    @Test
    public void testConnectionDisconnectionWithAuth() throws Server.ListeningException, Client.ConnectionException, InterruptedException, Client.ServerNotConnectedException {
        Server server = serverLatchAuth;
        Client client = clientLatchAuth;

        startAndConnect(server, client);

        // Check connection
        Assertions.assertTrue(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));

        // Check client registered in the server
        // (client attempt 2 connections, but 1st one can't register client because missing client's id)
        Assertions.assertEquals(1, server.getClients().size());
        ClientInfo clientInfo = server.getClients().get(0);
        Assertions.assertEquals(ID_CLIENT, clientInfo.getClientId());

        clientSendTestData(client);

        disconnectAndStop(server, client);
    }

    @Test
    public void testConnectionDisconnectionTemp() throws Server.ListeningException, Client.ConnectionException, InterruptedException, Client.ServerNotConnectedException {
        Server server = serverLatchTmp;
        Client client = clientLatchTmp;

        startAndConnect(server, client);

        // Check connection
        Assertions.assertTrue(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));

        // Check client registered in the server
        Assertions.assertEquals(2, server.getClients().size());
        ClientInfo clientInfo = server.getClients().get(1);
        Assertions.assertEquals(String.format(DefaultServer.ID_CLI_FORMAT, client.getServerInfo().getLocalAddress(), client.getServerInfo().getLocalPort()), clientInfo.getClientId());

        clientSendTestData(client);

        disconnectAndStop(server, client);
    }

    @Test
    public void testConnectionDisconnectionTempWithAuth() throws Server.ListeningException, Client.ConnectionException, InterruptedException, Client.ServerNotConnectedException {
        Server server = serverLatchTmpAuth;
        Client client = clientLatchTmpAuth;

        startAndConnect(server, client);

        // Check connection
        Assertions.assertTrue(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));

        // Check client registered in the server
        // (client attempt 2 connections, but 1st one can't register client because missing client's id)
        Assertions.assertEquals(1, server.getClients().size());
        ClientInfo clientInfo = server.getClients().get(0);
        Assertions.assertEquals(ID_CLIENT, clientInfo.getClientId());

        clientSendTestData(client);

        disconnectAndStop(server, client);
    }


    // Load already populated keystores

    @Test
    public void testClientPopulatedKeyStore() throws Server.ListeningException, Client.ConnectionException, InterruptedException, Client.ServerNotConnectedException, UtilsJKS.StoreException, UtilsJKS.GenerationException, SSLCertClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsSSL.GenerationException, SSLCertServer.SSLCertServerException {
        Server server = serverLatch;
        Client client = clientLatch;

        startAndConnect(server, client);

        // Check connection
        Assertions.assertTrue(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));

        // Check client registered in the server
        Assertions.assertEquals(2, server.getClients().size());
        ClientInfo clientInfo = server.getClients().get(1);
        Assertions.assertEquals(String.format(DefaultServer.ID_CLI_FORMAT, client.getServerInfo().getLocalAddress(), client.getServerInfo().getLocalPort()), clientInfo.getClientId());

        clientSendTestData(client);

        disconnectAndStop(server, client);

        // Re-create server and client
        // (client with already existing keystore containing the server certificate)
        serverLatch = new CertSharingSSLServer(ID_SERVER, PORT,
                SERVER_KS_PATH + 1, SERVER_KS_PASS, SERVER_CERT_ALIAS, SERVER_CERT_PATH + 1, false,
                latchSLE, latchSCE, latchSME);
        clientLatch = new CertSharingSSLClient(ID_CLIENT, LOCALHOST, PORT,
                CLIENT_KS_PATH + 1, CLIENT_KS_PASS, CLIENT_CERT_ALIAS, null,
                latchCLE, latchCSE, latchCME);
        server = serverLatch;
        client = clientLatch;

        startAndConnect(server, client);

        // Check connection
        Assertions.assertTrue(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));

        // Check client registered in the server
        // (now client can connect on 1st try, because the server already know client cert)
        Assertions.assertEquals(1, server.getClients().size());
        clientInfo = server.getClients().get(0);
        Assertions.assertEquals(String.format(DefaultServer.ID_CLI_FORMAT, client.getServerInfo().getLocalAddress(), client.getServerInfo().getLocalPort()), clientInfo.getClientId());

        clientSendTestData(client);

        disconnectAndStop(server, client);
    }

    @Test
    public void testServerPopulatedKeyStore() throws Server.ListeningException, Client.ConnectionException, InterruptedException, Client.ServerNotConnectedException, UtilsJKS.StoreException, UtilsJKS.GenerationException, SSLCertClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsSSL.GenerationException, SSLCertServer.SSLCertServerException {
        Server server = serverLatchAuth;
        Client client = clientLatchAuth;

        startAndConnect(server, client);

        // Check connection
        Assertions.assertTrue(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));

        // Check client registered in the server
        // (client attempt 2 connections, but 1st one can't register client because missing client's id)
        Assertions.assertEquals(1, server.getClients().size());
        ClientInfo clientInfo = server.getClients().get(0);
        Assertions.assertEquals(ID_CLIENT, clientInfo.getClientId());

        clientSendTestData(client);

        disconnectAndStop(server, client);

        // Re-create server and client
        // (client with already existing keystore containing the server certificate)
        try {
            serverLatchAuth = new CertSharingSSLServer(ID_SERVER, PORT,
                    SERVER_KS_PATH + 2, SERVER_KS_PASS, SERVER_CERT_ALIAS, SERVER_CERT_PATH + 2, true,
                    latchSLE, latchSCE, latchSME);
        } catch (UtilsJKS.LoadingException e) {
            return;
        }
        clientLatchAuth = new CertSharingSSLClient(ID_CLIENT, LOCALHOST, PORT,
                CLIENT_KS_PATH + 2, CLIENT_KS_PASS, CLIENT_CERT_ALIAS, CLIENT_CERT_PATH + 2,
                latchCLE, latchCSE, latchCME);
        server = serverLatchAuth;
        client = clientLatchAuth;

        startAndConnect(server, client);

        // Check connection
        Assertions.assertTrue(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));

        // Check client registered in the server
        // (client attempt 2 connections, but 1st one can't register client because missing client's id)
        Assertions.assertEquals(1, server.getClients().size());
        clientInfo = server.getClients().get(0);
        Assertions.assertEquals(ID_CLIENT, clientInfo.getClientId());

        clientSendTestData(client);

        disconnectAndStop(server, client);
    }


    private void startAndConnect(Server server, Client client) throws Server.ListeningException, Client.ConnectionException {
        System.out.println("\nSERVER START");
        server.start();
        System.out.println("\nCLIENT CONNECTION");
        client.connect();
    }

    private void clientSendTestData(Client client) throws Client.ServerNotConnectedException {
        System.out.println("\nCLIENT 2 SERVER");
        client.sendData("ExampleText");
    }

    private void disconnectAndStop(Server server, Client client) {
        System.out.println("\nCLIENT DISCONNECTION");
        client.disconnect();
        System.out.println("\nSERVER STOP");
        server.stop();
    }

}
