package com.robypomper.comm.peer;

import java.util.concurrent.CountDownLatch;

public class PeerDataListener_Latch implements PeerDataListener {

    public CountDownLatch onDataRx = new CountDownLatch(1);
    public CountDownLatch onDataTx = new CountDownLatch(1);

    @Override
    public void onDataRx(Peer peer, byte[] data) {
        onDataRx.countDown();
    }

    @Override
    public void onDataTx(Peer peer, byte[] data) {
        onDataTx.countDown();
    }

}
