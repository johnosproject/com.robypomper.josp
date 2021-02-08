package com.robypomper.comm.server;

public enum ServerState {

    // Values

    STARTUP,
    STARTED,
    SHUTDOWN,
    STOPPED;


    // Utils

    public boolean isStartup() {
        return this == STARTUP;
    }

    public boolean isRunning() {
        return this == STARTED;
    }

    public boolean isShutdown() {
        return this == SHUTDOWN;
    }

    public boolean isStopped() {
        return this == STOPPED;
    }

}
