package com.robypomper.josp.jcp.params.fe;

import com.robypomper.josp.jcp.paths.fe.APIFEState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;

public class JOSPBooleanStateHtml extends JOSPComponentHtml {

    public final boolean state;
    public final String pathState;

    public JOSPBooleanStateHtml(JSLBooleanState state) {
        super(state);
        this.state = state.getState();
        this.pathState = APIFEState.FULL_PATH_BOOL(objId, componentPath);
    }

}
