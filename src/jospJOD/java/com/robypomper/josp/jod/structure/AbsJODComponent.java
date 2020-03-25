package com.robypomper.josp.jod.structure;

import com.robypomper.josp.jod.systems.JODStructure;

import java.util.Map;


/**
 * Default implementation of {@link JODComponent} interface.
 * <p>
 * In addition to the method defined by the interface, this class manage the
 * reference to the JOD Structure system. This reference is used by container
 * components to initialize other components. Also the state components use
 * this reference to propagate the status updates to the JOD Communication system.
 */
public class AbsJODComponent implements JODComponent {

    // Internal vars

    private final JODStructure structure;
    private JODContainer parent = null;
    private final String name;
    private final String descr;


    // Constructor

    /**
     * Default constructor that initialize component's commons properties.
     * <p>
     * Moreover it store the reference to the JOD Structure system.
     *
     * @param structure the JOD Structure system.
     * @param name      the name of the component.
     * @param descr     the description of the component.
     */
    public AbsJODComponent(JODStructure structure, String name, String descr) {
        this.structure = structure;
        this.name = name;
        this.descr = descr != null ? descr : "";
    }


    // Commons properties

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
    public String getDescr() {
        return descr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JODContainer getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JODComponentPath getPath() {
        if (parent == null)
            return new DefaultJODComponentPath(StructureDefinitions.PATH_STR_ROOT);
        return parent.getPath().add(getName());
    }


    // Implementation methods

    /**
     * Method called by {@link AbsJODContainer#setComponents(Map)} to set his self
     * as parent of the current component.
     *
     * <b>This method can be called only one time.</b> During their initialization,
     * {@link JODContainer} and his sub classes, call the setComponents(Map)
     * method and then this method.
     *
     * @param parent the container parent of current component.
     */
    public void setParent(JODContainer parent) throws JODStructure.ComponentInitException {
        if (this.parent != null)
            throw new JODStructure.ComponentInitException(String.format("Component '%s', can't set twice 'parent' property", getName()));

        this.parent = parent;
    }

    /**
     * @return the JOD Structure system.
     */
    protected JODStructure getStructure() {
        return structure;
    }

}