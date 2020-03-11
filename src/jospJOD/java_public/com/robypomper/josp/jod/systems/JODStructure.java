package com.robypomper.josp.jod.systems;

import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODComponentPath;
import com.robypomper.josp.jod.structure.JODRoot;


/**
 * Interface for Object's structure system.
 *
 * Implementation of this interface must provide methods to access and manage
 * the object's structure.
 *
 * This system is based on data parsing, then when activated with {@link #startAutoRefresh()}
 * this class periodically checks for data updates. Then, on updates, it notifies
 * connected services for the new object structure.
 */
public interface JODStructure {

    // JOD Component's interaction methods (from communication)

    /**
     * @return the object's Root component.
     */
    JODRoot getRoot();

    /**
     * @param path required component path.
     * @return component corresponding to given path, or null if not found.
     */
    JODComponent getComponent(JODComponentPath path);


    // Mngm methods

    /**
     * Start periodically checks on object's structure data file for new updates.
     */
    void startAutoRefresh();

    /**
     * Stop periodically checks on object's structure data file for new updates.
     */
    void stopAutoRefresh();

    /**
     * Load object's structure from data file.
     */
    void loadStructure();

    /**
     * Set the {@link JODCommunication} reference to the JODStructure object.
     *
     * This cross-system reference is required by the State Update Flow.
     *
     * @param comm the {@link JODCommunication} reference.
     */
    void setCommunication(JODCommunication comm);

}
