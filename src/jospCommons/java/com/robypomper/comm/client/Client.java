package com.robypomper.comm.client;

import com.robypomper.comm.configs.AutoReConnectConfigs;
import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.peer.Peer;

public interface Client extends Peer {

    // Client connection methods

    void connect() throws PeerConnectionException;


    // Behaviours configs

    AutoReConnectConfigs getAutoReConnectConfigs();

}
