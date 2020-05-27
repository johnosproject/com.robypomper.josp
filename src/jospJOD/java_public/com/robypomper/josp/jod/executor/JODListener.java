package com.robypomper.josp.jod.executor;


/**
 * Listener interface used by
 * {@link JODExecutorMngr} JOD system.
 */
public interface JODListener extends JODWorker {

    // Listen methods

    /**
     * Start listener server.
     */
    void listen();

    /**
     * Halt listener server.
     */
    void halt();

}
