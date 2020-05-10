package com.robypomper.josp.jsl.objs.structure;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.protocol.JOSPProtocol;


/**
 * Default implementation of {@link JSLState} interface.
 */
public class AbsJSLState extends AbsJSLComponent
        implements JSLState {


    // Constructor

    /**
     * Default constructor that initialize the status component with his
     * properties.
     *
     * @param remoteObject the {@link JSLRemoteObject} representing JOD object.
     * @param name         the name of the component.
     * @param descr        the description of the component.
     */
    public AbsJSLState(JSLRemoteObject remoteObject, String name, String descr) {
        super(remoteObject, name, descr);
    }


    // Status upd flow (struct)

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateStatus(JOSPProtocol.StatusUpd statusUpd) {
        // ToDo: implement updateStatus method
        return false;
    }

}
