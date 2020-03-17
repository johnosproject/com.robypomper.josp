package com.robypomper.josp.jod.structure;

import com.robypomper.josp.jod.executor.JODExecutor;


/**
 * Action component representation.
 * <p>
 * Action component receive commands from the JOD Communication system and elaborate
 * them using associated {@link com.robypomper.josp.jod.executor.JODExecutor}.
 */
public interface JODAction extends JODState {

    // Status's properties

    /**
     * The string representing the executor that execute current component actions.
     *
     * @return the string representing current action's executor.
     */
    String getExecutor();


    // Status upd flow (struct)

    /**
     * Called by JOD Communication system, this method execute the action via
     * the {@link JODExecutor#exec()} method.
     *
     * @param params the action's to params.
     */
    boolean execAction(JODActionParams params);

}
