package com.robypomper.josp.jod.executor.factories;

import com.robypomper.josp.jod.executor.AbsJODExecutor;


/**
 * JOD Executor Factory.
 */
public class FactoryJODExecutor extends AbsFactoryJODWorker<AbsJODExecutor> {

    // Internal vars

    private static FactoryJODExecutor instance = null;


    // Constructor

    /**
     * Private constructor because this class is singleton.
     */
    private FactoryJODExecutor() {}

    /**
     * @return the singleton instance of this class.
     */
    public static FactoryJODExecutor instance() {
        if (instance == null)
            instance = new FactoryJODExecutor();
        return instance;
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "Executor";
    }

}
