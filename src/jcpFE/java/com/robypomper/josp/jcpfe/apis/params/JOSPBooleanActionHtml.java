package com.robypomper.josp.jcpfe.apis.params;

import com.robypomper.josp.jcpfe.apis.paths.APIJCPFEAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;

public class JOSPBooleanActionHtml extends JOSPBooleanStateHtml {

    public final String pathSwitch;
    public final String pathTrue;
    public final String pathFalse;

    public JOSPBooleanActionHtml(JSLBooleanAction action) {
        super(action);
        this.pathSwitch = APIJCPFEAction.FULL_PATH_BOOL_SWITCH(objId, componentPath);
        this.pathTrue = APIJCPFEAction.FULL_PATH_BOOL_TRUE(objId, componentPath);
        this.pathFalse = APIJCPFEAction.FULL_PATH_BOOL_FALSE(objId, componentPath);
    }

}
