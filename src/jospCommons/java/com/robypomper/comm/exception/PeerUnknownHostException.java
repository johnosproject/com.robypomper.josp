package com.robypomper.comm.exception;

import com.robypomper.comm.peer.Peer;

public class PeerUnknownHostException extends PeerException {

    // Class constants

    private static final String MSG = "Error on Peer '%s' because can't resolve remoteAddr '%s'";


    // Internal vars

    private final String hostname;


    // Constructors

    public PeerUnknownHostException(String peerLocalId, String hostname) {
        this(peerLocalId, hostname, String.format(MSG, peerLocalId, hostname));
    }

    public PeerUnknownHostException(String peerLocalId, String hostname, Throwable cause) {
        this(peerLocalId, hostname, cause, String.format(MSG, peerLocalId, hostname));
    }

    public PeerUnknownHostException(String peerLocalId, String hostname, String message) {
        this(peerLocalId, hostname, null, message);
    }

    public PeerUnknownHostException(String peerLocalId, String hostname, Throwable cause, String message) {
        this((Peer) null, hostname, cause, message);
    }

    public PeerUnknownHostException(Peer peer, String hostname) {
        this(peer, hostname, String.format(MSG, peer, hostname));
    }

    public PeerUnknownHostException(Peer peer, String hostname, Throwable cause) {
        this(peer, hostname, cause, String.format(MSG, peer, hostname));
    }

    public PeerUnknownHostException(Peer peer, String hostname, String message) {
        this(peer, hostname, null, message);
    }

    public PeerUnknownHostException(Peer peer, String hostname, Throwable cause, String message) {
        super(peer, cause, message);
        this.hostname = hostname;
    }


    // Getters

    public String getHostname() {
        return hostname;
    }

}
