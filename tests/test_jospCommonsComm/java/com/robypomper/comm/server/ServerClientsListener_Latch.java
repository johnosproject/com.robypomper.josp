package com.robypomper.comm.server;

import java.util.concurrent.CountDownLatch;

public class ServerClientsListener_Latch implements ServerClientsListener {

    public CountDownLatch onConnect = new CountDownLatch(1);
    public ServerClient clientOnConnect;
    public CountDownLatch onDisconnect = new CountDownLatch(1);
    public ServerClient clientOnDisconnect;
    public CountDownLatch onFail = new CountDownLatch(1);

    @Override
    public void onConnect(Server server, ServerClient client) {
        clientOnConnect = client;
        onConnect.countDown();
    }

    @Override
    public void onDisconnect(Server server, ServerClient client) {
        clientOnDisconnect = client;
        onDisconnect.countDown();
    }

    @Override
    public void onFail(Server server, ServerClient client, String failMsg, Throwable exception) {
        onFail.countDown();
    }

}
