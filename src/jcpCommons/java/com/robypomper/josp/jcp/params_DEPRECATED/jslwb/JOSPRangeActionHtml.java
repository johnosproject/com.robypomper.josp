package com.robypomper.josp.jcp.params_DEPRECATED.jslwb;

import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.actions.Paths20;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;

public class JOSPRangeActionHtml extends JOSPRangeStateHtml {

    public final String pathSetValue;
    public final String pathInc;
    public final String pathDec;
    public final String pathMax;
    public final String pathMin;
    public final String pathSet1_2;
    public final String pathSet1_3;
    public final String pathSet2_3;

    public JOSPRangeActionHtml(JSLRangeAction action) {
        super(action);
        this.pathSetValue = Paths20.FULL_PATH_RANGE_SET(objId, componentPath);
        this.pathInc = Paths20.FULL_PATH_RANGE_INC(objId, componentPath);
        this.pathDec = Paths20.FULL_PATH_RANGE_DEC(objId, componentPath);
        this.pathMax = Paths20.FULL_PATH_RANGE_MAX(objId, componentPath);
        this.pathMin = Paths20.FULL_PATH_RANGE_MIN(objId, componentPath);
        this.pathSet1_2 = Paths20.FULL_PATH_RANGE_1_2(objId, componentPath);
        this.pathSet1_3 = Paths20.FULL_PATH_RANGE_1_3(objId, componentPath);
        this.pathSet2_3 = Paths20.FULL_PATH_RANGE_2_3(objId, componentPath);
    }

}