package com.robypomper.josp.jod.systems;


/**
 * Interface for Object's info system.
 * <p>
 * This system collect all object's info and provide them to other JOD's systems.
 * <p>
 * JODObjectInfo implementations can access to the JCP API and JOD settings file
 * to load and store values of Object's info (for example the Hardware ID must
 * be generated from the JCP APIs Object and stored on local settings file).
 */
public interface JODObjectInfo {

    // Object's systems

    /**
     * Set object's systems so ObjectInfo can access to systems info and provide
     * them as a unique point of reference.
     *
     * @param structure   the JOD structure system.
     * @param executor    the JOD executor system.
     * @param comm        the JOD communication system.
     * @param permissions the JOD permissions system.
     */
    void setSystems(JODStructure structure, JODExecutorMngr executor, JODCommunication comm, JODPermissions permissions);


    // Obj's info

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


    // Users's info

    /**
     * The object owner's User ID.
     *
     * @return owner's User ID.
     */
    String getOwnerId();


    // Structure's info

    /**
     * The object's structure definition in a String object.
     *
     * @return object's structure definition.
     */
    String getStructureStr();

    /**
     * The object's brand.
     *
     * @return object's brand.
     */
    String getBrand();

    /**
     * The object's model.
     *
     * @return object's model.
     */
    String getModel();

    /**
     * The object's description.
     *
     * @return object's description.
     */
    String getLongDescr();


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
