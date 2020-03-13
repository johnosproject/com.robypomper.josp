package com.robypomper.josp.jod.executor;


/**
 * Default Executor class used by {@link JODExecutor} implementations.
 */
public abstract class AbsJODExecutor extends AbsJODWorker implements JODExecutor {

    // Internal vars

    private boolean enabled = false;


    // Constructor

    /**
     * {@inheritDoc}
     */
    public AbsJODExecutor(String name, String proto) {
        super(name, proto);
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }


    // JODExecutor's impl

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exec() {
        System.out.println(String.format("DEB: Exec '%s' JOD Executor action.", getName()));
        if (!enabled)
            return false;

        return subExec();
    }

    protected abstract boolean subExec();


    // Mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public void enable() {
        System.out.println(String.format("DEB: Enable '%s' JOD Executor action.", getName()));
        if (isEnabled()) return;

        enabled = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disable() {
        System.out.println(String.format("DEB: Disable '%s' JOD Executor action.", getName()));
        if (!isEnabled()) return;

        enabled = false;
    }
}
