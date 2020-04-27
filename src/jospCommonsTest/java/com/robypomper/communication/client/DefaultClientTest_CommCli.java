package com.robypomper.communication.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;


public class DefaultClientTest_CommCli extends DefaultClientTest_Base {


    // idConnected, connect and disconnect

    @Test
    public void testIsConnected() throws Client.ConnectionException, InterruptedException, IOException {
        // Check test client isConnected==false
        assert !clientLatch.isConnected();

        // Start server
        ServerSocket server = new ServerSocket(PORT);

        // Start test client and check isConnected==true
        clientLatch.connect();
        Assertions.assertTrue(latchCLE.onConnected.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(clientLatch.isConnected());

        // Stop test client and check isConnected==false
        clientLatch.disconnect();
        Assertions.assertTrue(latchCLE.onDisconnected.await(1, TimeUnit.SECONDS));
        Assertions.assertFalse(clientLatch.isConnected());

        // Stop server
        server.close();
        assert server.isClosed();
    }

    @Test
    public void testConnectToServerNotRunning_FAIL() {
        // Check test client isConnected==false
        assert !clientLatch.isConnected();

        //// Start server DON'T start
        //ServerSocket server = new ServerSocket(PORT);

        // Start test client and check isConnected==true
        Assertions.assertThrows(Client.ConnectionException.class,clientLatch::connect);
        Assertions.assertFalse(clientLatch.isConnected());
    }

    @Test
    public void testServerInfo() throws Client.ConnectionException, InterruptedException, IOException {
        // Check test client null server info
        Assertions.assertNull(clientLatch.getServerInfo());

        // Start server
        ServerSocket server = new ServerSocket(PORT);

        // Start test client and check isConnected==true
        clientLatch.connect();
        latchCLE.onConnected.await(1, TimeUnit.SECONDS);

        // Check test client not null server info
        ServerInfo serverInfo = clientLatch.getServerInfo();
        Assertions.assertNotNull(serverInfo);
        //Assertions.assertEquals(server.getInetAddress(),serverInfo.getPeerAddress());
        //Expected :0.0.0.0/0.0.0.0
        //Actual   :localhost/127.0.0.1
        Assertions.assertEquals(LOCALHOST,serverInfo.getPeerAddress());
        Assertions.assertEquals(server.getLocalPort(),serverInfo.getPeerPort());
        Assertions.assertTrue(serverInfo.isConnected());


        // Stop test client and check isConnected==false
        clientLatch.disconnect();
        Assertions.assertTrue(latchCLE.onDisconnected.await(1, TimeUnit.SECONDS));


        // Check test client not null server info and disconnected
        ServerInfo serverInfo2 = clientLatch.getServerInfo();
        Assertions.assertNotNull(serverInfo2);
        Assertions.assertFalse(serverInfo.isConnected());
        Assertions.assertFalse(serverInfo2.isConnected());

        // Stop server
        server.close();
        assert server.isClosed();
    }

}
