package com.robypomper.josp.jsl.objs.structure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;


/**
 * Base component representation.
 * <p>
 * This interface collect all components commons properties such as name,
 * description, etc...
 */
public interface JSLComponent {

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
    JSLComponentPath getPath();

    /**
     * @return the remote object of the component.
     */
    JSLRemoteObject getRemoteObject();

    /**
     * @return the parent of the component.
     */
    @JsonIgnore
    JSLContainer getParent();

    /**
     * @return the component type.
     */
    String getType();

}
