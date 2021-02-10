package com.robypomper.comm.exception;

import com.robypomper.comm.server.Server;

import java.net.InetAddress;
import java.net.ServerSocket;

public class ServerStartupException extends ServerException {

    // Class constants

    private static final String MSG = "Can't startup '%s' server because can't bind server's socket to '%s:%d'";


    // Internal vars

    private final ServerSocket serverSocket;
    private final InetAddress bindAddr;
    private final int bindPort;


    // Constructors

    public ServerStartupException(Server server, InetAddress bindAddr, int bindPort) {
        this(server, bindAddr, bindPort, null);
    }

    public ServerStartupException(Server server, InetAddress bindAddr, int bindPort, Throwable cause) {
        this(server, null, bindAddr, bindPort, cause);
    }

    public ServerStartupException(Server server, ServerSocket serverSocket, InetAddress bindAddr, int bindPort) {
        this(server, serverSocket, bindAddr, bindPort, null);
    }

    public ServerStartupException(Server server, ServerSocket serverSocket, InetAddress bindAddr, int bindPort, Throwable cause) {
        super(server, cause, String.format(MSG, server.getLocalId(), bindAddr, bindPort));
        this.serverSocket = serverSocket;
        this.bindAddr = bindAddr;
        this.bindPort = bindPort;
    }


    // Getters

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public InetAddress getServerAddr() {
        return bindAddr;
    }

    public int getServerPort() {
        return bindPort;
    }

}
