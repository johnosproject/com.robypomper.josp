package com.robypomper.josp.jcp.params.fe;

import com.robypomper.josp.jcp.paths.fe.APIFEState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;

public class JOSPRangeStateHtml extends JOSPComponentHtml {

    public final double state;
    public final String pathState;
    public final double max;
    public final double min;
    public final double step;

    public JOSPRangeStateHtml(JSLRangeState state) {
        super(state);
        this.state = state.getState();
        this.pathState = APIFEState.FULL_PATH_RANGE(objId, componentPath);
        this.max = state.getMax();
        this.min = state.getMin();
        this.step = state.getStep();
    }

}
