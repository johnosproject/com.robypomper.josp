package com.robypomper.comm.peer;

import java.util.concurrent.CountDownLatch;

public class PeerConnectionListener_Latch implements PeerConnectionListener {

    public CountDownLatch onConnecting = new CountDownLatch(1);
    public CountDownLatch onWaiting = new CountDownLatch(1);
    public CountDownLatch onConnect = new CountDownLatch(1);
    public CountDownLatch onDisconnecting = new CountDownLatch(1);
    public CountDownLatch onDisconnect = new CountDownLatch(1);
    public CountDownLatch onFail = new CountDownLatch(1);

    @Override
    public void onConnecting(Peer peer) {
        onConnecting.countDown();
    }

    @Override
    public void onWaiting(Peer peer) {
        onWaiting.countDown();
    }

    @Override
    public void onConnect(Peer peer) {
        onConnect.countDown();
    }

    @Override
    public void onDisconnecting(Peer peer) {
        onDisconnecting.countDown();
    }

    @Override
    public void onDisconnect(Peer peer) {
        onDisconnect.countDown();
    }

    @Override
    public void onFail(Peer peer, String failMsg, Throwable exception) {
        onFail.countDown();
    }

}
