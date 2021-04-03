package com.robypomper.josp.jcp.params_DEPRECATED.jslwb;

import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.states.Paths20;
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
        this.pathState = Paths20.FULL_PATH_RANGE(objId, componentPath);
        this.max = state.getMax();
        this.min = state.getMin();
        this.step = state.getStep();
    }

}
