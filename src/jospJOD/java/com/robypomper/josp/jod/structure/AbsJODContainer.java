package com.robypomper.josp.jod.structure;

import com.robypomper.josp.jod.systems.JODExecutorMngr;
import com.robypomper.josp.jod.systems.JODStructure;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Default implementation of {@link JODContainer} interface.
 * <p>
 * In addition to the method defined by the interface, this class manage the
 * reference to the JOD Executor Mngr system. This reference is used by container
 * components to initialize state and action components. Because the state
 * components require this reference to propagate the status updates to the
 * JOD Communication system.
 * <p>
 * Moreover, this implementation provide a set of method to create components.
 * This methods helps {@link JODContainer} and {@link JODRoot} sub classes to
 * implement custom initialization procedures (p.e.: for different source formats
 * like json, xml...; actually the json implementation is provided by the
 * {@link JODRoot_Jackson} class).
 */
public class AbsJODContainer extends AbsJODComponent
        implements JODContainer {

    // Internal vars

    private Map<String, JODComponent> components = null;
    private JODExecutorMngr executorMngr = null;


    // Constructor

    /**
     * Protected constructor used by {@link JODRoot} component and others
     * {@link JODContainer} sub classes.
     * <p>
     * When this constructor is used, the sub class must call also the
     * {@link #setComponents(Map)} method.
     *
     * @param structure the JOD Structure system.
     * @param execMngr  the JOD Executor Mngr system.
     * @param name      the name of the component.
     * @param descr     the description of the component.
     */
    protected AbsJODContainer(JODStructure structure, JODExecutorMngr execMngr, String name, String descr) {
        super(structure, name, descr);
        this.executorMngr = execMngr;
    }


    /**
     * Default constructor that initialize the container with his properties and
     * the children components list.
     *
     * @param structure the JOD Structure system.
     * @param name      the name of the component.
     * @param descr     the description of the component.
     * @param subComps  the list of container's sub components.
     */
    public AbsJODContainer(JODStructure structure, String name, String descr, Map<String, JODComponent> subComps) {
        super(structure, name, descr);

        try {
            setComponents(subComps);
        } catch (JODStructure.ComponentInitException e) {
            assert false;   // This should be implementation error
        }
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return StructureDefinitions.TYPE_CONTAINER;
    }


    // Sub components

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<JODComponent> getComponents() {
        return components != null ? components.values() : Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JODComponent getComponentByName(String name) {
        return components != null ? components.get(name) : null;
    }


    // Implementation methods

    /**
     * Set sub components list.
     *
     * <b>This method can be called only one time.</b> During their initialization,
     * {@link JODContainer} and his sub classes, call this method. Then for each
     * sub component in the list, the {@link AbsJODComponent#setParent(JODContainer)}
     * method is called.
     * <p>
     * Protected method used by {@link AbsJODContainer} sub classes in conjunction
     * with the protected {@link AbsJODContainer#AbsJODContainer(JODStructure, JODExecutorMngr, String, String)}
     * constructor.
     */
    protected void setComponents(Map<String, JODComponent> subComps) throws JODStructure.ComponentInitException {
        if (this.components != null)
            throw new JODStructure.ComponentInitException(String.format("Component '%s', can't set twice 'contains' property.", getName()));

        this.components = subComps != null ? subComps : new HashMap<>();

        for (JODComponent comp : components.values()) {
            assert comp instanceof AbsJODComponent;
            ((AbsJODComponent) comp).setParent(this);
        }
    }

    /**
     * @return the JOD Executor Mngr system.
     */
    protected JODExecutorMngr getExecutorMngr() {
        return executorMngr;
    }


    // Component initialization

    /**
     * Create a component of <code>compType</code> type and the other properties.
     *
     * @param compType     the type of the component to create.
     * @param compName     the name of the component to create.
     * @param compSettings the key-value pairs of the component properties.
     * @return the created component.
     */
    protected JODComponent createComponent(String compType, String compName, Map<String, Object> compSettings) throws JODStructure.ParsingException {
        if (StructureDefinitions.TYPE_JOD_CONTAINER.compareToIgnoreCase(compType) == 0)
            return createContainer(compName, compSettings);

        if (StructureDefinitions.TYPE_JOD_STATE.compareToIgnoreCase(compType) == 0)
            return createState(compName, compSettings);

        if (StructureDefinitions.TYPE_JOD_ACTION.compareToIgnoreCase(compType) == 0)
            return createAction(compName, compSettings);

        throw new JODStructure.ParsingUnknownTypeException(compType, compName);
    }

    /**
     * Create a state component.
     *
     * @param compName     the name of the component to create.
     * @param compSettings the key-value pairs of the component properties.
     * @return the created state component.
     */
    protected JODState createState(String compName, Map<String, Object> compSettings) throws JODStructure.InstantiationParsedDataException {
        String descr = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_DESCR);
        String listener = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_LISTNER);
        String puller = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_PULLER);

        try {
            return new AbsJODState(getStructure(), getExecutorMngr(), compName, descr, listener, puller);
        } catch (JODStructure.ComponentInitException e) {
            throw new JODStructure.InstantiationParsedDataException(compName, listener, puller, e);
        }
    }

    /**
     * Create an action component.
     *
     * @param compName     the name of the component to create.
     * @param compSettings the key-value pairs of the component properties.
     * @return the created state component.
     */
    protected JODAction createAction(String compName, Map<String, Object> compSettings) throws JODStructure.InstantiationParsedDataException {
        String descr = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_DESCR);
        String listener = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_LISTNER);
        String puller = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_PULLER);
        String executor = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_EXECUTOR);

        try {
            return new AbsJODAction(getStructure(), getExecutorMngr(), compName, descr, listener, puller, executor);
        } catch (JODStructure.ComponentInitException e) {
            throw new JODStructure.InstantiationParsedDataException(compName, listener, puller, executor, e);
        }
    }

    /**
     * Create a container component.
     *
     * @param compName     the name of the component to create.
     * @param compSettings the key-value pairs of the component properties.
     * @return the created state component.
     */
    protected JODContainer createContainer(String compName, Map<String, Object> compSettings) throws JODStructure.ParsingException {
        String descr = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_DESCR);

        @SuppressWarnings("unchecked")
        Map<String, Object> contains = (Map<String, Object>) compSettings.get("contains");
        Map<String, JODComponent> subComps = createFromContains(contains);

        return new AbsJODContainer(getStructure(), compName, descr, subComps);
    }

    /**
     * Create all components contained in the <code>subComps</code> map.
     * <p>
     * The given map contains the pairs name/<code>compProps</code> for each
     * component to create. In the <code>compProps</code> there must be present
     * the {@value StructureDefinitions#PROP_COMPONENT_TYPE} property, used to
     * define witch component type to create.
     * <p>
     * The <code>compProps</code> must be a {@link Map} of {@link String}-{@link Object}s.
     *
     * @param comps the map containing the pairs name/comp's properties.
     * @return a map containing the pairs name/component.
     */
    protected Map<String, JODComponent> createFromContains(Map<String, Object> comps) throws JODStructure.ParsingException {
        Map<String, JODComponent> components = new HashMap<>();
        for (Map.Entry<String, Object> compJson : comps.entrySet()) {
            String compName = compJson.getKey();
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> compSettings = (Map<String, Object>) compJson.getValue();
                String compType = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_TYPE);
                JODComponent compInstance = createComponent(compType, compName, compSettings);
                components.put(compName, compInstance);

            } catch (Exception e) {
                throw new JODStructure.ParsingException(String.format("---malformed component %s---", compName), e);
            }
        }
        return components;
    }

}
