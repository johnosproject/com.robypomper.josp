package com.robypomper.communication.server;

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
        startServer(serverLatch);

        // Connect client
        Socket s = new Socket(LOCALHOST, PORT);
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
        s.close();
        Assertions.assertTrue(latchSCE.onClientDisconnection.await(1, TimeUnit.SECONDS));

        // Check client list
        clients = serverLatch.getClients();
        printClientInfoList(clients);
        Assertions.assertEquals(1, clients.size());

        // Check client status
        Assertions.assertFalse(client.isConnected());

        // Stop test server
        stopServer(serverLatch);
    }

    @Test
    public void testClientDisconnectionGoodbye() throws Server.ListeningException, IOException, InterruptedException {
        // Start test server and connect client
        startServer(serverLatch);
        Socket s = connectClient(LOCALHOST, PORT, latchSCE.onClientConnection);

        // Disconnect client (terminate)
        clientSend(s, DefaultServer.MSG_BYE_SRV);
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
        Socket s = connectClient(LOCALHOST, PORT, latchSCE.onClientConnection);

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
        startServer(serverLatch);
        connectClient(LOCALHOST, PORT, latchSCE.onClientConnection);

        // Stop test server
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
        connectClient(LOCALHOST, PORT, latchSCE.onClientConnection);

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
        startServer(serverLatch);
        Assertions.assertTrue(latchSLE.onStarted.await(1000, TimeUnit.SECONDS));

        // Connect client
        Socket s = new Socket(LOCALHOST, PORT);
        Assertions.assertTrue(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));

        // Check client list
        List<ClientInfo> clients = serverLatch.getClients();
        printClientInfoList(clients);
        Assertions.assertEquals(1, clients.size());
        Assertions.assertTrue(clients.get(0).isConnected());

        // Disconnect client
        s.close();
        Assertions.assertTrue(latchSCE.onClientDisconnection.await(1, TimeUnit.SECONDS));

        // Check client list
        clients = serverLatch.getClients();
        printClientInfoList(clients);
        Assertions.assertEquals(1, clients.size());
        Assertions.assertFalse(clients.get(0).isConnected());

        // Reconnect client
        latchSCE.onClientConnection = new CountDownLatch(1);
        s = new Socket(LOCALHOST, PORT);
        Assertions.assertTrue(latchSCE.onClientConnection.await(1, TimeUnit.SECONDS));

        // Check client list
        clients = serverLatch.getClients();
        printClientInfoList(clients);
        Assertions.assertEquals(2, clients.size());

        // Stop test server
        s.close();
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
        Server server = new DefaultServer(ID_SERVER, PORT,
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
        Socket s = connectClient(LOCALHOST, PORT, latchSCE.onClientConnection);

        // Client send data
        clientSend(s, "ExampleData".getBytes(PeerInfo.CHARSET));

        // Check client error event
        Assertions.assertTrue(latchSCE.onClientError.await(1, TimeUnit.SECONDS));

        // Close client connection and stop server
        s.close();
        stopServer(server);
    }

}