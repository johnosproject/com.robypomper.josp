package com.robypomper.josp.jod.executor;

import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.pillars.JODBooleanState;
import com.robypomper.log.Mrk_JOD;

import java.util.Map;


/**
 * JOD Listener advanced test.
 * <p>
 * This test show how to initialise current Listener using properties from
 * <code>configStr</code> constructor's param. This string is set by the user and
 * contain all properties required to the Listener. If some property is missing,
 * a {@link com.robypomper.josp.jod.executor.JODWorker.MissingPropertyException}
 * will throw from the Listener constructor (and catch from the FactoryJODListener).
 * <p>
 * Each {@link #frequency} interactions of the server's infinite loop
 * (from {@link #getServerLoop()} method) call the
 * {@link JODState} sub class's <code>setUpdate(...)</code> method.
 * <p>
 * The sleeping time between each loop interaction can be set via
 * {@value #PROP_SLEEP_TIME} configs string (default = 1000ms).
 * <p>
 * Print log messages, from dedicated thread, on server startup and shutdown.
 */
public class ListenerTestAdv extends AbsJODListenerLoop {

    // Class constants

    private static final String PROP_FREQUENCY = "frequency";
    private static final String PROP_SLEEP_TIME = "sleep";


    // Internal vars

    private int frequency = 1;
    private int sleepTime = 1000;


    // Constructor

    /**
     * Default ListenerTest constructor.
     *
     * @param name       name of the listener.
     * @param proto      proto of the listener.
     * @param configsStr configs string, parse {@value #PROP_FREQUENCY}(int) and
     *                   {@value #PROP_SLEEP_TIME}(int)properties.
     */
    public ListenerTestAdv(String name, String proto, String configsStr) throws MissingPropertyException, ParsingPropertyException {
        super(name, proto);
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerTestAdv for component '%s' init with config string '%s://%s'", getName(), proto, configsStr));

        Map<String, String> properties = splitConfigsStrings(configsStr);
        frequency = parseConfigInt(properties, PROP_FREQUENCY, frequency);
        sleepTime = parseConfigInt(properties, PROP_SLEEP_TIME, sleepTime);
    }


    // Mngm

    /**
     * Server Loop method: print a log messages and start infinite loop where
     * each {@link #frequency} (default: 10) interactions call the
     * {@link JODState} sub class's <code>setUpdate(...)</code> method.
     * <p>
     * Each loop interaction start a {@link Thread#sleep(long)} for {@link #sleepTime}
     * millisecond (default: 10x1000).
     */
    @Override
    protected void getServerLoop() {
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerTestAdv for component '%s' with frequency='%d' and sleepTime='%d'ms", getName(), frequency, sleepTime));

        int count = 0;
        while (!mustShoutingDown()) {
            count++;
            if (count % frequency == 0) {
                log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerTestAdv for component '%s' of proto '%s' listened", getName(), getProto()));

                // For each JODState supported
                if (getComponent() instanceof JODBooleanState)
                    ((JODBooleanState) getComponent()).setUpdate(true);

            }
            try {
                //noinspection BusyWait
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                break;
            }
        }

        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerTestAdv for component '%s' terminated", getName()));
    }

}
