package com.robypomper.josp.jsl.objs.structure;


/**
 * Action component representation.
 * <p>
 * Action component receive commands from the 3rd party service implementation
 * and send them to corresponding JOD object.
 */
public interface JSLAction extends JSLState {

    // Action cmd flow (struct)

    /**
     * Called by 3rd party service implementations, this method send the action
     * request to the corresponding JOD object.
     *
     * @param params the action's to params.
     */
    void execAction(JSLActionParams params);

}
