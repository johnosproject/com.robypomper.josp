package com.robypomper.josp.jod.structure.pillars;

import com.robypomper.java.JavaFormatter;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.structure.AbsJODState;
import com.robypomper.josp.jod.structure.JODStateUpdate;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.jod.structure.StructureDefinitions;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class JODRangeState extends AbsJODState {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final double min;
    private final double max;
    private final double step;
    private double state = 0;


    // Constructor

    /**
     * Default constructor that initialize the status component with his
     * properties and worker.
     *
     * <b>NB:</b> only once of <code>listener</code> and <code>puller</code>
     * params can be set, the other one must be null.
     *
     * @param structure the JOD Structure system.
     * @param execMngr  the JOD Executor Mngr system.
     * @param name      the name of the component.
     * @param descr     the description of the component.
     * @param listener  the listener full configs string.
     * @param puller    the puller full configs string.
     */
    public JODRangeState(JODStructure structure, JODExecutorMngr execMngr, String name, String descr, String listener, String puller, Double min, Double max, Double step) throws JODStructure.ComponentInitException {
        super(structure, execMngr, name, descr, listener, puller);
        this.min = min != null ? min : 0;
        this.max = max != null ? max : 100;
        this.step = step != null ? step : 10;
    }


    // Status's properties

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return StructureDefinitions.TYPE_RANGE_STATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() {
        return Double.toString(state);
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStep() {
        return step;
    }


    // Status's methods

    public void setUpdate(double newState) {
        double oldState = state;
        state = newState;
        try {
            propagateState(new JOSPRange(newState, oldState));
        } catch (JODStructure.CommunicationSetException e) {
            log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error on propagating state of component '%s' to JOD Communication because %s", getName(), e.getMessage()), e);
        }
    }


    // Range StateUpdate implementation

    private static class JOSPRange implements JODStateUpdate {

        private final double newState;
        private final double oldState;

        public JOSPRange(double newState, double oldState) {
            this.newState = newState;
            this.oldState = oldState;
        }

        @Override
        public String getType() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String encode() {
            return String.format("new:%s\nold:%s", JavaFormatter.doubleToStr(newState), JavaFormatter.doubleToStr(oldState));
        }

    }

}
