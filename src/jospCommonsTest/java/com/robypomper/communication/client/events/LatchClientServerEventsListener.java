package com.robypomper.communication.client.events;

import java.util.concurrent.CountDownLatch;


public class LatchClientServerEventsListener extends LogClientServerEventsListener {

    // Internal vars

    public CountDownLatch onServerConnection = new CountDownLatch(1);
    public CountDownLatch onServerDisconnection = new CountDownLatch(1);
    public CountDownLatch onServerClientDisconnected = new CountDownLatch(1);
    public CountDownLatch onServerGoodbye = new CountDownLatch(1);
    public CountDownLatch onServerTerminated = new CountDownLatch(1);
    public CountDownLatch onServerError = new CountDownLatch(1);


    // Client connection events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerConnection() {
        super.onServerConnection();
        onServerConnection.countDown();
    }


    // Server disconnection events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerDisconnection() {
        super.onServerDisconnection();
        onServerDisconnection.countDown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerClientDisconnected() {
        super.onServerClientDisconnected();
        onServerClientDisconnected.countDown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerGoodbye() {
        super.onServerGoodbye();
        onServerGoodbye.countDown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerTerminated() {
        super.onServerTerminated();
        onServerTerminated.countDown();
    }


    // Server errors events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerError(Throwable e) {
        super.onServerError(e);
        onServerError.countDown();
    }

}
