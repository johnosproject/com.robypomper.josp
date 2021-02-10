package com.robypomper.comm.connection;

import com.robypomper.comm.peer.PeerInfoLocal;
import com.robypomper.comm.peer.PeerInfoRemote;

public interface ConnectionInfo {

    // Getters

    ConnectionState getState();

    ConnectionStats getStats();

    PeerInfoLocal getLocalInfo();

    PeerInfoRemote getRemoteInfo();

    String getProtocolName();

}
