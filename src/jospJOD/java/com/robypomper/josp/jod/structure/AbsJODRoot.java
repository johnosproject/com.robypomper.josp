package com.robypomper.josp.jod.structure;

import com.robypomper.josp.jod.executor.JODExecutorMngr;


/**
 * Basic root component implementation.
 * <p>
 * This class implement the {@link JODRoot} interface and is used as a base class
 * for root parser classes. That means the {@link AbsJODRoot} sub classes are
 * used to initialize object's structures. This technique allow to initialize
 * an object structure from a stored structure definition (like text file).
 * moreover different sub classes can support different sources and formats (json,
 * xml...).
 * <p>
 * The double constructor and the setters methods give more flexibility on root
 * initialization to his sub classes.
 */
public class AbsJODRoot extends AbsJODContainer
        implements JODRoot {

    // Internal vars

    private String model;
    private String brand;
    private String descr_long;


    // Constructor

    /**
     * Protected constructor, that delegate the sub classes to set the root
     * component properties.
     *
     * @param structure the JOD Structure system.
     * @param execMngr  the JOD Executor Mngr system.
     */
    protected AbsJODRoot(JODStructure structure, JODExecutorMngr execMngr) {
        super(structure, execMngr, StructureDefinitions.ROOT_NAME, StructureDefinitions.ROOT_DESCR);
    }

    /**
     * Default constructor that set all root component properties.
     *
     * @param structure the JOD Structure system.
     * @param execMngr  the JOD Executor Mngr system.
     * @param model     the object's model.
     * @param brand     the object's brand.
     * @param descrLong the object's long description.
     */
    public AbsJODRoot(JODStructure structure, JODExecutorMngr execMngr, String model, String brand, String descrLong) {
        this(structure, execMngr);
        setModel(model);
        setBrand(brand);
        setDescr_long(descrLong);
    }


    // Root's properties

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModel() {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBrand() {
        return brand;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescr_long() {
        return descr_long;
    }


    // Implementation methods

    /**
     * Set object's model.
     *
     * @param model the object's model.
     */
    protected void setModel(String model) {
        this.model = model;
    }

    /**
     * Set object's brand.
     *
     * @param brand the object's brand.
     */
    protected void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Set object's long description.
     *
     * @param descrLong the object's long description.
     */
    protected void setDescr_long(String descrLong) {
        this.descr_long = descrLong;
    }

}
