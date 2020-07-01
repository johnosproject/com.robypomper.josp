package com.robypomper.josp.jod.structure;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * State component representation.
 * <p>
 * State component must propagate monitored status changes to connected services.
 * This is allow by the method {@link #propagateState(JODStateUpdate)} that can
 * be called from {@link com.robypomper.josp.jod.executor.AbsJODWorker#sendUpdate(JODStateUpdate)}.
 */
@SuppressWarnings("JavadocReference")
public interface JODState extends JODComponent {

    // Status's properties

    /**
     * The string representing the worker that monitoring current component.
     *
     * @return the string representing current state's worker.
     */
    @JsonIgnore
    String getWorker();

    String getState();


    // Status upd flow (struct)

    /**
     * Called by current state's worker (puller or listener), this method forward
     * the <code>statusUpd</code> to the JOD Communication system.
     *
     * @param statusUpd the status to propagate.
     */
    void propagateState(JODStateUpdate statusUpd) throws JODStructure.CommunicationSetException;

}
