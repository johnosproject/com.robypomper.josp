package com.robypomper.discovery;

import java.util.concurrent.CountDownLatch;

public class DiscoverStateListener_Latch implements DiscoverStateListener {

    public CountDownLatch onStart = new CountDownLatch(1);
    public CountDownLatch onStop = new CountDownLatch(1);
    public CountDownLatch onFail = new CountDownLatch(1);

    @Override
    public void onStart(Discover discover) {
        onStart.countDown();
    }

    @Override
    public void onStop(Discover discover) {
        onStop.countDown();
    }

    @Override
    public void onFail(Discover discover, String failMsg, Throwable exception) {
        onFail.countDown();
    }

}
