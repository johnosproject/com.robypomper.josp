package com.robypomper.josp.jsl.objs.structure.pillars;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLAction;
import com.robypomper.josp.jsl.objs.structure.JSLActionParams;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JSLBooleanAction extends JSLBooleanState implements JSLAction {

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
    public JSLBooleanAction(JSLRemoteObject remoteObject, String name, String descr, String type) {
        super(remoteObject, name, descr, type);
    }


    // Action's methods

    @Override
    public void execAction(JSLActionParams params) {
        log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Propagating component '%s' state", getName()));
        getRemoteObject().getCommunication().forwardAction(getRemoteObject(), this, params);
        log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Component '%s' propagated state", getName()));
    }


    // Boolean ActionParams implementation

    public static class JOSPBoolean implements JSLActionParams {

        private final boolean newState;
        private final boolean oldState;

        public JOSPBoolean(boolean newState, JSLBooleanState component) {
            this.newState = newState;
            this.oldState = component.getState();
        }

        @Override
        public String getType() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String encode() {
            return String.format("new:%b\nold:%b", newState, oldState);
        }

    }

}
