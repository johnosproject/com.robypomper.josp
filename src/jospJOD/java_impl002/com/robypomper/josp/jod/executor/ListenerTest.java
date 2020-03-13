package com.robypomper.josp.jod.executor;


/**
 * JOD Listener test.
 * <p>
 * Each interaction of the server's infinite loop (from {@link #getServerLoop()}
 * method) call the {@link #sendUpdate()} method (without any param because not
 * yet implemented).
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
        System.out.println(String.format("JOD Listener init %s://%s.", proto, configsStr));
    }

    // Mngm

    /**
     * Server Loop method: print a log messages and start infinite loop where
     * call the {@link #sendUpdate()} method each 10 seconds (10.000ms).
     */
    @Override
    protected void getServerLoop() {
        System.out.println(String.format("JOD Listener server run %s://%s.", getProto(), getName()));

        while (!mustShoutingDown()) {
            sendUpdate();
            try {
                Thread.sleep(1000 * 10);
            } catch (InterruptedException e) {
                break;
            }
        }

        System.out.println(String.format("JOD Listener server terminated %s://%s.", getProto(), getName()));
    }

}
