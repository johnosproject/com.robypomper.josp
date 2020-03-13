package com.robypomper.josp.jod.executor;

import com.robypomper.josp.jod.structure.JODComponent;


/**
 * Default basic implementation class for JOD executor manager worker representations.
 */
public abstract class AbsJODWorker implements JODWorker {

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

}
