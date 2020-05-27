package com.robypomper.communication.server;

import com.robypomper.communication.server.events.LatchServerClientEventsListener;
import com.robypomper.communication.server.events.LatchServerLocalEventsListener;
import com.robypomper.communication.server.events.LatchServerMessagingEventsListener;
import com.robypomper.communication.server.standard.EchoServer;
import com.robypomper.communication.server.standard.LogServer;
import com.robypomper.log.Mrk_Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class DefaultServerTest_Base {

    // Class constants

    final static String ID_SERVER = "TestServer";
    final static InetAddress LOCALHOST = InetAddress.getLoopbackAddress();


    // Internal vars

    protected static Logger log = LogManager.getLogger();
    protected static int port = 1234;
    protected Server serverLog = null;
    protected Server serverEcho = null;
    protected Server serverLatch = null;
    protected LatchServerLocalEventsListener latchSLE = null;
    protected LatchServerClientEventsListener latchSCE = null;
    protected LatchServerMessagingEventsListener latchSME = null;


    // Test configurations

    @BeforeEach
    public void setUp() {
        log.debug(Mrk_Test.TEST_SPACER, "########## ########## ########## ########## ##########");
        log.debug(Mrk_Test.TEST_METHODS, "setUp");

        // Init test server
        serverLog = new LogServer(ID_SERVER, ++port);
        serverEcho = new EchoServer(ID_SERVER, port);

        latchSLE = new LatchServerLocalEventsListener();
        latchSCE = new LatchServerClientEventsListener();
        latchSME = new LatchServerMessagingEventsListener();
        serverLatch = new DefaultServer(ID_SERVER, port, latchSLE, latchSCE, latchSME);

        log.debug(Mrk_Test.TEST_METHODS, "test");
    }

    @AfterEach
    public void tearDown() {
        log.debug(Mrk_Test.TEST_METHODS, "tearDown");

        // If still running, stop test server
        if (serverLog.isRunning())
            serverLog.stop();
        if (serverEcho.isRunning())
            serverEcho.stop();
        if (serverLatch.isRunning())
            serverLatch.stop();
    }


    // Utils methods

    public static void startServer(Server server) throws Server.ListeningException {
        server.start();
        assert server.isRunning();
    }

    public static void stopServer(Server server) {
        server.stop();
        assert !server.isRunning();
    }

    public static void printClientInfoList(List<ClientInfo> clients) {
        log.debug(Mrk_Test.TEST_METHODS, "Client list:");
        for (ClientInfo c : clients)
            log.debug(Mrk_Test.TEST_METHODS, String.format("\t%s @ %s", c.getClientId(), c.getPeerFullAddress()));
    }

    @SuppressWarnings("SameParameterValue")
    public static Socket connectClient(InetAddress localhost, int port, CountDownLatch latchOnConnection) throws IOException, InterruptedException {
        Socket s = new Socket(localhost, port);
        //noinspection RedundantIfStatement
        if (latchOnConnection != null)
            assert latchOnConnection.await(1, TimeUnit.SECONDS);
        return s;
    }

    @SuppressWarnings("SameParameterValue")
    public static SSLSocket connectClient(SSLContext sslCtx, InetAddress localhost, int port, CountDownLatch latchOnConnection) throws IOException, InterruptedException {
        SSLSocket s = (SSLSocket) sslCtx.getSocketFactory().createSocket(localhost, port);
        s.startHandshake();
        //noinspection RedundantIfStatement
        if (latchOnConnection != null)
            assert latchOnConnection.await(1, TimeUnit.SECONDS);
        return s;
    }

    public static void clientSend(Socket socket, byte[] data) throws IOException {
        socket.getOutputStream().write(data);
    }

}