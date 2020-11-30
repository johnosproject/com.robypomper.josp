package com.robypomper.josp.jcp.params.jslwb;

import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;

public class JOSPBooleanStateHtml extends JOSPComponentHtml {

    public final boolean state;
    public final String pathState;

    public JOSPBooleanStateHtml(JSLBooleanState state) {
        super(state);
        this.state = state.getState();
        this.pathState = APIJSLWBState.FULL_PATH_BOOL(objId, componentPath);
    }

}
