package com.robypomper.josp.jod.executor;


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
 * (from {@link #getServerLoop()} method) call the {@link #sendUpdate()} method
 * (without any param because not yet implemented). The frequency must be set via
 * configs string usign {@value #PROP_FREQUENCY} property.
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

    private int frequency = -1;
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
    public ListenerTestAdv(String name, String proto, String configsStr) throws MissingPropertyException {
        super(name, proto);
        System.out.println(String.format("JOD Listener init %s://%s.", proto, configsStr));

        for (String keyAndProp : configsStr.split(";")) {
            if (keyAndProp.indexOf('=') < 0) {
                // Boolean properties

            } else {
                // Key = Value properties
                String key = keyAndProp.substring(0, keyAndProp.indexOf('='));
                String value = keyAndProp.substring(keyAndProp.indexOf('=') + 1);
                if (key.compareToIgnoreCase(PROP_FREQUENCY) == 0)
                    try {
                        frequency = Integer.parseInt(value);
                    } catch (Exception e) {
                        throw new MissingPropertyException(PROP_FREQUENCY, getProto(), getName(), value, "Listener");
                    }
                else if (key.compareToIgnoreCase(PROP_SLEEP_TIME) == 0)
                    try {
                        sleepTime = Integer.parseInt(value);
                    } catch (Exception e) {
                        throw new MissingPropertyException(PROP_FREQUENCY, getProto(), getName(), value, "Listener");
                    }
            }
        }

        if (frequency == -1)
            throw new MissingPropertyException(PROP_FREQUENCY, getProto(), getName(), "Listener");
    }

    // Mngm

    /**
     * Server Loop method: print a log messages and start infinite loop where
     * call the {@link #sendUpdate()} method each {@link #frequency} (default: 10)
     * loop print a dot ('.') and call the {@link #sendUpdate()} method.
     * <p>
     * Each loop interaction start a {@link Thread#sleep(long)} for {@link #sleepTime}
     * millisecond (default: 10x1000).
     */
    @Override
    protected void getServerLoop() {
        System.out.println(String.format("JOD Listener server run %s://%s.", getProto(), getName()));

        int count = 0;
        while (!mustShoutingDown()) {
            count++;
            if (count % frequency == 0) {
                System.out.print(".");
                sendUpdate();
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                break;
            }
        }

        System.out.println(String.format("JOD Listener server terminated %s://%s.", getProto(), getName()));
    }

}
