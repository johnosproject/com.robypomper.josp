package com.robypomper.josp.jod.executor.factories;

import com.robypomper.josp.jod.executor.AbsJODListener;


/**
 * JOD Listener Factory.
 */
public class FactoryJODListener extends AbsFactoryJODWorker<AbsJODListener> {

    // Internal vars

    private static FactoryJODListener instance = null;


    // Constructor

    /**
     * Private constructor because this class is singleton.
     */
    private FactoryJODListener() {}

    /**
     * @return the singleton instance of this class.
     */
    public static FactoryJODListener instance() {
        if (instance == null)
            instance = new FactoryJODListener();
        return instance;
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "Listener";
    }

}
