package com.robypomper.josp.jsl.objs.structure;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.josp.protocol.JOSPStateUpdateParams;

import java.util.HashMap;
import java.util.Map;


/**
 * Default implementation of {@link JSLState} interface.
 */
public abstract class AbsJSLState extends AbsJSLComponent
        implements JSLState {

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
    public AbsJSLState(JSLRemoteObject remoteObject, String name, String descr, String type) {
        super(remoteObject, name, descr, type);
    }


    // Status classes

    private static final Map<String, Class<? extends JOSPStateUpdateParams>> stateClasses = new HashMap<>();

    public static void loadAllStateClasses() {
        registerStateClass(JSLBooleanState.JOSPBoolean.class.getSimpleName(), JSLBooleanState.JOSPBoolean.class);
        registerStateClass(JSLRangeState.JOSPRange.class.getSimpleName(), JSLRangeState.JOSPRange.class);
    }

    public static void registerStateClass(String typeName, Class<? extends JOSPStateUpdateParams> cl) {
        stateClasses.put(typeName, cl);
    }

    public static Map<String, Class<? extends JOSPStateUpdateParams>> getStateClasses() {
        return stateClasses;
    }

}
