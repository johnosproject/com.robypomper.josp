package com.robypomper.josp.jsl.objs.structure.pillars;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLAction;
import com.robypomper.josp.jsl.objs.structure.JSLActionParams;
import com.robypomper.log.Mrk_JSL;
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
    public JSLRangeAction(JSLRemoteObject remoteObject, String name, String descr, String type, double min, double max, double step) {
        super(remoteObject, name, descr, type, min, max, step);
    }


    // Action's methods

    @Override
    public void execAction(JSLActionParams params) {
        log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Propagating component '%s' state", getName()));
        getRemoteObject().getCommunication().forwardAction(getRemoteObject(), this, params);
        log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Component '%s' propagated state", getName()));
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
