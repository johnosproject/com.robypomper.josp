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

import com.robypomper.communication.client.events.LogClientLocalEventsListener;
import com.robypomper.communication.client.events.LogClientMessagingEventsListener;
import com.robypomper.communication.peer.PeerInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DefaultClientTest_Msg extends DefaultClientTest_Base {

    // Server messaging

    @Test
    public void testReceivedData() throws Client.ConnectionException, IOException, InterruptedException {
        byte[] originalDataByte = "bytesData".getBytes(PeerInfo.CHARSET);
        String originalDataString = "bytesString";

        CountDownLatch onDataReceivedBytes = new CountDownLatch(1);
        CountDownLatch onDataReceivedString = new CountDownLatch(1);

        Client client = new DefaultClient(ID_CLIENT, LOCALHOST, port,
                new LogClientLocalEventsListener(),
                latchCSE,
                new LogClientMessagingEventsListener() {

                    @Override
                    public boolean onDataReceived(byte[] readData) throws Throwable {
                        super.onDataReceived(readData);
                        if (Arrays.equals(readData, originalDataByte)) {
                            onDataReceivedBytes.countDown();
                            return true;
                        }

                        return false;
                    }

                    @Override
                    public boolean onDataReceived(String readData) throws Throwable {
                        super.onDataReceived(readData);
                        if (readData.equals(originalDataString)) {
                            onDataReceivedString.countDown();
                            return true;
                        }

                        return false;
                    }

                });


        // Start server
        ServerSocket server = new ServerSocket(port);

        // Connect client
        client.connect();

        // Server send bytes data
        Socket serverClient = server.accept();
        serverClient.getOutputStream().write(originalDataByte);
        Assertions.assertTrue(onDataReceivedBytes.await(1, TimeUnit.SECONDS));

        // Server send string data
        serverClient.getOutputStream().write(originalDataString.getBytes(PeerInfo.CHARSET));
        Assertions.assertTrue(onDataReceivedString.await(1, TimeUnit.SECONDS));

        // Close client connection
        client.disconnect();

        // Stop server
        server.close();
        assert server.isClosed();
    }

    @Test
    public void testSendData() throws Client.ConnectionException, IOException, InterruptedException, Client.ServerNotConnectedException {
        CountDownLatch onDataSendBytes = new CountDownLatch(1);
        CountDownLatch onDataSendString = new CountDownLatch(1);

        Client client = new DefaultClient(ID_CLIENT, LOCALHOST, port,
                new LogClientLocalEventsListener(),
                latchCSE,
                new LogClientMessagingEventsListener() {

                    @Override
                    public void onDataSend(byte[] writtenData) {
                        super.onDataSend(writtenData);
                        onDataSendBytes.countDown();
                    }

                    @Override
                    public void onDataSend(String writtenData) {
                        super.onDataSend(writtenData);
                        onDataSendString.countDown();
                    }

                });

        // Start server
        ServerSocket server = new ServerSocket(port);

        // Connect client
        client.connect();

        // Client send byte data
        client.sendData("ExampleData");
        Assertions.assertTrue(onDataSendBytes.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(onDataSendString.await(1, TimeUnit.SECONDS));

        // Close client connection
        client.disconnect();

        // Stop server
        server.close();
        assert server.isClosed();
    }

}