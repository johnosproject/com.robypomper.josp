package com.robypomper.comm.exception;

import com.robypomper.comm.peer.Peer;

public class PeerNotConnectedException extends PeerException {

    // Class constants

    private static final String MSG = "Error on Peer '%s' because socket not connected";


    // Constructors

    public PeerNotConnectedException(Peer peer) {
        super(peer, String.format(MSG, peer));
    }

    public PeerNotConnectedException(Peer peer, String message) {
        super(peer, message);
    }

}
