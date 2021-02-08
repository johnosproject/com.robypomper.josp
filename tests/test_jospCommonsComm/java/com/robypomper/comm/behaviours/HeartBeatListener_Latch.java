package com.robypomper.comm.behaviours;

import com.robypomper.comm.peer.Peer;

import java.util.concurrent.CountDownLatch;

public class HeartBeatListener_Latch implements HeartBeatListener {


    public CountDownLatch onSend = new CountDownLatch(1);
    public CountDownLatch onSuccess = new CountDownLatch(1);
    public CountDownLatch onFail = new CountDownLatch(1);

    @Override
    public void onSend(Peer peer, HeartBeatConfigs hb) {
        onSend.countDown();
    }

    @Override
    public void onSuccess(Peer peer, HeartBeatConfigs hb) {
        onSuccess.countDown();
    }

    @Override
    public void onFail(Peer peer, HeartBeatConfigs hb) {
        onFail.countDown();
    }
}
