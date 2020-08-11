package com.robypomper.josp.jcpfe.apis.params;

import com.robypomper.josp.jcpfe.apis.paths.APIJCPFEState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;

public class JOSPBooleanStateHtml extends JOSPComponentHtml {

    public final boolean state;
    public final String pathState;

    public JOSPBooleanStateHtml(JSLBooleanState state) {
        super(state);
        this.state = state.getState();
        this.pathState = APIJCPFEState.FULL_PATH_BOOL(objId, componentPath);
    }

}
