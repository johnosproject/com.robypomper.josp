package com.robypomper.josp.jod.structure.executor;

import com.robypomper.josp.jod.structure.JODComponent;


/**
 * Base class for messaging classes between structure and executor mngr JOD systems.
 * <p>
 * His sub classes are initialized from the JOD structure during
 * {@link JODComponent} creation and given to JOD executor manager for worker
 * (Puller, Listener or Executor) creation.
 */
public abstract class JODComponentWorker {

    // Internal vars

    private final JODComponent component;
    private final String name;
    private final String proto;
    private final String configsStr;


    // Constructor

    /**
     * Full args constructor.
     *
     * @param component  the {@link JODComponent} that created this JODComponentWorker.
     * @param name       the JODWorker's name.
     * @param proto      the JODWorker's protocol.
     * @param configsStr the JODWorker's configs string.
     */
    protected JODComponentWorker(JODComponent component, String name, String proto, String configsStr) {
        this.component = component;
        this.name = name;
        this.proto = proto;
        this.configsStr = configsStr;
    }


    // Getters

    /**
     * {@link JODComponent} that generated current JODComponentWorker.
     *
     * @return the owner {@link JODComponent}.
     */
    public JODComponent getComponent() {
        return component;
    }

    /**
     * JODWorker's name.
     *
     * @return the JODComponentWorker's name.
     */
    public String getName() {
        return name;
    }

    /**
     * JODWorker's protocol.
     *
     * @return the JODComponentWorker's protocol.
     */
    public String getProto() {
        return proto;
    }

    /**
     * JODWorker's configs string.
     *
     * @return the JODComponentWorker's configs string.
     */
    public String getConfigsStr() {
        return configsStr;
    }

}
