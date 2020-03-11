package com.robypomper.josp.jod.structure;

/**
 * Action component representation.
 */
public interface JODAction extends JODState {

    void execAction(JODActionParams params);

}
