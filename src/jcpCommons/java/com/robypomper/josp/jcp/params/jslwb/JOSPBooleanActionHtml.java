package com.robypomper.josp.jcp.params.jslwb;

import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;

public class JOSPBooleanActionHtml extends JOSPBooleanStateHtml {

    public final String pathSwitch;
    public final String pathTrue;
    public final String pathFalse;

    public JOSPBooleanActionHtml(JSLBooleanAction action) {
        super(action);
        this.pathSwitch = APIJSLWBAction.FULL_PATH_BOOL_SWITCH(objId, componentPath);
        this.pathTrue = APIJSLWBAction.FULL_PATH_BOOL_TRUE(objId, componentPath);
        this.pathFalse = APIJSLWBAction.FULL_PATH_BOOL_FALSE(objId, componentPath);
    }

}
