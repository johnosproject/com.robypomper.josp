package com.robypomper.discovery;

import java.util.concurrent.CountDownLatch;


public class TestDiscoverListener implements DiscoverListener {

    // Internal vars

    private final String serviceName;
    public CountDownLatch onServiceDiscovered = new CountDownLatch(1);
    public CountDownLatch onServiceLost = new CountDownLatch(1);

    public TestDiscoverListener(String serviceName) {
        this.serviceName = serviceName;
    }


    // Events

    @Override
    public void onServiceDiscovered(DiscoveryService discSrv) {
        if (discSrv.name.equalsIgnoreCase(serviceName)) {
            System.out.println(String.format("%s\t\tTEST onServiceDiscovered(%s)", "????????", discSrv.name));
            onServiceDiscovered.countDown();
        }
    }

    @Override
    public void onServiceLost(DiscoveryService lostSrv) {
        if (lostSrv.name.equalsIgnoreCase(serviceName)) {
            System.out.println(String.format("%s\t\tTEST onServiceLost(%s)", "????????", lostSrv.name));
            onServiceLost.countDown();
        }
    }

}
