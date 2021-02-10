package com.robypomper.comm.peer;

public enum DisconnectionReason {

    // Values

    NOT_DISCONNECTED,

    LOCAL_REQUEST,
    REMOTE_REQUEST,

    CONNECTION_LOST,
    HEARTBEAT_TIMEOUT,
    REMOTE_ERROR;


    // Utils

    public boolean isRequested() {
        return this == LOCAL_REQUEST
                || this == REMOTE_REQUEST;
    }

    public boolean isError() {
        return this == CONNECTION_LOST
                || this == HEARTBEAT_TIMEOUT
                || this == REMOTE_ERROR;
    }

}
