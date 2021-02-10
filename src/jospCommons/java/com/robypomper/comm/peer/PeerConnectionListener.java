package com.robypomper.comm.peer;

public interface PeerConnectionListener {

    // Events

    void onConnecting(Peer peer);

    void onWaiting(Peer peer);

    void onConnect(Peer peer);

    void onDisconnecting(Peer peer);

    void onDisconnect(Peer peer);

    void onFail(Peer peer, String failMsg, Throwable exception);

}
