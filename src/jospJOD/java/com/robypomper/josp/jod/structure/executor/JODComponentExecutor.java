package com.robypomper.josp.jod.structure.executor;

import com.robypomper.josp.jod.structure.JODComponent;


/**
 * Class for messaging data to the {@link com.robypomper.josp.jod.executor.JODExecutor}
 * creation.
 * <p>
 * His implementation is provided by {@link JODComponentWorker} super class. The
 * aim of this class is just for type checks.
 */
public class JODComponentExecutor extends JODComponentWorker {

    /**
     * {@inheritDoc}
     * <p>
     * This constructor is Public only because of JOD Shell testing and creation
     * JODComponentExecutor creation.
     */
    public JODComponentExecutor(JODComponent component, String name, String proto, String configsStr) {
        super(component, name, proto, configsStr);
    }

}
