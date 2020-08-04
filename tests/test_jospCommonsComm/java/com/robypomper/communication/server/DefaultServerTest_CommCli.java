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

package com.robypomper.communication.server;

import com.robypomper.communication.client.DefaultClient;
import com.robypomper.communication.peer.PeerInfo;
import com.robypomper.communication.server.events.LogServerLocalEventsListener;
import com.robypomper.communication.server.events.LogServerMessagingEventsListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class DefaultServerTest_CommCli extends DefaultServerTest_Base {

    // Clients

    @Test
    public void testClientConnectionAndDisconnection() throws Server.ListeningException, IOException, InterruptedException {
        // Start test server
        System.out.println("\nSERVER START");
        startServer(serverLatch);

        // Connect client
        System.out.println("\nCLIENT CONNECT");
        Socket s = new Socket(LOCALHOST, port);
        Assertions.assertTrue(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));

        // Check client list
        List<ClientInfo> clients = serverLatch.getClients();
        printClientInfoList(clients);
        Assertions.assertEquals(1, clients.size());

        // Check client status
        ClientInfo client = clients.get(0);
        Assertions.assertTrue(client.isConnected());
        String calculatedClientId = String.format(DefaultServer.ID_CLI_FORMAT, s.getLocalAddress(), s.getLocalPort());
        Assertions.assertEquals(calculatedClientId, client.getClientId());

        // Disconnect client
        System.out.println("\nCLIENT DISCONNECT");
        s.close();
        Assertions.assertTrue(latchSCE.onClientDisconnection.await(10, TimeUnit.SECONDS));

        // Check client list
        clients = serverLatch.getClients();
        printClientInfoList(clients);
        Assertions.assertEquals(0, clients.size());

        // Check client status
        Assertions.assertFalse(client.isConnected());

        // Stop test server
        System.out.println("\nSERVER STOP");
        stopServer(serverLatch);
    }

    @Test
    public void testClientDisconnectionGoodbye() throws Server.ListeningException, IOException, InterruptedException {
        // Start test server and connect client
        startServer(serverLatch);
        Socket s = connectClient(LOCALHOST, port, latchSCE.onClientConnection);

        // Disconnect client (terminate)
        clientSend(s, DefaultClient.MSG_BYE_CLI);
        s.close();
        Assertions.assertTrue(latchSCE.onClientDisconnection.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(latchSCE.onClientGoodbye.await(1, TimeUnit.SECONDS));

        // Stop test server
        stopServer(serverLatch);
    }

    @Test
    public void testClientDisconnectionTerminate() throws Server.ListeningException, IOException, InterruptedException {
        // Start test server and connect client
        startServer(serverLatch);
        Socket s = connectClient(LOCALHOST, port, latchSCE.onClientConnection);

        // Disconnect client (terminate)
        s.close();
        Assertions.assertTrue(latchSCE.onClientDisconnection.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(latchSCE.onClientTerminated.await(1, TimeUnit.SECONDS));

        // Stop test server
        stopServer(serverLatch);
    }

    @Test
    public void testClientDisconnectionFromServerStopServer() throws Server.ListeningException, IOException, InterruptedException {
        // Start test server and connect client
        System.out.println("\nSERVER START");
        startServer(serverLatch);
        Thread.sleep(100);

        System.out.println("\nCLIENT CONNECT");
        connectClient(LOCALHOST, port, latchSCE.onClientConnection);
        Thread.sleep(100);

        // Stop test server
        System.out.println("\nSERVER STOP");
        stopServer(serverLatch);

        // Check client disconnection
        Assertions.assertTrue(latchSCE.onClientDisconnection.await(2, TimeUnit.SECONDS));
        Assertions.assertTrue(latchSCE.onClientServerDisconnected.await(1, TimeUnit.SECONDS));
    }

    @Test
    @Disabled
    public void testClientDisconnectionFromServerCloseConnection() throws Server.ListeningException, IOException, InterruptedException {
        // Start test server and connect client
        startServer(serverLatch);
        connectClient(LOCALHOST, port, latchSCE.onClientConnection);

        // Disconnect client (terminate)
        ClientInfo client = serverLatch.getClients().get(0);
        client.closeConnection();
        /*
         processClient non rileva che la connessione viene chiusa dal server e non dal client
         creare nuovo metodo ClientInfo:disconnect() che oltre a chiudere la connessione
         avvisa il metodo processClient che la connessione è chiusa dal lato del server
        */

        // Check client disconnection
        Assertions.assertTrue(latchSCE.onClientDisconnection.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(latchSCE.onClientServerDisconnected.await(1, TimeUnit.SECONDS));

        // Stop test server
        stopServer(serverLatch);
    }

    @Test
    public void testClientConnectionDouble() throws Server.ListeningException, IOException, InterruptedException {
        // Start test server
        System.out.println("\nSERVER START");
        startServer(serverLatch);
        Assertions.assertTrue(latchSLE.onStarted.await(1000, TimeUnit.SECONDS));

        // Connect client
        System.out.println("\nCLIENT CONNECT");
        Socket s = new Socket(LOCALHOST, port);
        Assertions.assertTrue(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));

        // Check client list
        List<ClientInfo> clients = serverLatch.getClients();
        printClientInfoList(clients);
        Assertions.assertEquals(1, clients.size());
        ClientInfo client = clients.get(0);
        Assertions.assertTrue(client.isConnected());

        // Disconnect client
        System.out.println("\nCLIENT DISCONNECT");
        s.close();
        Assertions.assertTrue(latchSCE.onClientDisconnection.await(1, TimeUnit.SECONDS));

        // Check client list
        clients = serverLatch.getClients();
        printClientInfoList(clients);
        Assertions.assertEquals(0, clients.size());
        Assertions.assertFalse(client.isConnected());

        // Reconnect client
        System.out.println("\nCLIENT RE-CONNECT");
        latchSCE.onClientConnection = new CountDownLatch(1);
        s = new Socket(LOCALHOST, port);
        Assertions.assertTrue(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));

        // Check client list
        clients = serverLatch.getClients();
        printClientInfoList(clients);
        Assertions.assertEquals(1, clients.size());

        // Disconnect client
        System.out.println("\nCLIENT RE_DISCONNECT");
        s.close();

        // Stop test server
        System.out.println("\nSERVER STOP");
        stopServer(serverLatch);
    }

    @Test
    @Disabled
    public void testClientErrorOnSetupStream() {
        throw new UnsupportedOperationException("Not testable");
    }

    @Test
    @Disabled
    public void testClientErrorOnReadStreamData() {
        throw new UnsupportedOperationException("Not testable");
    }

    @Test
    public void testClientErrorOnProcessRequest() throws Server.ListeningException, IOException, InterruptedException {
        Server server = new DefaultServer(ID_SERVER, port,
                new LogServerLocalEventsListener(),
                latchSCE,
                new LogServerMessagingEventsListener() {

                    @Override
                    public boolean onDataReceived(ClientInfo client, byte[] readData) throws Throwable {
                        super.onDataReceived(client, readData);
                        throw new Exception("(TEST) error on processing data");
                    }

                });

        // Start server and connect client
        startServer(server);
        Socket s = connectClient(LOCALHOST, port, latchSCE.onClientConnection);

        // Client send data
        clientSend(s, "ExampleData".getBytes(PeerInfo.CHARSET));

        // Check client error event
        Assertions.assertTrue(latchSCE.onClientError.await(1, TimeUnit.SECONDS));

        // Close client connection and stop server
        s.close();
        stopServer(server);
    }

}