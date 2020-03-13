package com.robypomper.josp.jod.executor.factories;

import com.robypomper.josp.jod.executor.AbsJODPuller;

/**
 * JOD Puller Factory.
 */
public class FactoryJODPuller extends AbsFactoryJODWorker<AbsJODPuller> {

    // Internal vars

    private static FactoryJODPuller instance = null;


    // Constructor

    /**
     * Private constructor because this class is singleton.
     */
    private FactoryJODPuller() {}

    /**
     * @return the singleton instance of this class.
     */
    public static FactoryJODPuller instance() {
        if (instance == null)
            instance = new FactoryJODPuller();
        return instance;
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "Puller";
    }

}
