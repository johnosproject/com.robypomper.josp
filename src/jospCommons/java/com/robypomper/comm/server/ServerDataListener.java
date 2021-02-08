package com.robypomper.comm.server;

import com.robypomper.comm.peer.Peer;

public interface ServerDataListener {

    // Events

    void onDataRx(Server server, Peer peer, byte[] data);

    void onDataTx(Server server, Peer peer, byte[] data);

}
