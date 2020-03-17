package com.robypomper.josp.jod.executor;


/**
 * Puller interface used by
 * {@link com.robypomper.josp.jod.systems.JODExecutorMngr} JOD system.
 */
public interface JODPuller extends JODWorker {

    // Polling method

    /**
     * Polling method, executed periodically each {@link AbsJODPuller#getPollingTime()}
     * milliseconds.
     * <p>
     * Implementation of this function must perform a pulling request and
     * when detect value updates, then send the update to the corresponding JOD
     * Component via the {@link AbsJODWorker#sendUpdate(com.robypomper.josp.jod.structure.JODStateUpdate)}
     * method (this method is provided by the {@link AbsJODWorker} class because
     * need to be implemented, the {@link JODPuller} interface can't host methods
     * implementations. AbsJODWorker is the basic class for each JOD Puller
     * implementation).
     * <p>
     * This method is call only by the internal JOD Puller timer, so no extra
     * check are required like the {@link AbsJODListenerLoop} class.
     */
    void pull();

    // Mngm

    /**
     * Start puller timer.
     */
    void startTimer();


    /**
     * Stop puller timer.
     */
    void stopTimer();

}
