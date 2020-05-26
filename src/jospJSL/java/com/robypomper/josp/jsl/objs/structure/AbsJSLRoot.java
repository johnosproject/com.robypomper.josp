package com.robypomper.josp.jsl.objs.structure;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;


/**
 * Basic root component implementation.
 * <p>
 * This class implement the {@link JSLRoot} interface and is used as a base class
 * for root parser classes. That means the {@link AbsJSLRoot} sub classes are
 * used to initialize object's structures. This technique allow to initialize
 * an object structure from a stored structure definition (like text file).
 * moreover different sub classes can support different sources and formats (json,
 * xml...).
 * <p>
 * The double constructor and the setters methods give more flexibility on root
 * initialization to his sub classes.
 */
public class AbsJSLRoot extends AbsJSLContainer
        implements JSLRoot {

    // Internal vars

    private String model;
    private String brand;
    private String descr_long;


    // Constructor

    /**
     * Protected constructor, that delegate the sub classes to set the root
     * component properties.
     *
     * @param remoteObject the {@link JSLRemoteObject} representing JOD object.
     */
    protected AbsJSLRoot(JSLRemoteObject remoteObject) {
        super(remoteObject, StructureDefinitions.ROOT_NAME, StructureDefinitions.ROOT_DESCR, "");    // root type is set by jackson parser
    }

    /**
     * Default constructor that set all root component properties.
     *
     * @param remoteObject the {@link JSLRemoteObject} representing JOD object.
     * @param model        the object's model.
     * @param brand        the object's brand.
     * @param descrLong    the object's long description.
     */
    public AbsJSLRoot(JSLRemoteObject remoteObject, String model, String brand, String descrLong) {
        this(remoteObject);
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
