package com.robypomper.josp.jsl.objs.structure.pillars;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLAction;
import com.robypomper.josp.jsl.objs.structure.JSLActionParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JSLRangeAction extends JSLRangeState implements JSLAction {

    // Internal vars

    private static final Logger log = LogManager.getLogger();


    // Constructor

    /**
     * Default constructor that initialize the action component with his
     * properties.
     *
     * @param remoteObject the {@link JSLRemoteObject} representing JOD object.
     * @param name         the name of the component.
     * @param descr        the description of the component.
     */
    public JSLRangeAction(JSLRemoteObject remoteObject, String name, String descr, String type, double min, double max, double step, double state) {
        super(remoteObject, name, descr, type, min, max, step, state);
    }


    // Action's methods

    public void execSetValue(double val) throws JSLRemoteObject.MissingPermission, JSLRemoteObject.ObjectNotConnected {
        if (val < getMin() || val > getMax())
            return;

        JSLAction.execAction(new JSLRangeAction.JOSPRange(val, this), this, log);
    }

    public void execSetMin() throws JSLRemoteObject.MissingPermission, JSLRemoteObject.ObjectNotConnected {
        if (getState() == getMin())
            return;

        JSLAction.execAction(new JSLRangeAction.JOSPRange(getMin(), this), this, log);
    }

    public void execSetMax() throws JSLRemoteObject.MissingPermission, JSLRemoteObject.ObjectNotConnected {
        if (getState() == getMax())
            return;

        JSLAction.execAction(new JSLRangeAction.JOSPRange(getMax(), this), this, log);
    }

    public void execIncrease() throws JSLRemoteObject.MissingPermission, JSLRemoteObject.ObjectNotConnected {
        if (getState() >= getMax())
            return;

        double val = getState() + getStep();
        if (val > getMax())
            val = getMax();

        JSLAction.execAction(new JSLRangeAction.JOSPRange(val, this), this, log);
    }

    public void execDecrease() throws JSLRemoteObject.MissingPermission, JSLRemoteObject.ObjectNotConnected {
        if (getState() <= getMin())
            return;

        double val = getState() - getStep();
        if (val < getMin())
            val = getMin();

        JSLAction.execAction(new JSLRangeAction.JOSPRange(val, this), this, log);
    }


    // Boolean ActionParams implementation

    public static class JOSPRange implements JSLActionParams {

        private final double newState;
        private final double oldState;

        public JOSPRange(double newState, JSLRangeState component) {
            this.newState = newState;
            this.oldState = component.getState();
        }

        @Override
        public String getType() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String encode() {
            return String.format("new:%f\nold:%f", newState, oldState);
        }

    }

}
