package com.robypomper.communication.client.events;

import com.robypomper.communication.client.standard.SSLCertClient;
import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.trustmanagers.AbsCustomTrustManager;

import java.util.concurrent.CountDownLatch;


public class LatchSSLCertClientListener implements SSLCertClient.SSLCertClientListener {

    // Internal vars

    public CountDownLatch onCertificateSend = new CountDownLatch(1);
    public CountDownLatch onCertificateStored = new CountDownLatch(1);


    // Events

    @Override
    public void onCertificateSend() {
        onCertificateSend.countDown();
    }

    @Override
    public void onCertificateStored(AbsCustomTrustManager certTrustManager) {
        onCertificateStored.countDown();
    }

}
