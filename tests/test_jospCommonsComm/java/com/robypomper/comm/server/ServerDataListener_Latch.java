package com.robypomper.comm.server;

import com.robypomper.comm.peer.Peer;

import java.util.concurrent.CountDownLatch;

public class ServerDataListener_Latch implements ServerDataListener {

    public CountDownLatch onDataRx = new CountDownLatch(1);
    public CountDownLatch onDataTx = new CountDownLatch(1);

    @Override
    public void onDataRx(Server server, Peer peer, byte[] data) {
        onDataRx.countDown();
    }

    @Override
    public void onDataTx(Server server, Peer peer, byte[] data) {
        onDataTx.countDown();
    }

}
