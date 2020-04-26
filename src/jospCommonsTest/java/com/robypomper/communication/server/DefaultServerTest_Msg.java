package com.robypomper.communication.server;

import com.robypomper.communication.peer.PeerInfo;
import com.robypomper.communication.server.events.LogServerLocalEventsListener;
import com.robypomper.communication.server.events.LogServerMessagingEventsListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DefaultServerTest_Msg extends DefaultServerTest_Base {

    // Server messaging

    @Test
    public void testReceivedData() throws Server.ListeningException, IOException, InterruptedException {
        byte[] originalDataByte = "bytesData".getBytes(PeerInfo.CHARSET);
        String originalDataString = "bytesString";

        CountDownLatch onDataReceivedBytes = new CountDownLatch(1);
        CountDownLatch onDataReceivedString = new CountDownLatch(1);

        Server server = new DefaultServer(ID_SERVER, PORT,
                new LogServerLocalEventsListener(),
                latchSCE,
                new LogServerMessagingEventsListener() {

                    @Override
                    public boolean onDataReceived(ClientInfo client, byte[] readData) throws Throwable {
                        super.onDataReceived(client, readData);
                        if (Arrays.equals(readData, originalDataByte)) {
                            onDataReceivedBytes.countDown();
                            return true;
                        }

                        return false;
                    }

                    @Override
                    public boolean onDataReceived(ClientInfo client, String readData) throws Throwable {
                        super.onDataReceived(client, readData);
                        if (readData.equals(originalDataString)) {
                            onDataReceivedString.countDown();
                            return true;
                        }

                        return false;
                    }

                });

        // Start server and connect client
        startServer(server);
        Socket s = connectClient(LOCALHOST, PORT, latchSCE.onClientConnection);

        // Client send byte data
        clientSend(s, originalDataByte);
        Assertions.assertTrue(onDataReceivedBytes.await(1, TimeUnit.SECONDS));

        // Client send string data
        clientSend(s, originalDataString.getBytes(PeerInfo.CHARSET));
        Assertions.assertTrue(onDataReceivedString.await(1, TimeUnit.SECONDS));

        // Close client connection and stop server
        s.close();
        stopServer(server);
    }

    @Test
    public void testSendData() throws Server.ListeningException, IOException, InterruptedException, Server.ClientNotFoundException, Server.ServerStoppedException, Server.ClientNotConnectedException {
        CountDownLatch onDataSendBytes = new CountDownLatch(1);
        CountDownLatch onDataSendString = new CountDownLatch(1);

        Server server = new DefaultServer(ID_SERVER, PORT,
                new LogServerLocalEventsListener(),
                latchSCE,
                new LogServerMessagingEventsListener() {

                    @Override
                    public void onDataSend(ClientInfo client, byte[] writtenData) {
                        super.onDataSend(client, writtenData);
                        onDataSendBytes.countDown();
                    }

                    @Override
                    public void onDataSend(ClientInfo client, String writtenData) {
                        super.onDataSend(client, writtenData);
                        onDataSendString.countDown();
                    }

                });

        // Start server and connect client
        startServer(server);
        Socket s = connectClient(LOCALHOST, PORT, latchSCE.onClientConnection);
        ClientInfo client = server.getClients().get(0);

        // Server send byte data
        server.sendData(client.getClientId(), "ExampleData");
        Assertions.assertTrue(onDataSendBytes.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(onDataSendString.await(1, TimeUnit.SECONDS));

        // Close client connection and stop server
        s.close();
        stopServer(server);
    }

}