package com.robypomper.communication.server.events;

import java.util.concurrent.CountDownLatch;


public class LatchServerLocalEventsListener extends LogServerLocalEventsListener {

    // Internal vars

    public CountDownLatch onStarted = new CountDownLatch(1);
    public CountDownLatch onStopped = new CountDownLatch(1);
    public CountDownLatch onStopError = new CountDownLatch(1);
    public CountDownLatch onServerError = new CountDownLatch(1);


    // Server start events

    @Override
    public void onStarted() {
        super.onStarted();
        onStarted.countDown();
    }


    // Server stop events

    @Override
    public void onStopped() {
        super.onStopped();
        onStopped.countDown();
    }

    @Override
    public void onStopError(Exception e) {
        super.onStopError(e);
        onStopError.countDown();
    }


    // Server error events

    @Override
    public void onServerError(Exception e) {
        super.onServerError(e);
        onServerError.countDown();
    }

}
