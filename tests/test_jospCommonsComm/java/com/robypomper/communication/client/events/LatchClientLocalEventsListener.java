package com.robypomper.communication.client.events;

import java.util.concurrent.CountDownLatch;


public class LatchClientLocalEventsListener extends LogClientLocalEventsListener {

    // Internal vars

    public CountDownLatch onConnected = new CountDownLatch(1);
    public CountDownLatch onDisconnected = new CountDownLatch(1);
    public CountDownLatch onDisconnectionError = new CountDownLatch(1);


    // Client connect events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConnected() {
        super.onConnected();
        onConnected.countDown();
    }


    // Client disconnect events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisconnected() {
        super.onDisconnected();
        onDisconnected.countDown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisconnectionError(Exception e) {
        super.onDisconnectionError(e);
        onDisconnectionError.countDown();
    }

}
