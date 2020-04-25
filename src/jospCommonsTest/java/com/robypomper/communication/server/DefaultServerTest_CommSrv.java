package com.robypomper.communication.server;

import com.robypomper.communication.server.standard.LogServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class DefaultServerTest_CommSrv extends DefaultServerTest_Base {

    // isRunning, start and stop

    @Test
    public void testIsRunning() throws Server.ListeningException, InterruptedException {
        // Check test server isRunning==false
        assert !serverLatch.isRunning();

        // Start test server and check isRunning==true
        serverLatch.start();
        Assertions.assertTrue(latchSLE.onStarted.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(serverLatch.isRunning());

        // Stop test server and check isRunning==false
        serverLatch.stop();
        Assertions.assertTrue(latchSLE.onStopped.await(1, TimeUnit.SECONDS));
        Assertions.assertFalse(serverLatch.isRunning());
    }

    @Test
    public void testStart() throws Server.ListeningException, InterruptedException {
        // Start test server and check that's running
        serverLatch.start();
        Assertions.assertTrue(latchSLE.onStarted.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(serverLatch.isRunning());
    }

    @Test
    @Deprecated // No more exception for server already started, only warn logging message
    public void testStartAlready_FAIL() throws Server.ListeningException {
        // Start test server
        startServer(serverLog);

        // Start again test server and catch exception
        assert serverLog.isRunning();
        //Assertions.assertThrows(Server.ListeningException.class, serverLog::start);
        serverLog.start();
        assert serverLog.isRunning();
    }

    @Test
    public void testStartBounded_FAIL() throws Server.ListeningException {
        // Start test server
        startServer(serverLog);

        // Init 2nd test server with same port of test server
        Server secondServer = new LogServer(ID_SERVER + "2nd", PORT);

        // Start 2nd test server and catch exception
        assert !secondServer.isRunning();
        Assertions.assertThrows(Server.ListeningException.class, secondServer::start);
        assert !secondServer.isRunning();
    }

    @Test
    public void testStop() throws Server.ListeningException, InterruptedException {
        // Start test server
        startServer(serverLatch);

        // Stop test server and check that's NOT running
        serverLatch.stop();
        Assertions.assertTrue(latchSLE.onStopped.await(1, TimeUnit.SECONDS));
        Assertions.assertFalse(serverLatch.isRunning());
    }

    @Test
    @Deprecated // No more exception for server already started, only warn logging message
    public void testStopAlready_FAIL() {
        // DON'T Start test server
        // startServer();

        // Stop test server and catch exception
        assert !serverLog.isRunning();
        //Assertions.assertThrows(Server.ListeningException.class, serverLog::stop);
        serverLog.stop();
        assert !serverLog.isRunning();
    }

    @Test
    @Deprecated // No more exception for server already started, only warn logging message
    public void testStopAlreadyAfterStart_FAIL() throws Server.ListeningException, InterruptedException {
        // Start test server
        startServer(serverLatch);
        Assertions.assertTrue(latchSLE.onStarted.await(1, TimeUnit.SECONDS));
        stopServer(serverLatch);
        Assertions.assertTrue(latchSLE.onStopped.await(1, TimeUnit.SECONDS));

        // Stop test server and catch exception
        assert !serverLatch.isRunning();
        //Assertions.assertThrows(Server.ListeningException.class, serverLog::stop);
        serverLatch.stop();
        assert !serverLatch.isRunning();
    }

    @Test
    @Disabled
    public void testStopErrorOnSocketClose() {
        throw new UnsupportedOperationException("Not testable");
    }

    @Test
    @Disabled
    public void testStopErrorOnThreadInterrupt() {
        throw new UnsupportedOperationException("Not testable");
    }

    @Test
    @Disabled
    public void testServerErrorOnAcceptClientConnections() {
        throw new UnsupportedOperationException("Not testable");
    }

}