package com.robypomper.josp.states;

/**
 * JSL state representations.
 */
public enum JODState {

    /**
     * JSL library instance is started and operative.
     */
    RUN,

    /**
     * JSL library instance is starting, when finish the status become
     * {@link #RUN} or {@link #STOP} if error occurs.
     */
    STARTING,

    /**
     * JSL library instance is stopped.
     */
    STOP,

    /**
     * JSL library instance is disconnecting, when finish the status
     * become {@link #STOP}.
     */
    SHOUTING,

    /**
     * JSL library instance is shouting down and startup, when finish the status
     * become {@link #RUN} or {@link #STOP} if error occurs.
     */
    RESTARTING;

}
