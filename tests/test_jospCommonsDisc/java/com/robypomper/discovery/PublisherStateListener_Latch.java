package com.robypomper.discovery;

import java.util.concurrent.CountDownLatch;

public class PublisherStateListener_Latch implements PublisherStateListener {

    public CountDownLatch onStart = new CountDownLatch(1);
    public CountDownLatch onStop = new CountDownLatch(1);
    public CountDownLatch onFail = new CountDownLatch(1);

    @Override
    public void onStart(Publisher publisher) {
        onStart.countDown();
    }

    @Override
    public void onStop(Publisher publisher) {
        onStop.countDown();
    }

    @Override
    public void onFail(Publisher publisher, String failMsg, Throwable exception) {
        onFail.countDown();
    }

}
