package com.robypomper.josp.jod.structure;


/**
 * Base component representation.
 * <p>
 * This interface collect all components commons properties such as name,
 * description, etc...
 */
public interface JODComponent {

    // Commons properties

    /**
     * @return the name of the component.
     */
    String getName();

    /**
     * @return the description of the component.
     */
    String getDescr();

    /**
     * @return the path of the component (it will be unique path).
     */
    JODComponentPath getPath();

    /**
     * @return the parent of the component.
     */
    JODContainer getParent();

}
