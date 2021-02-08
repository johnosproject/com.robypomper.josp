package com.robypomper.comm.peer;

import java.net.InetAddress;

public interface PeerInfo {

    // Getters

    boolean isConnected();

    String getId();

    String getHostname();

    InetAddress getAddr();

    Integer getPort();

    boolean isLocal();

    boolean isRemote();

}
