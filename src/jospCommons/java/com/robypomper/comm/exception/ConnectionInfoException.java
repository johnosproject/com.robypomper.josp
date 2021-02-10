package com.robypomper.comm.exception;

import com.robypomper.comm.connection.ConnectionInfo;

public class ConnectionInfoException extends Throwable {

    // Internal vars

    private final ConnectionInfo connection;


    // Constructors

    public ConnectionInfoException(ConnectionInfo connection) {
        this.connection = connection;
    }

    public ConnectionInfoException(ConnectionInfo connection, String message) {
        super(message);
        this.connection = connection;
    }

    public ConnectionInfoException(ConnectionInfo connection, Throwable cause) {
        super(cause);
        this.connection = connection;
    }

    public ConnectionInfoException(ConnectionInfo connection, Throwable cause, String message) {
        super(message, cause);
        this.connection = connection;
    }


    // Getters

    public ConnectionInfo getConnectionInfo() {
        return connection;
    }

}
