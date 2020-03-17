package com.robypomper.josp.jod.executor;

import com.robypomper.josp.jod.structure.JODComponent;


/**
 * Default basic implementation class for JOD executor manager worker representations.
 */
public abstract class AbsJODWorker implements JODWorker {

    // Class constants

    public static final String CONFIG_STR_SEP = "://";

    // Internal vars

    private final String proto;
    private final String name;
    private JODComponent component;


    // Constructor

    /**
     * Default constructor.
     *
     * @param name  the JODWorker's name.
     * @param proto the JODWorker's protocol.
     */
    public AbsJODWorker(String name, String proto) {
        this.proto = proto;
        this.name = name;
    }


    // Setter

    /**
     * Set the {@link JODComponent} associated to the current JODWorker.
     * <p>
     * The owner component must be set via this method, and not via constructor,
     * because the implementation classes must not access to JODComponent
     * directly.
     * <p>
     * NB: the component owner can be set only once.
     *
     * @param component the owner {@link JODComponent}.
     */
    public void setComponent(JODComponent component) {
        if (this.component != null)
            throw new IllegalStateException(String.format("JOD Worker '%s' already have a JOD Component owner.", getName()));
        this.component = component;
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProto() {
        return proto;
    }


    // To JODComponent method

    /**
     * Method called from {@link JODPuller} and {@link JODListener} to send a
     * status update to associated {@link JODComponent}.
     */
    protected void sendUpdate() {
        // ToDo: implements flow Send Update (Execution > JODComponent)
        System.out.println("WAR: Flow Send Update not yet implemented");
        //component.sendUpdate...
    }


    // Full config string mngm

    /**
     * Return the protocol part of the worker fullConfigs string.
     *
     * @param fullConfigs the worker full configs string.
     * @return the protocol defined in given full configs string.
     */
    public static String extractProto(String fullConfigs) {
        return fullConfigs.substring(0, fullConfigs.indexOf(CONFIG_STR_SEP)).trim();
    }

    /**
     * Return the configs/name part of the worker fullConfigs string.
     *
     * @param fullConfigs the worker full configs string.
     * @return the configs/name defined in given full configs string.
     */
    public static String extractConfigsStr(String fullConfigs) {
        return fullConfigs.substring(fullConfigs.indexOf(CONFIG_STR_SEP) + 3).trim();
    }

    /**
     * Compose the two part of the full configs string.
     *
     * @param proto  the string used as protocol part of the full configs string.
     * @param config the string used as congis/name part of the full configs string.
     * @return the composed full configs string.
     */
    public static String mergeConfigsStr(String proto, String config) {
        return proto + CONFIG_STR_SEP + config;
    }

}
