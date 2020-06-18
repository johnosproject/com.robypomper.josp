package com.robypomper.josp.jod.structure;

import com.robypomper.josp.jod.structure.pillars.JODBooleanAction;
import com.robypomper.josp.protocol.JOSPActionCommandParams;

import java.util.HashMap;
import java.util.Map;


public class AbsJODAction {

    // Actions classes

    private static final Map<String, Class<? extends JOSPActionCommandParams>> actionClasses = new HashMap<>();

    public static void loadAllActionClasses() {
        registerActionClass(JODBooleanAction.JOSPBoolean.class.getSimpleName(), JODBooleanAction.JOSPBoolean.class);
    }

    public static void registerActionClass(String typeName, Class<? extends JOSPActionCommandParams> cl) {
        actionClasses.put(typeName, cl);
    }

    public static Map<String, Class<? extends JOSPActionCommandParams>> getActionClasses() {
        return actionClasses;
    }

}
