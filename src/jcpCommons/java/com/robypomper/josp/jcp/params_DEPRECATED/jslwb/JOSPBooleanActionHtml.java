package com.robypomper.josp.jcp.params_DEPRECATED.jslwb;

import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.actions.Paths20;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;

public class JOSPBooleanActionHtml extends JOSPBooleanStateHtml {

    public final String pathSwitch;
    public final String pathTrue;
    public final String pathFalse;

    public JOSPBooleanActionHtml(JSLBooleanAction action) {
        super(action);
        this.pathSwitch = Paths20.FULL_PATH_BOOL_SWITCH(objId, componentPath);
        this.pathTrue = Paths20.FULL_PATH_BOOL_TRUE(objId, componentPath);
        this.pathFalse = Paths20.FULL_PATH_BOOL_FALSE(objId, componentPath);
    }

}
