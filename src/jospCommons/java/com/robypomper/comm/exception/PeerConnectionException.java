package com.robypomper.comm.exception;

import com.robypomper.comm.peer.Peer;

import javax.net.ssl.SSLException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

public class PeerConnectionException extends PeerException {

    // Class constants

    private static final String MSG = "Error on Peer '%s' because can't connect socket to '%s:%d'";


    // Internal vars

    private final Socket socket;
    private final InetAddress remoteAddr;
    private final int remotePort;


    // Constructors

    public PeerConnectionException(Peer peer) {
        this(peer, String.format(MSG, peer, "N/A", 0));
    }

    public PeerConnectionException(Peer peer, Throwable cause) {
        this(peer, cause, String.format(MSG, peer, "N/A", 0));
    }

    public PeerConnectionException(Peer peer, String message) {
        this(peer, (Throwable) null, message);
    }

    public PeerConnectionException(Peer peer, Throwable cause, String message) {
        this(peer, null, null, 0, cause, message);
    }

    public PeerConnectionException(Peer peer, Socket socket) {
        this(peer, socket, String.format(MSG, peer, "N/A", 0));
    }

    public PeerConnectionException(Peer peer, Socket socket, Throwable cause) {
        this(peer, socket, cause, String.format(MSG, peer, "N/A", 0));
    }

    public PeerConnectionException(Peer peer, Socket socket, String message) {
        this(peer, socket, null, message);
    }

    public PeerConnectionException(Peer peer, Socket socket, Throwable cause, String message) {
        this(peer, socket, null, 0, cause, message);
    }

    public PeerConnectionException(Peer peer, Socket socket, InetAddress remoteAddr, int remotePort) {
        this(peer, socket, remoteAddr, remotePort, String.format(MSG, peer, (remoteAddr != null ? remoteAddr.getHostAddress() : "N/A"), remotePort));
    }

    public PeerConnectionException(Peer peer, Socket socket, InetAddress remoteAddr, int remotePort, Throwable cause) {
        this(peer, socket, remoteAddr, remotePort, cause, String.format(MSG, peer, (remoteAddr != null ? remoteAddr.getHostAddress() : "N/A"), remotePort));
    }

    public PeerConnectionException(Peer peer, Socket socket, InetAddress remoteAddr, int remotePort, String message) {
        this(peer, socket, remoteAddr, remotePort, null, message);
    }

    public PeerConnectionException(Peer peer, Socket socket, InetAddress remoteAddr, int remotePort, Throwable cause, String message) {
        super(peer, cause, message);
        //if (cause != null) System.out.println(String.format(Thread.currentThread().getName() + " Emitted PeerConnectionException with cause [%s] %s", cause.getClass().getSimpleName(), cause.getMessage()));
        //else System.out.println(Thread.currentThread().getName() + " Emitted PeerConnectionException with NO cause");
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

    public boolean isRemotePeerNotAvailable() {
        return getCause() instanceof ConnectException;
    }

    public boolean isLocalPeerNotAllowed() {
        return getCause() instanceof SSLException;
    }

}
