package com.robypomper.josp.jcp.gws.broker;

import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;

public interface BrokerClient {

    // Getters

    String getId();

    String getName();


    // Messages methods

    void send(String data) throws PeerNotConnectedException, PeerStreamException;

}
