package com.robypomper.comm.behaviours;

import com.robypomper.comm.peer.Peer;

public interface ByeMsgListener {

    // Events

    void onBye(Peer peer, ByeMsgImpl byeMsg);

}
