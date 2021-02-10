package com.robypomper.comm.exception;

import com.robypomper.comm.server.Server;

public class ServerShutdownException extends ServerException {


    // Constructors

    public ServerShutdownException(Server server, Throwable cause, String msg) {
        super(server, cause, msg);
    }

}
