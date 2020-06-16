package com.robypomper.josp.jod.structure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.robypomper.josp.jod.executor.JODExecutor;
import com.robypomper.josp.protocol.JOSPProtocol;


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
    @JsonIgnore
    String getExecutor();


    // Status upd flow (struct)

    /**
     * Called by JOD Communication system, this method execute the action via
     * the {@link JODExecutor#exec()} method.
     *
     * @param commandAction the action's to execute.
     */
    boolean execAction(JOSPProtocol.ActionCmd commandAction);

}
