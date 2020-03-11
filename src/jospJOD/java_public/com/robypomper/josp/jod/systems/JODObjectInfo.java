package com.robypomper.josp.jod.systems;

/**
 * Interface for Object's info system.
 *
 * This system collect all object's info and provide them to other JOD's systems.
 *
 * JODObjectInfo implementations can access to the JCP API and JOD settings file
 * to load and store values of Object's info (for example the hardware id must
 * be generated from the JCP APIs Object and stored on local settings file).
 */
public interface JODObjectInfo {

    // Info

    /**
     * The Object's ID is the main id used to identifiy the Object in the JOSP
     * Eco-System. When it be reset, then must re-register the Object.
     *
     * @return the object's ID.
     */
    String getObjId();

    /**
     * Human readable object's name.
     *
     * @return the object's name
     */
    String getObjName();

    /**
     * The Hardware ID is the id that allow to identify a physical object.
     *
     * It help to identify same physical object also when the Object ID is reset.
     *
     * @return the object's Hardware ID.
     */
    String getObjIdHw();


    // Mngm methods

    /**
     * Start periodically checks on settings file and JCP APIs Object for object's
     * info changes.
     */
    void startAutoRefresh();

    /**
     * Stop periodically checks on settings file and JCP APIs Object for object's
     * info changes.
     */
    void stopAutoRefresh();

}
