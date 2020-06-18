package com.robypomper.josp.jod.executor;

import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.pillars.JODBooleanState;
import com.robypomper.josp.jod.structure.pillars.JODRangeState;
import com.robypomper.log.Mrk_JOD;


/**
 * JOD Puller test.
 * <p>
 * Each time the {@link #pull()} method is called, it print a log message and call
 * the {@link JODState} sub class's <code>setUpdate(...)</code> method.
 */
public class PullerTest extends AbsJODPuller {

    // Constructor

    /**
     * Default PullerTest constructor.
     *
     * @param name       name of the puller.
     * @param proto      proto of the puller.
     * @param configsStr configs string, can be an empty string.
     */
    public PullerTest(String name, String proto, String configsStr) {
        super(name, proto);
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerTest for component '%s' init with config string '%s://%s'.", getName(), proto, configsStr));
    }


    // Mngm

    /**
     * Pull method: print a log message and call the {@link JODState} sub
     * class's <code>setUpdate(...)</code> method.
     */
    @Override
    public void pull() {
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerTest '%s' of proto '%s' pulling", getName(), getProto()));

        // For each JODState supported
        if (getComponent() instanceof JODBooleanState)
            ((JODBooleanState) getComponent()).setUpdate(true);
        else if (getComponent() instanceof JODRangeState)
            ((JODRangeState) getComponent()).setUpdate(5);

    }

}
