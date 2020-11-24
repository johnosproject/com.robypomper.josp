package com.robypomper.josp.jcp.params.fe;

import com.robypomper.josp.jcp.paths.fe.APIFEAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;

public class JOSPBooleanActionHtml extends JOSPBooleanStateHtml {

    public final String pathSwitch;
    public final String pathTrue;
    public final String pathFalse;

    public JOSPBooleanActionHtml(JSLBooleanAction action) {
        super(action);
        this.pathSwitch = APIFEAction.FULL_PATH_BOOL_SWITCH(objId, componentPath);
        this.pathTrue = APIFEAction.FULL_PATH_BOOL_TRUE(objId, componentPath);
        this.pathFalse = APIFEAction.FULL_PATH_BOOL_FALSE(objId, componentPath);
    }

}
