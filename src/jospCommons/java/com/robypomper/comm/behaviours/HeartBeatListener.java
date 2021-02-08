package com.robypomper.comm.behaviours;

import com.robypomper.comm.peer.Peer;

public interface HeartBeatListener {

    // Events

    void onSend(Peer peer, HeartBeatConfigs hb);

    void onSuccess(Peer peer, HeartBeatConfigs hb);

    void onFail(Peer peer, HeartBeatConfigs hb);

}
