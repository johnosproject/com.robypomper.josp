package com.robypomper.josp.states;

/**
 * JSL Local communication state representations.
 */
public enum JSLLocalState {

    /**
     * JSL library instance is started and waiting for new services.
     */
    RUN_WAITING,

    /**
     * JSL library instance is started and connected to some service.
     */
    RUN_CONNECTED,

    /**
     * JSL library instance is starting, when finish the status become
     * or {@link #STOP} if error occurs.
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
    SHOUTING;


    /**
     * Join all RUN_ states.
     *
     * @return true if current state is a RUN_ state.
     */
    public boolean isRUN() {
        return this == RUN_WAITING
                || this == RUN_CONNECTED
                //|| this == CONNECTING_WAITING_JCP_GWS
                ;
    }


}
