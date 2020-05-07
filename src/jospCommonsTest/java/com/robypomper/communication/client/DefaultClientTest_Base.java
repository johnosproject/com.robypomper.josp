package com.robypomper.communication.client;

import com.robypomper.communication.client.events.LatchClientLocalEventsListener;
import com.robypomper.communication.client.events.LatchClientMessagingEventsListener;
import com.robypomper.communication.client.events.LatchClientServerEventsListener;
import com.robypomper.communication.client.standard.LogClient;
import com.robypomper.log.Markers;
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
        log.debug(Markers.TEST_SPACER, "########## ########## ########## ########## ##########");
        log.debug(Markers.TEST_METHODS, "setUp");

        // Init test client
        clientLog = new LogClient(ID_CLIENT, LOCALHOST, ++port);

        latchCLE = new LatchClientLocalEventsListener();
        latchCSE = new LatchClientServerEventsListener();
        latchCME = new LatchClientMessagingEventsListener();
        clientLatch = new DefaultClient(ID_CLIENT, LOCALHOST, port, latchCLE, latchCSE, latchCME);

        log.debug(Markers.TEST_METHODS, "test");
    }

    @AfterEach
    public void tearDown() {
        log.debug(Markers.TEST_METHODS, "tearDown");

        // If still running, stop test server
        if (clientLog.isConnected())
            clientLog.disconnect();
        if (clientLatch.isConnected())
            clientLatch.disconnect();
    }

}
