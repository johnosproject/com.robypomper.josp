package com.robypomper.josp.jod.executor;


/**
 * Default Listener class used by {@link JODListener} implementations.
 */
public abstract class AbsJODListener extends AbsJODWorker implements JODListener {

    // Class's constants

    public static final int DEF_JOIN_TIME = 1000;


    // Constructor

    /**
     * {@inheritDoc}
     */
    public AbsJODListener(String name, String proto) {
        super(name, proto);
    }

}
