package com.robypomper.communication.client.events;

import java.util.concurrent.CountDownLatch;

public class LatchClientMessagingEventsListener extends LogClientMessagingEventsListener {

    // Internal vars

    public CountDownLatch onDataSendBytes = new CountDownLatch(1);
    public CountDownLatch onDataSendString = new CountDownLatch(1);
    public CountDownLatch onDataReceivedBytes = new CountDownLatch(1);
    public CountDownLatch onDataReceivedString = new CountDownLatch(1);


    // Data send events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDataSend(byte[] writtenData) {
        super.onDataSend(writtenData);
        onDataSendBytes.countDown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDataSend(String writtenData) {
        super.onDataSend(writtenData);
        onDataSendString.countDown();
    }


    // Data received events

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onDataReceived(byte[] readData) throws Throwable {
        boolean res = super.onDataReceived(readData);
        onDataReceivedBytes.countDown();
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onDataReceived(String readData) throws Throwable {
        boolean res = super.onDataReceived(readData);
        onDataReceivedString.countDown();
        return res;
    }

}
