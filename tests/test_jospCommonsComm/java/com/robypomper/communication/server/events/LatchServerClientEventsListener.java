package com.robypomper.communication.server.events;

import com.robypomper.communication.server.ClientInfo;

import java.util.concurrent.CountDownLatch;


public class LatchServerClientEventsListener extends LogServerClientEventsListener {

    // Internal vars

    public CountDownLatch onClientConnection = new CountDownLatch(1);
    public CountDownLatch onClientDisconnection = new CountDownLatch(1);
    public CountDownLatch onClientServerDisconnected = new CountDownLatch(1);
    public CountDownLatch onClientGoodbye = new CountDownLatch(1);
    public CountDownLatch onClientTerminated = new CountDownLatch(1);
    public CountDownLatch onClientError = new CountDownLatch(1);


    // Client connection events

    @Override
    public void onClientConnection(ClientInfo client) {
        super.onClientConnection(client);
        onClientConnection.countDown();
    }


    // Client disconnection events

    @Override
    public void onClientDisconnection(ClientInfo client) {
        super.onClientDisconnection(client);
        onClientDisconnection.countDown();
    }

    @Override
    public void onClientServerDisconnected(ClientInfo client) {
        super.onClientServerDisconnected(client);
        onClientServerDisconnected.countDown();
    }

    @Override
    public void onClientGoodbye(ClientInfo client) {
        super.onClientGoodbye(client);
        onClientGoodbye.countDown();
    }

    @Override
    public void onClientTerminated(ClientInfo client) {
        super.onClientTerminated(client);
        onClientTerminated.countDown();
    }


    // Client errors events

    @Override
    public void onClientError(ClientInfo client, Throwable t) {
        super.onClientError(client, t);
        onClientError.countDown();
    }

}
