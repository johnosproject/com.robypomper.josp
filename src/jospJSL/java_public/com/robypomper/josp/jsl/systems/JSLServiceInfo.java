package com.robypomper.josp.jsl.systems;


/**
 * Interface for JSL Service's info system.
 * <p>
 * This system collect all service's info and provide them to other JSL's systems.
 * <p>
 * JSLServiceInfo implementations can access to the JCP API and JSL settings file
 * to load and store values of Service's info.
 */
public interface JSLServiceInfo {

    // Service's systems

    /**
     * Set service's systems so ServiceInfo can access to systems info and provide
     * them as a unique point of reference.
     *
     * @param user the JSL user mngm system.
     * @param objs the JSL objects mngm system.
     * @param comm the JSL communication system.
     */
    void setSystems(JSLUserMngr user, JSLObjsMngr objs, JSLCommunication comm);


    // Srv's info

    /**
     * The Service's ID is the main id used to identify the Service in the JOSP
     * Eco-System.
     *
     * @return the service's ID.
     */
    String getSrvId();

    /**
     * Human readable service's name.
     *
     * @return the service's name
     */
    String getSrvName();


    // Users's info

    /**
     * @return <code>true</code> if current JSL instance has logged current user.
     */
    boolean isUserLogged();

    /**
     * The logged user ID.
     *
     * @return logged user ID, if user was not logged then it return
     * <code>null</code>.
     */
    String getUserId();

    /**
     * The logged user ID.
     *
     * @return logged user ID, if user was not logged then it return
     * <code>null</code>.
     */
    String getUsername();


    // Objects's info

    /**
     * @return the number of connected objects.
     */
    int getConnectedObjectsCount();

    /**
     * @return the number of known objects.
     */
    int getKnownObjectsCount();


    // Mngm methods

    /**
     * Start periodically checks on settings file and JCP APIs Service for service's
     * info changes.
     */
    void startAutoRefresh();

    /**
     * Stop periodically checks on settings file and JCP APIs Service for service's
     * info changes.
     */
    void stopAutoRefresh();

}
