package com.robypomper.josp.jsl.objs.structure;

import java.util.Collection;


/**
 * Base container representation.
 * <p>
 * A container is a special component that can contain other components. This
 * component is used to group and organize components across the object's structure.
 * <p>
 * This interface define methods to access to the sub components: getting the
 * list of component's names or to get once by his name.
 */
public interface JSLContainer extends JSLComponent {

    // Sub components

    /**
     * @return the list of all component's names contained in current container.
     */
    Collection<JSLComponent> getComponents();

    /**
     * Search the <code>name</code> component in the current container components
     * list.
     *
     * @param name the name of searched component.
     * @return the contained component or null if no component correspond to
     * given <code>name</code>.
     */
    JSLComponent getComponentByName(String name);

}
