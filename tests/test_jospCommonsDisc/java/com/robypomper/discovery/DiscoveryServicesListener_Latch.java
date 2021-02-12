package com.robypomper.discovery;

import java.util.concurrent.CountDownLatch;

public class DiscoveryServicesListener_Latch implements DiscoveryServicesListener {

    public CountDownLatch onServiceDiscovered = new CountDownLatch(1);
    public CountDownLatch onServiceLost = new CountDownLatch(1);

    @Override
    public void onServiceDiscovered(DiscoveryService discSrv) {
        onServiceDiscovered.countDown();
    }

    @Override
    public void onServiceLost(DiscoveryService lostSrv) {
        onServiceLost.countDown();
    }

}
