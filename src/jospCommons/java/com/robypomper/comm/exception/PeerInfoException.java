package com.robypomper.comm.exception;

import com.robypomper.comm.peer.PeerInfo;

public class PeerInfoException extends Throwable {

    // Internal vars

    private final PeerInfo peer;


    // Constructors

    public PeerInfoException(PeerInfo peer) {
        this.peer = peer;
    }

    public PeerInfoException(PeerInfo peer, String message) {
        super(message);
        this.peer = peer;
    }

    public PeerInfoException(PeerInfo peer, Throwable cause) {
        super(cause);
        this.peer = peer;
    }

    public PeerInfoException(PeerInfo peer, Throwable cause, String message) {
        super(message, cause);
        this.peer = peer;
    }


    // Getters

    public PeerInfo getPeer() {
        return peer;
    }

}
