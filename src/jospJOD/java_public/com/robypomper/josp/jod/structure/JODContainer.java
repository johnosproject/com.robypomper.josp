package com.robypomper.josp.jod.structure;

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
public interface JODContainer extends JODComponent {

    // Sub components

    /**
     * @return the list of all component's names contained in current container.
     */
    Collection<JODComponent> getComponents();

    /**
     * Search the <code>name</code> component in the current container components
     * list.
     *
     * @param name the name of searched component.
     * @return the contained component or null if no component correspond to
     * given <code>name</code>.
     */
    JODComponent getComponentByName(String name);

}
