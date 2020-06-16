package com.robypomper.josp.jsl.objs.structure;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Default implementation of {@link JSLAction} interface.
 * <p>
 * This class inherits {@link AbsJSLState} so it manage also a action's status
 * updates.
 */
public class AbsJSLAction extends AbsJSLState
        implements JSLAction {

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
    public AbsJSLAction(JSLRemoteObject remoteObject, String name, String descr, String type) {
        super(remoteObject, name, descr, type);
    }


    // Action cmd flow (struct)

    /**
     * {@inheritDoc}
     */
    @Override
    public void execAction(JSLActionParams params) {
        log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Propagating component '%s' state", getName()));
        getRemoteObject().getCommunication().forwardAction(getRemoteObject(), this, params);
        log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Component '%s' propagated state", getName()));
    }


    // Temporary: this class must be defined and implemented by AbsJSLAction sub classes
    public static class JOSPIntTest implements JSLActionParams {

        private final int newState;
        private final int oldState;

        public JOSPIntTest(int newState, AbsJSLAction component) {
            this.newState = newState;
            this.oldState = component.getState();
        }

        @Override
        public String getType() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String encode() {
            return String.format("new:%d\nold:%d", newState, oldState);
        }

    }

}
