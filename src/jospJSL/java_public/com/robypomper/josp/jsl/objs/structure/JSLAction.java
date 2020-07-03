package com.robypomper.josp.jsl.objs.structure;


import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.Logger;


/**
 * Action component representation.
 * <p>
 * Action component receive commands from the 3rd party service implementation
 * and send them to corresponding JOD object.
 */
public interface JSLAction extends JSLState {

    // Action cmd flow (struct)

    static void execAction(JSLActionParams params, JSLAction comp, Logger log) throws JSLRemoteObject.MissingPermission, JSLRemoteObject.ObjectNotConnected {
        log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Sending component '%s' state to object '%s'", comp.getName(), comp.getRemoteObject().getId()));
        comp.getRemoteObject().sendObjectCmdMsg(comp, params);
        log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Component '%s' send state to object '%s'", comp.getName(), comp.getRemoteObject().getId()));
    }
}
