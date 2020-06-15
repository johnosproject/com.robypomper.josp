package com.robypomper.josp.jsl.objs.structure;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPStateUpdateParams;

import java.util.HashMap;
import java.util.Map;


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
    public AbsJSLState(JSLRemoteObject remoteObject, String name, String descr, String type) {
        super(remoteObject, name, descr, type);
    }


    // Status classes

    private static final Map<String, Class<? extends JOSPStateUpdateParams>> stateClasses = new HashMap<>();

    public static void loadAllStateClasses() {
        registerStateClass(JOSPIntTest.class.getSimpleName(), JOSPIntTest.class);
    }

    public static void registerStateClass(String typeName, Class<? extends JOSPStateUpdateParams> cl) {
        stateClasses.put(typeName, cl);
    }

    public static Map<String, Class<? extends JOSPStateUpdateParams>> getStateClasses() {
        return stateClasses;
    }


    // Temporary: this methods must be defined and implemented by AbsJSLState sub classes
    private int state;

    public int getState() {
        return state;
    }

    @Override
    public boolean updateStatus(JOSPProtocol.StatusUpd statusUpd) {
        if (statusUpd.getUpdate() instanceof JOSPIntTest) {
            JOSPIntTest stateUpdate = (JOSPIntTest) statusUpd.getUpdate();
            state = stateUpdate.newState;
            System.out.printf("\n\nReceived status update from %s::%s (obj::component)%n", statusUpd.getObjectId(), statusUpd.getComponentPath());
            System.out.printf("\tnewState %d%n", stateUpdate.newState);
            System.out.printf("\toldState %d%n", stateUpdate.oldState);
            return true;
        }
        return false;
    }


    // Temporary: this class must be defined and implemented by AbsJSLState sub classes
    public static class JOSPIntTest implements JSLStateUpdate {

        public final int newState;
        public final int oldState;

        public JOSPIntTest(String updData) {
            String[] lines = updData.split("\n");

            newState = Integer.parseInt(lines[0].substring(lines[0].indexOf(":") + 1));
            oldState = Integer.parseInt(lines[1].substring(lines[1].indexOf(":") + 1));
        }

        @Override
        public String getType() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String encode() {
            throw new RuntimeException("JSL JOSPIntTest::encode() method must be NOT called");
        }

    }

}
