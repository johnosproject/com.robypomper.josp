package com.robypomper.comm.connection;

public enum ConnectionState {

    // Values

    CONNECTING,         // Only Client
    WAITING_SERVER,     // Only Client
    CONNECTED,          // Client + Server
    DISCONNECTING,      // Only Client
    DISCONNECTED;       // Client + Server


    // Utils

    public boolean isConnecting() {
        return this == CONNECTING
                || this == WAITING_SERVER;
    }

    public boolean isConnected() {
        return this == CONNECTED;
    }

    public boolean isDisconnecting() {
        return this == DISCONNECTING;
    }

    public boolean isDisconnected() {
        return this == DISCONNECTED;
    }

}
