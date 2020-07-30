package com.robypomper.josp.jod.executor;


/**
 * Executor interface used by
 * {@link JODExecutorMngr} JOD system.
 */
public interface JODExecutor extends JODWorker {

    // Execution method

    /**
     * Method called from {@link com.robypomper.josp.jod.structure.JODComponent}
     * to exec current executor action.
     *
     * @return true if the action was execute successfully, false otherwise.
     */
    @Deprecated
    boolean exec();

    // Mngm

    /**
     * Enable action execution.
     */
    void enable();

    /**
     * Disable action execution.
     */
    void disable();

}
