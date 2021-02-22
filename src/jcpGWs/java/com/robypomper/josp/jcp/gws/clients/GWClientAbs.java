package com.robypomper.josp.jcp.gws.clients;

import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;
import com.robypomper.comm.peer.Peer;
import com.robypomper.comm.peer.PeerConnectionListener;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.josp.jcp.gws.broker.GWBroker;

public abstract class GWClientAbs {

    // Internal vars

    private final ServerClient client;
    private final GWBroker gwBroker;


    // Constructors

    protected GWClientAbs(ServerClient client, GWBroker gwBroker) {
        this.client = client;
        this.gwBroker = gwBroker;
        client.addListener(connectionListener);
    }


    // Getters

    public String getId() {
        return client.getRemoteId();
    }

    protected GWBroker getBroker() {
        return gwBroker;
    }


    // Connection mngm

    protected void forceDisconnection() throws PeerDisconnectionException {
        client.disconnect();
    }


    // Messages methods

    public void send(String data) throws PeerStreamException, PeerNotConnectedException {
        client.sendData(data);
    }


    // Connection mngm

    protected abstract void onDisconnected();

    private final PeerConnectionListener connectionListener = new PeerConnectionListener() {

        @Override
        public void onConnecting(Peer peer) {

        }

        @Override
        public void onWaiting(Peer peer) {

        }

        @Override
        public void onConnect(Peer peer) {

        }

        @Override
        public void onDisconnecting(Peer peer) {

        }

        @Override
        public void onDisconnect(Peer peer) {
            onDisconnected();
        }

        @Override
        public void onFail(Peer peer, String failMsg, Throwable exception) {
        }

    };

}
