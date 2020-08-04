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

import com.robypomper.communication.client.events.LatchClientLocalEventsListener;
import com.robypomper.communication.client.events.LatchClientMessagingEventsListener;
import com.robypomper.communication.client.events.LatchClientServerEventsListener;
import com.robypomper.communication.client.standard.LogClient;
import com.robypomper.log.Mrk_Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.net.InetAddress;

public class DefaultClientTest_Base {

    // Class constants

    final static String ID_CLIENT = "TestClient";
    final static InetAddress LOCALHOST = InetAddress.getLoopbackAddress();


    // Internal vars

    protected static Logger log = LogManager.getLogger();
    protected static int port = 1234;
    protected Client clientLog = null;
    protected Client clientLatch = null;
    protected LatchClientLocalEventsListener latchCLE = null;
    protected LatchClientServerEventsListener latchCSE = null;
    protected LatchClientMessagingEventsListener latchCME = null;


    // Test configurations

    @BeforeEach
    public void setUp() {
        log.debug(Mrk_Test.TEST_SPACER, "########## ########## ########## ########## ##########");
        log.debug(Mrk_Test.TEST_METHODS, "setUp");

        // Init test client
        clientLog = new LogClient(ID_CLIENT, LOCALHOST, ++port);

        latchCLE = new LatchClientLocalEventsListener();
        latchCSE = new LatchClientServerEventsListener();
        latchCME = new LatchClientMessagingEventsListener();
        clientLatch = new DefaultClient(ID_CLIENT, LOCALHOST, port, latchCLE, latchCSE, latchCME);

        log.debug(Mrk_Test.TEST_METHODS, "test");
    }

    @AfterEach
    public void tearDown() {
        log.debug(Mrk_Test.TEST_METHODS, "tearDown");

        // If still running, stop test server
        if (clientLog.isConnected())
            clientLog.disconnect();
        if (clientLatch.isConnected())
            clientLatch.disconnect();
    }

}
