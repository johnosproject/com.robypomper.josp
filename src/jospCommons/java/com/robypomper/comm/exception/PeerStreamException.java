package com.robypomper.comm.exception;

import com.robypomper.comm.peer.Peer;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class PeerStreamException extends PeerException {

    // Class constants

    private static final String MSG_IN = "Error on Peer '%s''s input stream";
    private static final String MSG_OUT = "Error on Peer '%s''s output stream";


    // Internal vars

    private final DataInputStream in;
    private final DataOutputStream out;


    // Constructors

    public PeerStreamException(Peer peer, DataInputStream in) {
        this(peer, in, String.format(MSG_IN, peer));
    }

    public PeerStreamException(Peer peer, DataInputStream in, Throwable cause) {
        this(peer, in, cause, String.format(MSG_IN, peer));
    }

    public PeerStreamException(Peer peer, DataInputStream in, String message) {
        this(peer, in, null, message);
    }

    public PeerStreamException(Peer peer, DataInputStream in, Throwable cause, String message) {
        super(peer, message);
        this.in = in;
        this.out = null;
    }

    public PeerStreamException(Peer peer, DataOutputStream out) {
        this(peer, out, String.format(MSG_OUT, peer));
    }

    public PeerStreamException(Peer peer, DataOutputStream out, Throwable cause) {
        this(peer, out, cause, String.format(MSG_OUT, peer));
    }

    public PeerStreamException(Peer peer, DataOutputStream out, String message) {
        this(peer, out, null, message);
    }

    public PeerStreamException(Peer peer, DataOutputStream out, Throwable cause, String message) {
        super(peer, cause, message);
        this.in = null;
        this.out = out;
    }


    // Getters

    public boolean isInputStreamError() {
        return in != null;
    }

    public DataInputStream getInputStream() {
        return in;
    }

    public boolean isOutputStreamError() {
        return out != null;
    }

    public DataOutputStream getOutputStream() {
        return out;
    }

}
