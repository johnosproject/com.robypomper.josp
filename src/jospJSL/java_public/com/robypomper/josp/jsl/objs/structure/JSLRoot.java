package com.robypomper.josp.jsl.objs.structure;


/**
 * Base root component representation.
 * <p>
 * The root component is a special container that add specific properties related
 * to the object they belong to.
 */
public interface JSLRoot extends JSLContainer {

    // Root's properties

    /**
     * Object's model is defined by object maker.
     *
     * @return the object's model.
     */
    String getModel();

    /**
     * Object's maker name.
     *
     * @return the object's brand.
     */
    String getBrand();

    /**
     * Object's 2nd description.
     * <p>
     * This description help end-user to understand what the object is and what
     * it can do for them.
     *
     * @return the object's long description.
     */
    String getDescr_long();

}
