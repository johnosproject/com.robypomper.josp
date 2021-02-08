package com.robypomper.comm.peer;

public interface PeerDataListener {

    // Events

    void onDataRx(Peer peer, byte[] data);

    void onDataTx(Peer peer, byte[] data);

}
