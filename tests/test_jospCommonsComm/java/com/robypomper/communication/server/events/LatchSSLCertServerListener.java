package com.robypomper.communication.server.events;

import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.standard.SSLCertServer;
import com.robypomper.communication.trustmanagers.AbsCustomTrustManager;

import java.util.concurrent.CountDownLatch;


public class LatchSSLCertServerListener implements SSLCertServer.SSLCertServerListener {

    // Internal vars

    public CountDownLatch onCertificateSend = new CountDownLatch(1);
    public CountDownLatch onCertificateStored = new CountDownLatch(1);


    // Events

    @Override
    public void onCertificateSend(ClientInfo client) {
        onCertificateSend.countDown();
    }

    @Override
    public void onCertificateStored(AbsCustomTrustManager certTrustManager, ClientInfo client) {
        onCertificateStored.countDown();
    }

}
