package com.robypomper.josp.jod.executor;

import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.pillars.JODBooleanState;
import com.robypomper.josp.jod.structure.pillars.JODRangeState;
import com.robypomper.log.Mrk_JOD;


/**
 * JOD Listener test.
 * <p>
 * Each interaction of the server's infinite loop (from {@link #getServerLoop()}
 * method) call the {@link JODState} sub class's <code>setUpdate(...)</code>
 * method each 10 seconds (10.000ms).
 * <p>
 * Print log messages, from dedicated thread, on server startup and shutdown.
 */
public class ListenerTest extends AbsJODListenerLoop {

    // Constructor

    /**
     * Default ListenerTest constructor.
     *
     * @param name       name of the listener.
     * @param proto      proto of the listener.
     * @param configsStr configs string, can be an empty string.
     */
    public ListenerTest(String name, String proto, String configsStr) {
        super(name, proto);
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerTest for component '%s' init with config string '%s://%s'", getName(), proto, configsStr));
    }


    // Mngm

    /**
     * Server Loop method: print a log messages and start infinite loop where
     * call the {@link JODState} sub class's <code>setUpdate(...)</code> method
     * each 10 seconds (10.000ms).
     */
    @Override
    protected void getServerLoop() {
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerTest for component '%s' of proto '%s' running", getName(), getProto()));

        while (!mustShoutingDown()) {
            log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerTest for component '%s' of proto '%s' listened", getName(), getProto()));

            // For each JODState supported
            if (getComponent() instanceof JODBooleanState)
                ((JODBooleanState) getComponent()).setUpdate(true);
            else if (getComponent() instanceof JODRangeState)
                ((JODRangeState) getComponent()).setUpdate(5);

            try {
                //noinspection BusyWait
                Thread.sleep(1000 * 10);
            } catch (InterruptedException e) {
                break;
            }
        }

        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerTest for component '%s' terminated", getName()));
    }

}
