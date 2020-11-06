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

package com.robypomper.communication.client;

import com.robypomper.communication.CommunicationBase;
import com.robypomper.communication.client.events.LogClientLocalEventsListener;
import com.robypomper.communication.client.events.LogClientMessagingEventsListener;
import com.robypomper.communication.peer.PeerInfo;
import com.robypomper.communication.server.DefaultServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;


public class DefaultClientTest_CommSrv extends DefaultClientTest_Base {

    // Server

    @Test
    public void testServerConnectionAndDisconnection() throws Client.ConnectionException, InterruptedException, IOException {
        // Check test client isConnected==false
        assert !clientLatch.isConnected();

        // Start server
        ServerSocket server = new ServerSocket(port);

        // Start test client and check isConnected==true
        clientLatch.connect();
        Assertions.assertTrue(latchCSE.onServerConnection.await(1, TimeUnit.SECONDS));

        // Stop test client and check isConnected==false
        clientLatch.disconnect();
        Assertions.assertTrue(latchCSE.onServerDisconnection.await(1, TimeUnit.SECONDS));
        Assertions.assertFalse(clientLatch.isConnected());

        // Stop server
        server.close();
        assert server.isClosed();
    }

    @Test
    public void testServerDisconnectionGoodbye() throws Client.ConnectionException, InterruptedException, IOException {
        // Check test client isConnected==false
        assert !clientLatch.isConnected();

        // Start server
        ServerSocket server = new ServerSocket(port);

        // Start test client and check isConnected==true
        clientLatch.connect();
        Assertions.assertTrue(latchCSE.onServerConnection.await(1, TimeUnit.SECONDS));

        // Send bye msg from server
        Socket serverClient = server.accept();
        CommunicationBase.transmitData(serverClient.getOutputStream(), DefaultServer.MSG_BYE_SRV);

        // Stop server
        server.close();
        assert server.isClosed();

        // Check isConnected==false
        Assertions.assertTrue(latchCSE.onServerDisconnection.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(latchCSE.onServerGoodbye.await(1, TimeUnit.SECONDS));
        Assertions.assertFalse(clientLatch.isConnected());
    }

    @Test
    public void testServerDisconnectionTerminate() throws Client.ConnectionException, InterruptedException, IOException {
        // Check test client isConnected==false
        assert !clientLatch.isConnected();

        // Start server
        ServerSocket server = new ServerSocket(port);

        // Start test client and check isConnected==true
        clientLatch.connect();
        Assertions.assertTrue(latchCSE.onServerConnection.await(1, TimeUnit.SECONDS));

        // Stop server without bye msg
        server.close();
        assert server.isClosed();

        // Check isConnected==false
        Assertions.assertTrue(latchCSE.onServerDisconnection.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(latchCSE.onServerTerminated.await(1, TimeUnit.SECONDS));
        Assertions.assertFalse(clientLatch.isConnected());
    }

    @Test
    public void testServerDisconnectionFromClientDisconnectClient() throws Client.ConnectionException, InterruptedException, IOException {
        // Check test client isConnected==false
        assert !clientLatch.isConnected();

        // Start server
        ServerSocket server = new ServerSocket(port);

        // Start test client and check isConnected==true
        clientLatch.connect();
        Assertions.assertTrue(latchCSE.onServerConnection.await(1, TimeUnit.SECONDS));

        // Stop server without bye msg
        clientLatch.disconnect();

        // Check isConnected==false
        Assertions.assertTrue(latchCSE.onServerDisconnection.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(latchCSE.onServerClientDisconnected.await(2, TimeUnit.SECONDS));
        Assertions.assertFalse(clientLatch.isConnected());

        // Stop server without bye msg
        server.close();
        assert server.isClosed();
    }

    @Test
    @Disabled
    public void testServerErrorOnReadStreamData() {
        throw new UnsupportedOperationException("Not testable");
    }

    @Test
    public void testServerErrorOnProcessRequest() throws IOException, Client.ConnectionException, InterruptedException {
        Client client = new DefaultClient(ID_CLIENT, LOCALHOST, port,
                new LogClientLocalEventsListener(),
                latchCSE,
                new LogClientMessagingEventsListener() {

                    @Override
                    public boolean onDataReceived(byte[] readData) throws Throwable {
                        super.onDataReceived(readData);
                        throw new Exception("(TEST) error on processing data");
                    }

                });

        // Start server
        ServerSocket server = new ServerSocket(port);

        // Start test client and check isConnected==true
        client.connect();
        Assertions.assertTrue(latchCSE.onServerConnection.await(1, TimeUnit.SECONDS));

        // Server send data
        Socket serverClient = server.accept();
        CommunicationBase.transmitData(serverClient.getOutputStream(), ("ExampleData").getBytes(PeerInfo.CHARSET));

        // Check client error event
        Assertions.assertTrue(latchCSE.onServerError.await(1, TimeUnit.SECONDS));

        // Close client connection and stop server
        client.disconnect();

        // Stop server without bye msg
        server.close();
        assert server.isClosed();
    }

}
