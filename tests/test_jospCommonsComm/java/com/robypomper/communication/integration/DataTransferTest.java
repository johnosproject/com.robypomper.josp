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

import com.robypomper.communication.client.Client;
import com.robypomper.communication.client.DefaultClient;
import com.robypomper.communication.client.standard.LogClient;
import com.robypomper.communication.peer.PeerInfo;
import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.DefaultServer;
import com.robypomper.communication.server.Server;
import com.robypomper.communication.server.events.LogServerClientEventsListener;
import com.robypomper.communication.server.events.LogServerLocalEventsListener;
import com.robypomper.communication.server.events.LogServerMessagingEventsListener;
import com.robypomper.java.JavaRandomStrings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.robypomper.communication.server.DefaultServerTest_Base.stopServer;

public class DataTransferTest {

    // Class constants

    final static String ID_SERVER = "TestServer";
    final static String ID_CLIENT = "TestClient";
    final static int PORT = 8234;
    final static InetAddress LOCALHOST = InetAddress.getLoopbackAddress();


    // Server messaging

    CountDownLatch onDataReceivedBytes = new CountDownLatch(1);
    int readBytes = -1;

    @Test
    public void testSingleMessages() throws Server.ListeningException, InterruptedException, Client.ConnectionException, Client.ServerNotConnectedException {
        byte[] originalDataByte = "bytesData".getBytes(PeerInfo.CHARSET);


        Server server = new DefaultServer(ID_SERVER, PORT,
                new LogServerLocalEventsListener(),
                new LogServerClientEventsListener(),
                new LogServerMessagingEventsListener() {

                    @Override
                    public boolean onDataReceived(ClientInfo client, byte[] readData) throws Throwable {
                        super.onDataReceived(client, readData);
                        readBytes = readData.length;
                        onDataReceivedBytes.countDown();
                        return true;
                    }

                    @Override
                    public boolean onDataReceived(ClientInfo client, String readData) throws Throwable {
                        return false;
                    }

                });

        // Start server and connect client
        server.start();

        DefaultClient clientLog = new LogClient(ID_CLIENT, LOCALHOST, PORT);
        clientLog.connect();

        // Client send short data
        clientLog.sendData(getBytes(1024, 'A'));
        Assertions.assertTrue(onDataReceivedBytes.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals(readBytes, 1024);

        // Client send short data
        onDataReceivedBytes = new CountDownLatch(1);
        clientLog.sendData(getBytes(10240, 'B'));
        Assertions.assertTrue(onDataReceivedBytes.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals(readBytes, 10240);

        // Client send short data
        onDataReceivedBytes = new CountDownLatch(1);
        clientLog.sendData(getBytes(60000, 'C'));
        Assertions.assertTrue(onDataReceivedBytes.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals(readBytes, 60000);

        // Close client connection and stop server
        clientLog.disconnect();
        stopServer(server);
    }


    CountDownLatch onDataReceivedBytes_2 = new CountDownLatch(1);
    CountDownLatch onDataReceivedBytes_3 = new CountDownLatch(1);
    CountDownLatch onDataReceivedBytes_4 = new CountDownLatch(1);
    int readBytes_2 = -1;
    int readBytes_3 = -1;
    int readBytes_4 = -1;

    @Test
    public void testMultipleMessages() throws Server.ListeningException, InterruptedException, Client.ConnectionException, Client.ServerNotConnectedException {
        byte[] originalDataByte = "bytesData".getBytes(PeerInfo.CHARSET);


        Server server = new DefaultServer(ID_SERVER, PORT,
                new LogServerLocalEventsListener(),
                new LogServerClientEventsListener(),
                new LogServerMessagingEventsListener() {

                    @Override
                    public boolean onDataReceived(ClientInfo client, byte[] readData) throws Throwable {
                        super.onDataReceived(client, readData);
                        if (onDataReceivedBytes.getCount() > 0) {
                            readBytes = readData.length;
                            onDataReceivedBytes.countDown();
                        } else if (onDataReceivedBytes_2.getCount() > 0) {
                            readBytes_2 = readData.length;
                            onDataReceivedBytes_2.countDown();
                        } else if (onDataReceivedBytes_3.getCount() > 0) {
                            readBytes_3 = readData.length;
                            onDataReceivedBytes_3.countDown();
                        }
                        return true;
                    }

                    @Override
                    public boolean onDataReceived(ClientInfo client, String readData) throws Throwable {
                        return false;
                    }

                });

        // Start server and connect client
        server.start();

        DefaultClient clientLog = new LogClient(ID_CLIENT, LOCALHOST, PORT);
        clientLog.connect();

        // Client send short data
        clientLog.sendData(getBytes(1024, 'A'));
        clientLog.sendData(getBytes(1024, 'B'));
        clientLog.sendData(getBytes(1024, 'C'));
        Assertions.assertTrue(onDataReceivedBytes.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals(readBytes, 1024);
        Assertions.assertTrue(onDataReceivedBytes_2.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals(readBytes_2, 1024);
        Assertions.assertTrue(onDataReceivedBytes_3.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals(readBytes_3, 1024);

        // Client send short data
        onDataReceivedBytes = new CountDownLatch(1);
        onDataReceivedBytes_2 = new CountDownLatch(1);
        onDataReceivedBytes_3 = new CountDownLatch(1);
        clientLog.sendData(getBytes(10240, 'X'));
        clientLog.sendData(getBytes(10240, 'Y'));
        clientLog.sendData(getBytes(10240, 'Z'));
        Assertions.assertTrue(onDataReceivedBytes.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals(readBytes, 10240);
        Assertions.assertTrue(onDataReceivedBytes_2.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals(readBytes_2, 10240);
        Assertions.assertTrue(onDataReceivedBytes_3.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals(readBytes_3, 10240);

        // Client send short data
        onDataReceivedBytes = new CountDownLatch(1);
        onDataReceivedBytes_2 = new CountDownLatch(1);
        onDataReceivedBytes_3 = new CountDownLatch(1);
        clientLog.sendData(getBytes(60000, '1'));
        clientLog.sendData(getBytes(60000, '2'));
        clientLog.sendData(getBytes(60000, '3'));
        Assertions.assertTrue(onDataReceivedBytes.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals(readBytes, 60000);
        Assertions.assertTrue(onDataReceivedBytes_2.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals(readBytes_2, 60000);
        Assertions.assertTrue(onDataReceivedBytes_3.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals(readBytes_3, 60000);

        // Close client connection and stop server
        clientLog.disconnect();
        stopServer(server);
    }

    Thread t1;
    Thread t2;
    Thread t3;
    Thread t4;

    @Test
    public void testMultipleThreads() throws Server.ListeningException, InterruptedException, Client.ConnectionException, Client.ServerNotConnectedException {
        byte[] originalDataByte = "bytesData".getBytes(PeerInfo.CHARSET);


        Server server = new DefaultServer(ID_SERVER, PORT,
                new LogServerLocalEventsListener(),
                new LogServerClientEventsListener(),
                new LogServerMessagingEventsListener() {

                    @Override
                    public boolean onDataReceived(ClientInfo client, byte[] readData) throws Throwable {
                        super.onDataReceived(client, readData);
                        if (onDataReceivedBytes.getCount() > 0) {
                            readBytes = readData.length;
                            onDataReceivedBytes.countDown();
                        } else if (onDataReceivedBytes_2.getCount() > 0) {
                            readBytes_2 = readData.length;
                            onDataReceivedBytes_2.countDown();
                        } else if (onDataReceivedBytes_3.getCount() > 0) {
                            readBytes_3 = readData.length;
                            onDataReceivedBytes_3.countDown();
                        } else if (onDataReceivedBytes_4.getCount() > 0) {
                            readBytes_4 = readData.length;
                            onDataReceivedBytes_4.countDown();
                        }
                        return true;
                    }

                    @Override
                    public boolean onDataReceived(ClientInfo client, String readData) throws Throwable {
                        return false;
                    }

                });

        // Start server and connect client
        server.start();

        DefaultClient clientLog = new LogClient(ID_CLIENT, LOCALHOST, PORT);
        clientLog.connect();

        // Client send short data
        t1 = new Thread((Runnable) () -> {
            try {
                clientLog.sendData(getBytes(1024, 'A'));
            } catch (Client.ServerNotConnectedException ignore) {
            }
        });
        t1.start();
        t2 = new Thread((Runnable) () -> {
            try {
                clientLog.sendData(getBytes(1024, 'B'));
            } catch (Client.ServerNotConnectedException ignore) {
            }
        });
        t2.start();
        t3 = new Thread((Runnable) () -> {
            try {
                clientLog.sendData(getBytes(1024, 'C'));
            } catch (Client.ServerNotConnectedException ignore) {
            }
        });
        t3.start();
        t1.join();
        t2.join();
        t3.join();
        Assertions.assertTrue(onDataReceivedBytes.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals(1024, readBytes);
        Assertions.assertTrue(onDataReceivedBytes_2.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals(1024, readBytes_2);
        Assertions.assertTrue(onDataReceivedBytes_3.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals(1024, readBytes_3);

        // Client send short data
        onDataReceivedBytes = new CountDownLatch(1);
        onDataReceivedBytes_2 = new CountDownLatch(1);
        onDataReceivedBytes_3 = new CountDownLatch(1);
        readBytes = -1;
        readBytes_2 = -1;
        readBytes_3 = -1;
        readBytes_4 = -1;
        t1 = new Thread((Runnable) () -> {
            try {
                clientLog.sendData(getBytes(10240, 'X'));
            } catch (Client.ServerNotConnectedException ignore) {
            }
        });
        t1.start();
        t2 = new Thread((Runnable) () -> {
            try {
                clientLog.sendData(getBytes(10240, 'Y'));
            } catch (Client.ServerNotConnectedException ignore) {
            }
        });
        t2.start();
        t3 = new Thread((Runnable) () -> {
            try {
                clientLog.sendData(getBytes(10240, 'Z'));
            } catch (Client.ServerNotConnectedException ignore) {
            }
        });
        t3.start();
        t1.join();
        t2.join();
        t3.join();
        Assertions.assertTrue(onDataReceivedBytes.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals(10240, readBytes);
        Assertions.assertTrue(onDataReceivedBytes_2.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals(10240, readBytes_2);
        Assertions.assertTrue(onDataReceivedBytes_3.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals(10240, readBytes_3);

        // Client send short data
        onDataReceivedBytes = new CountDownLatch(1);
        onDataReceivedBytes_2 = new CountDownLatch(1);
        onDataReceivedBytes_3 = new CountDownLatch(1);
        onDataReceivedBytes_4 = new CountDownLatch(1);
        readBytes = -1;
        readBytes_2 = -1;
        readBytes_3 = -1;
        readBytes_4 = -1;
        t1 = new Thread((Runnable) () -> {
            try {
                clientLog.sendData(getBytes(60000, '1'));
            } catch (Client.ServerNotConnectedException ignore) {
            }
        });
        t1.start();
        t2 = new Thread((Runnable) () -> {
            try {
                clientLog.sendData(getBytes(60000, '2'));
            } catch (Client.ServerNotConnectedException ignore) {
            }
        });
        t2.start();
        t3 = new Thread((Runnable) () -> {
            try {
                clientLog.sendData(getBytes(60000, '3'));
            } catch (Client.ServerNotConnectedException ignore) {
            }
        });
        t3.start();
        t4 = new Thread((Runnable) () -> {
            try {
                clientLog.sendData(getBytes(60000, '4'));
            } catch (Client.ServerNotConnectedException ignore) {
            }
        });
        t4.start();
        t1.join();
        t2.join();
        t3.join();
        t4.join();
        Thread.sleep(1000);
        onDataReceivedBytes.await(1, TimeUnit.SECONDS);
        Assertions.assertEquals(60000, readBytes);
        onDataReceivedBytes_2.await(1, TimeUnit.SECONDS);
        Assertions.assertEquals(60000, readBytes_2);
        onDataReceivedBytes_3.await(1, TimeUnit.SECONDS);
        Assertions.assertEquals(60000, readBytes_3);
        onDataReceivedBytes_4.await(1, TimeUnit.SECONDS);
        Assertions.assertEquals(60000, readBytes_4);

        // Close client connection and stop server
        clientLog.disconnect();
        stopServer(server);
    }

    private byte[] getBytes(int count, char c) {
        return JavaRandomStrings.repeatedString(count, c).getBytes();
    }

}