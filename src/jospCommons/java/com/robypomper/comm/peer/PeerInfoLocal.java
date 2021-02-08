package com.robypomper.comm.peer;

import java.net.NetworkInterface;

public interface PeerInfoLocal extends PeerInfo {

    // Getters

    NetworkInterface getIntf();

}
