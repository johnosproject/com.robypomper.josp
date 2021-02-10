package com.robypomper.comm.exception;

import com.robypomper.comm.peer.Peer;

import java.net.InetAddress;
import java.net.Socket;

public class PeerDisconnectionException extends PeerException {

    // Class constants

    private static final String MSG = "Error on Peer '%s' because can't disconnect socket from '%s:%d'";


    // Internal vars

    private final Socket socket;
    private final InetAddress remoteAddr;
    private final int remotePort;


    // Constructors

    public PeerDisconnectionException(Peer peer) {
        this(peer, String.format(MSG, peer, "N/A", 0));
    }

    public PeerDisconnectionException(Peer peer, Throwable cause) {
        this(peer, cause, String.format(MSG, peer, "N/A", 0));
    }

    public PeerDisconnectionException(Peer peer, String message) {
        this(peer, (Throwable) null, message);
    }

    public PeerDisconnectionException(Peer peer, Throwable cause, String message) {
        this(peer, null, null, 0, cause, message);
    }

    public PeerDisconnectionException(Peer peer, Socket socket) {
        this(peer, socket, String.format(MSG, peer, "N/A", 0));
    }

    public PeerDisconnectionException(Peer peer, Socket socket, Throwable cause) {
        this(peer, socket, cause, String.format(MSG, peer, "N/A", 0));
    }

    public PeerDisconnectionException(Peer peer, Socket socket, String message) {
        this(peer, socket, null, message);
    }

    public PeerDisconnectionException(Peer peer, Socket socket, Throwable cause, String message) {
        this(peer, socket, null, 0, cause, message);
    }

    public PeerDisconnectionException(Peer peer, Socket socket, InetAddress remoteAddr, int remotePort) {
        this(peer, socket, remoteAddr, remotePort, String.format(MSG, peer, remoteAddr.getHostAddress(), remotePort));
    }

    public PeerDisconnectionException(Peer peer, Socket socket, InetAddress remoteAddr, int remotePort, Throwable cause) {
        this(peer, socket, remoteAddr, remotePort, cause, String.format(MSG, peer, remoteAddr.getHostAddress(), remotePort));
    }

    public PeerDisconnectionException(Peer peer, Socket socket, InetAddress remoteAddr, int remotePort, String message) {
        this(peer, socket, remoteAddr, remotePort, null, message);
    }

    public PeerDisconnectionException(Peer peer, Socket socket, InetAddress remoteAddr, int remotePort, Throwable cause, String message) {
        super(peer, cause, message);
        this.socket = socket;
        this.remoteAddr = remoteAddr;
        this.remotePort = remotePort;
    }


    // Getters

    public Socket getSocket() {
        return socket;
    }

    public InetAddress getRemoteAddr() {
        return remoteAddr;
    }

    public int getRemotePort() {
        return remotePort;
    }

}
