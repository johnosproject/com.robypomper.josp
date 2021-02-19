package com.robypomper.comm.peer;

import com.robypomper.comm.exception.PeerInfoSocketNotConnectedException;
import com.robypomper.java.JavaSSL;

import javax.net.ssl.SSLSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerInfoDefault implements PeerInfo {

    // Internal vars

    // Properties from constructor
    private String id;
    private final String protoName;
    private final boolean isLocal;
    // Properties after first connection
    private String hostname = null;
    private InetAddress addr = null;
    private Integer port = null;
    // Properties updated on connection/disconnection
    private boolean isConnected = false;


    // Constructors

    protected PeerInfoDefault(String id, String protoName, boolean isLocal) {
        this.id = id;
        this.protoName = protoName;
        this.isLocal = isLocal;
    }


    // toString()

    @Override
    public String toString() {
        String extra = addr != null ? String.format("%s:%d", addr.getHostAddress(), port) : "NotConnected";
        return String.format("%s (%s)", id, extra);
    }


    // Getters

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getProto() {
        return protoName;
    }

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public InetAddress getAddr() {
        return addr;
    }

    @Override
    public Integer getPort() {
        return port;
    }

    @Override
    public boolean isLocal() {
        return isLocal;
    }

    @Override
    public boolean isRemote() {
        return !isLocal;
    }


    // Updaters

    public void updateOnConnected(Socket socket) throws PeerInfoSocketNotConnectedException {
        if (!socket.isConnected() || socket.isClosed())
            throw new PeerInfoSocketNotConnectedException(this, socket);

        if (isLocal()) {
            hostname = socket.getLocalAddress().getHostName();
            addr = socket.getLocalAddress();
            port = socket.getLocalPort();
        } else {
            id = extractRemoteId(socket);
            hostname = socket.getInetAddress().getHostName();
            addr = socket.getInetAddress();
            port = socket.getPort();
        }
        isConnected = true;
    }

    public void updateOnConnected(ServerSocket socket) throws PeerInfoSocketNotConnectedException {
        if (!socket.isBound() || socket.isClosed())
            throw new PeerInfoSocketNotConnectedException(this, socket);

        if (isLocal()) {
            hostname = socket.getInetAddress().getHostName();
            addr = socket.getInetAddress();
            port = socket.getLocalPort();
        } else {
            throw new IllegalArgumentException("Can't update remote peer info with ServerSocket as param");
        }
        isConnected = true;
    }

    public void updateOnDisconnected() {
        isConnected = false;
    }


    // Utils

    private String extractRemoteId(Socket socket) {
        if (socket instanceof SSLSocket) {
            try {
                return JavaSSL.getPeerId((SSLSocket) socket);
            } catch (JavaSSL.PeerException ignore) {
            }
        }

        return String.format("%s://%s:%d", protoName, socket.getInetAddress().getHostAddress(), socket.getPort());
    }

}
