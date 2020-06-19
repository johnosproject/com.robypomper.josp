package com.robypomper.josp.jsl.objs.structure.pillars;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.AbsJSLState;
import com.robypomper.josp.jsl.objs.structure.JSLStateUpdate;
import com.robypomper.josp.protocol.JOSPProtocol;

public class JSLBooleanState extends AbsJSLState {

    // Internal vars

    private boolean state;


    // Constructor

    /**
     * Default constructor that initialize the status component with his
     * properties.
     *
     * @param remoteObject the {@link JSLRemoteObject} representing JOD object.
     * @param name         the name of the component.
     * @param descr        the description of the component.
     * @param type         the type of the component.
     */
    public JSLBooleanState(JSLRemoteObject remoteObject, String name, String descr, String type) {
        super(remoteObject, name, descr, type);
    }


    // Status's properties

    public boolean getState() {
        return state;
    }

    @Override
    public boolean updateStatus(JOSPProtocol.StatusUpd statusUpd) {
        if (statusUpd.getUpdate() instanceof JOSPBoolean) {
            JOSPBoolean stateUpdate = (JOSPBoolean) statusUpd.getUpdate();
            state = stateUpdate.newState;
            System.out.printf("\n\nReceived status update from %s::%s (obj::component)%n", statusUpd.getObjectId(), statusUpd.getComponentPath());
            System.out.printf("\tnewState %b%n", stateUpdate.newState);
            System.out.printf("\toldState %b%n", stateUpdate.oldState);
            return true;
        }
        return false;
    }


    // Boolean StateUpdate implementation

    public static class JOSPBoolean implements JSLStateUpdate {

        public final boolean newState;
        public final boolean oldState;

        public JOSPBoolean(String updData) {
            String[] lines = updData.split("\n");

            newState = Boolean.parseBoolean(lines[0].substring(lines[0].indexOf(":") + 1));
            oldState = Boolean.parseBoolean(lines[1].substring(lines[1].indexOf(":") + 1));
        }

        @Override
        public String getType() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String encode() {
            throw new RuntimeException("JSL JOSPBoolean::encode() method must be NOT called");
        }

    }

}