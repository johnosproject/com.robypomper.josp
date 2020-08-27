package com.robypomper.josp.jcpfe.apis.params;

import com.robypomper.josp.jcpfe.apis.paths.APIJCPFEState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;

public class JOSPRangeStateHtml extends JOSPComponentHtml {

    public final double state;
    public final String pathState;

    public JOSPRangeStateHtml(JSLRangeState state) {
        super(state);
        this.state = state.getState();
        this.pathState = APIJCPFEState.FULL_PATH_RANGE(objId, componentPath);
    }

}