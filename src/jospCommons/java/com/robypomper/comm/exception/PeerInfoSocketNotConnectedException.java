package com.robypomper.comm.exception;

import com.robypomper.comm.peer.PeerInfo;

import java.net.ServerSocket;
import java.net.Socket;

public class PeerInfoSocketNotConnectedException extends PeerInfoException {

    // Class constants

    private static final String MSG = "Error set connected state to PeerInfo '%s' because socket not connected";
    private static final String MSG_SERVER = "Error set connected state to PeerInfo '%s' because server socket not bound";


    // Internal vars

    private final Socket socket;
    private final ServerSocket serverSocket;


    // Constructors

    public PeerInfoSocketNotConnectedException(PeerInfo peer, Socket socket) {
        super(peer, String.format(MSG, peer));
        this.socket = socket;
        this.serverSocket = null;
    }

    public PeerInfoSocketNotConnectedException(PeerInfo peer, Socket socket, Throwable cause) {
        super(peer, cause, String.format(MSG, peer));
        this.socket = socket;
        this.serverSocket = null;
    }

    public PeerInfoSocketNotConnectedException(PeerInfo peer, ServerSocket socket) {
        super(peer, String.format(MSG_SERVER, peer));
        this.socket = null;
        this.serverSocket = socket;
    }

    public PeerInfoSocketNotConnectedException(PeerInfo peer, ServerSocket socket, Throwable cause) {
        super(peer, cause, String.format(MSG_SERVER, peer));
        this.socket = null;
        this.serverSocket = socket;
    }


    // Getters

    public Socket getSocket() {
        return socket;
    }

}
