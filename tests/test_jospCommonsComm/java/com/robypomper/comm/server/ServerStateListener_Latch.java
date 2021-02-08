package com.robypomper.comm.server;

import java.util.concurrent.CountDownLatch;

public class ServerStateListener_Latch implements ServerStateListener {

    public CountDownLatch onStartup = new CountDownLatch(1);
    public CountDownLatch onShutdown = new CountDownLatch(1);
    public CountDownLatch onFail = new CountDownLatch(1);

    @Override
    public void onStart(Server server) {
        onStartup.countDown();
    }

    @Override
    public void onStop(Server server) {
        onShutdown.countDown();
    }

    @Override
    public void onFail(Server server, String failMsg, Throwable exception) {
        onFail.countDown();
    }

}
