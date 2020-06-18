package com.robypomper.josp.jod.structure.pillars;

import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.structure.AbsJODState;
import com.robypomper.josp.jod.structure.JODStateUpdate;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.jod.structure.StructureDefinitions;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class JODBooleanState extends AbsJODState {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private boolean state = false;


    // Constructor

    /**
     * Default constructor that initialize the status component with his
     * properties and worker.
     *
     * <b>NB:</b> only once of <code>listener</code> and <code>puller</code>
     * params can be set, the other one must be null.
     * <p>
     * ToDo set AbsJODState constructor protected
     *
     * @param structure the JOD Structure system.
     * @param execMngr  the JOD Executor Mngr system.
     * @param name      the name of the component.
     * @param descr     the description of the component.
     * @param listener  the listener full configs string.
     * @param puller    the puller full configs string.
     */
    public JODBooleanState(JODStructure structure, JODExecutorMngr execMngr, String name, String descr, String listener, String puller) throws JODStructure.ComponentInitException {
        super(structure, execMngr, name, descr, listener, puller);
    }


    // Status's properties

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return StructureDefinitions.TYPE_BOOL_STATE;
    }


    // Status's methods

    public void setUpdate(boolean newState) {
        boolean oldState = state;
        state = newState;
        try {
            propagateState(new JOSPBoolean(newState, oldState));
        } catch (JODStructure.CommunicationSetException e) {
            log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error on propagating state of component '%s' to JOD Communication because %s", getName(), e.getMessage()), e);
        }
    }


    // Boolean StateUpdate implementation

    private static class JOSPBoolean implements JODStateUpdate {

        private final boolean newState;
        private final boolean oldState;

        public JOSPBoolean(boolean newState, boolean oldState) {
            this.newState = newState;
            this.oldState = oldState;
        }

        @Override
        public String getType() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String encode() {
            return String.format("new:%s\nold:%s", newState, oldState);
        }

    }

}
