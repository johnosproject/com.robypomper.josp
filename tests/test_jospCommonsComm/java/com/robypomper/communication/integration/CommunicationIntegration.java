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
import com.robypomper.communication.client.standard.LogClient;
import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.Server;
import com.robypomper.communication.server.standard.LogServer;
import com.robypomper.josp.states.StateException;
import com.robypomper.log.Mrk_Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class CommunicationIntegration {

    // Class constants

    final static String ID_SERVER = "TestServer";
    final static String ID_CLIENT = "TestClient";
    final static int PORT = 1234;
    final static String LOCALHOST = InetAddress.getLoopbackAddress().getHostAddress();

    // Internal vars

    protected static Logger log = LogManager.getLogger();
    protected Server serverLog = null;
    protected Client clientLog = null;


    // Test configurations

    @BeforeEach
    public void setUp() {
        log.debug(Mrk_Test.TEST_SPACER, "########## ########## ########## ########## ##########");
        log.debug(Mrk_Test.TEST_METHODS, "setUp");

        // Init test server
        serverLog = new LogServer(ID_SERVER, PORT);
        clientLog = new LogClient(ID_CLIENT, LOCALHOST, PORT);

        log.debug(Mrk_Test.TEST_METHODS, "test");
    }

    @AfterEach
    public void tearDown() throws StateException {
        log.debug(Mrk_Test.TEST_METHODS, "tearDown");

        // If still running, stop test server
        if (clientLog.isConnected())
            clientLog.disconnect();
        if (serverLog.isRunning())
            serverLog.stop();
    }


    @Test
    public void testClientDisconnection() throws Server.ListeningException, InterruptedException, StateException, Client.AAAException, IOException {
        int startThread = Thread.getAllStackTraces().keySet().size();
        int startClients = serverLog.getClients().size();

        System.out.println("\nSERVER START");
        serverLog.start();
        System.out.println("\nCLIENT CONNECTION");
        clientLog.connect();

        Thread.sleep(100);

        System.out.println("\nCLIENT DISCONNECTION");
        clientLog.disconnect();
        System.out.println("\nSERVER STOP");
        serverLog.stop();

        Assertions.assertEquals(startThread, Thread.getAllStackTraces().keySet().size());
        int connectedClients = 0;
        for (ClientInfo cl : serverLog.getClients())
            if (cl.isConnected())
                connectedClients++;
        Assertions.assertEquals(startClients, connectedClients);
    }


    @Test
    public void testServerStop() throws Server.ListeningException, InterruptedException, StateException, Client.AAAException, IOException {
        int startThread = countThreads();
        int startClients = serverLog.getClients().size();

        System.out.println("\nSERVER START");
        serverLog.start();
        System.out.println("\nCLIENT CONNECT");
        clientLog.connect();

        Thread.sleep(100);
        System.out.println("\n");


        System.out.println("\nSERVER STOP");
        serverLog.stop();
        //System.out.println("\nCLIENT DISCONNECT");
        //clientLog.disconnect();

        Thread.sleep(100);
        System.out.println("\n");
        Assertions.assertEquals(startThread, countThreads());
        Assertions.assertEquals(startClients, countConnectedClients(serverLog));
    }

    private void printThreads() {
        for (Thread t : Thread.getAllStackTraces().keySet())
            System.out.println("- " + t.getName());
    }

    private int countThreads() {
        int count = 0;
        for (Thread t : Thread.getAllStackTraces().keySet())
            if (!TH_JVM_NAMES.contains(t.getName()))
                count++;
        return count;
    }

    private int countConnectedClients(Server server) {
        int count = 0;
        for (ClientInfo cl : server.getClients())
            if (cl.isConnected())
                count++;
        return count;
    }


    private static final List<String> TH_JVM_NAMES = new ArrayList<String>() {
        {
            add("Attach Listener");
            add("Attach Listener");
            add("Finalizer");
            add("Monitor Ctrl-Break");
            add("Reference Handler");
            add("Signal Dispatcher");
        }
    };

}
