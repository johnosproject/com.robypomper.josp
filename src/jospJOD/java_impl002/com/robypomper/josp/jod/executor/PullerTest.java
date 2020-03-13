package com.robypomper.josp.jod.executor;


/**
 * JOD Puller test.
 * <p>
 * Each time the {@link #pull()} method is called, it print a log message and call
 * the {@link #sendUpdate()} method (without any param because not yet implemented).
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
        System.out.println(String.format("JOD Puller init %s://%s.", proto, configsStr));
    }

    // Mngm

    /**
     * Pull method: print a log message and call the {@link #sendUpdate()} method.
     */
    @Override
    public void pull() {
        System.out.println(String.format("JOD Puller pull %s://%s.", getProto(), getName()));
        sendUpdate();
    }

}
