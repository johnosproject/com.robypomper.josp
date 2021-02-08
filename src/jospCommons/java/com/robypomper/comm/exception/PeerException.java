package com.robypomper.comm.exception;

import com.robypomper.comm.peer.Peer;

public class PeerException extends Throwable {

    // Internal vars

    private final Peer peer;


    // Constructors

    public PeerException(Peer peer) {
        this.peer = peer;
    }

    public PeerException(Peer peer, String message) {
        super(message);
        this.peer = peer;
    }

    public PeerException(Peer peer, Throwable cause) {
        super(cause);
        this.peer = peer;
    }

    public PeerException(Peer peer, Throwable cause, String message) {
        super(message, cause);
        this.peer = peer;
    }


    // Getters

    public Peer getPeer() {
        return peer;
    }

}
