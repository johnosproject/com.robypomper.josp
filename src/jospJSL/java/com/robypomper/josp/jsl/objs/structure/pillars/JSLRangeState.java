package com.robypomper.josp.jsl.objs.structure.pillars;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.AbsJSLState;
import com.robypomper.josp.jsl.objs.structure.JSLStateUpdate;
import com.robypomper.josp.protocol.JOSPProtocol;

public class JSLRangeState extends AbsJSLState {

    // Internal vars

    private double state;
    private final double min;
    private final double max;
    private final double step;


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
    public JSLRangeState(JSLRemoteObject remoteObject, String name, String descr, String type, double min, double max, double step) {
        super(remoteObject, name, descr, type);
        this.min = min;
        this.max = max;
        this.step = step;
    }


    // Status's properties

    public double getState() {
        return state;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStep() {
        return step;
    }

    @Override
    public boolean updateStatus(JOSPProtocol.StatusUpd statusUpd) {
        if (statusUpd.getUpdate() instanceof JOSPRange) {
            JOSPRange stateUpdate = (JOSPRange) statusUpd.getUpdate();
            state = stateUpdate.newState;
            System.out.printf("\n\nReceived status update from %s::%s (obj::component)%n", statusUpd.getObjectId(), statusUpd.getComponentPath());
            System.out.printf("\tnewState %f%n", stateUpdate.newState);
            System.out.printf("\toldState %f%n", stateUpdate.oldState);
            return true;
        }
        return false;
    }


    // Boolean StateUpdate implementation

    public static class JOSPRange implements JSLStateUpdate {

        public final double newState;
        public final double oldState;

        public JOSPRange(String updData) {
            String[] lines = updData.split("\n");

            newState = Double.parseDouble(lines[0].substring(lines[0].indexOf(":") + 1));
            oldState = Double.parseDouble(lines[1].substring(lines[1].indexOf(":") + 1));
        }

        @Override
        public String getType() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String encode() {
            throw new RuntimeException("JSL JOSPRange::encode() method must be NOT called");
        }

    }

}
