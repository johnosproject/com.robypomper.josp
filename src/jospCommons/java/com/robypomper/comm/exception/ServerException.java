package com.robypomper.comm.exception;

import com.robypomper.comm.server.Server;

public class ServerException extends Throwable {

    // Internal vars

    private final Server server;


    // Constructors

    public ServerException(Server server) {
        this.server = server;
    }

    public ServerException(Server server, String message) {
        super(message);
        this.server = server;
    }

    public ServerException(Server server, Throwable cause) {
        super(cause);
        this.server = server;
    }

    public ServerException(Server server, Throwable cause, String message) {
        super(message, cause);
        this.server = server;
    }


    // Getters

    public Server getServer() {
        return server;
    }

}
