package com.robypomper.josp.jcp.gws.gw;

import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;
import com.robypomper.comm.peer.Peer;
import com.robypomper.comm.peer.PeerConnectionListener;
import com.robypomper.comm.server.ServerClient;

public abstract class GWClientTCPAbs {

    // Internal vars

    private final ServerClient client;


    // Constructors

    protected GWClientTCPAbs(ServerClient client) {
        this.client = client;
        client.addListener(connectionListener);
    }


    // Getters

    public String getId() {
        return client.getRemoteId();
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

    @SuppressWarnings("FieldCanBeLocal")
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
