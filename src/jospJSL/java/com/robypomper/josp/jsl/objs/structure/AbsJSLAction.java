package com.robypomper.josp.jsl.objs.structure;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;


/**
 * Default implementation of {@link JSLAction} interface.
 * <p>
 * This class inherits {@link AbsJSLState} so it manage also a action's status
 * updates.
 */
public class AbsJSLAction extends AbsJSLState
        implements JSLAction {

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
    public boolean execAction(JSLActionParams params) {
        // ToDo: implement execAction method
        return false;
    }

}
