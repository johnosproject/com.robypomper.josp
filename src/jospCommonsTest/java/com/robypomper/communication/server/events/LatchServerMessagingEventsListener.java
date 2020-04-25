package com.robypomper.communication.server.events;

import com.robypomper.communication.server.ClientInfo;

import java.util.concurrent.CountDownLatch;


public class LatchServerMessagingEventsListener extends LogServerMessagingEventsListener {

    // Internal vars

    public CountDownLatch onDataSendBytes = new CountDownLatch(1);
    public CountDownLatch onDataSendString = new CountDownLatch(1);
    public CountDownLatch onDataReceivedBytes = new CountDownLatch(1);
    public CountDownLatch onDataReceivedString = new CountDownLatch(1);


    // Data send events

    @Override
    public void onDataSend(ClientInfo client, byte[] writtenData) {
        super.onDataSend(client, writtenData);
        onDataSendBytes.countDown();
    }

    @Override
    public void onDataSend(ClientInfo client, String writtenData) {
        super.onDataSend(client, writtenData);
        onDataSendString.countDown();
    }


    // Data received events

    @Override
    public boolean onDataReceived(ClientInfo client, byte[] readData) throws Throwable {
        boolean res = super.onDataReceived(client, readData);
        onDataReceivedBytes.countDown();
        return res;
    }

    @Override
    public boolean onDataReceived(ClientInfo client, String readData) throws Throwable {
        boolean res = super.onDataReceived(client, readData);
        onDataReceivedString.countDown();
        return res;
    }

}
