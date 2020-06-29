package com.robypomper.josp.jsl.objs;

import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLGwS2OClient;
import com.robypomper.josp.jsl.comm.JSLLocalClient;

import java.util.List;


/**
 * Interface for JSL Objects management system.
 * <p>
 * This system manage and provide all available JOD objects as
 * {@link JSLRemoteObject} instances.
 * <p>
 * Available JOD objects list depends on user/service permissions. A service can
 * access to an object if and only if that objects grant the permission to the
 * pair user/service.
 * <p>
 * Each {@link JSLRemoteObject} is initialized from {@link JSLLocalClient} instance
 * provided by the {@link JSLCommunication} system. An Object can be associated
 * to multiple {@link JSLLocalClient} and to the {@link JSLGwS2OClient}
 * at the same time.<br>
 * For every message tx between services and objects is send preferring local
 * connections and, only if no local connection is available is choose the
 * cloud connection.
 */
public interface JSLObjsMngr {

    // Object's access

    /**
     * @return an array containing all known objects.
     */
    List<JSLRemoteObject> getAllObjects();

    /**
     * @return an array containing all connected objects.
     */
    List<JSLRemoteObject> getAllConnectedObjects();

    /**
     * @param objId id of object required.
     * @return the object corresponding to given id, null if not found.
     */
    JSLRemoteObject getById(String objId);

    /**
     * @param client required object's local connection.
     * @return the object associated to given local connection, null if not found.
     */
    JSLRemoteObject getByConnection(JSLLocalClient client);

    /**
     * @param pattern object's search pattern.
     * @return an array containing all object's corresponding to given search
     * pattern.
     */
    List<JSLRemoteObject> searchObjects(JSLObjectSearchPattern pattern);


    // Connections mngm

    /**
     * Associate given connection to corresponding {@link JSLRemoteObject}.
     * <p>
     * This method is call by {@link JSLCommunication} system when it opens a
     * local connection with a JOD Object.
     * <p>
     * If there is no {@link JSLRemoteObject} yet, it will be created.
     *
     * @param serverConnection the open local connection to JOD object.
     */
    void addNewConnection(JSLLocalClient serverConnection);

    /**
     * Remove given connection to corresponding {@link JSLRemoteObject}.
     * <p>
     * This method is call by {@link JSLCommunication} system when it close (or
     * detect that is closed) a local connection with a JOD Object.
     * <p>
     * If there is no {@link JSLRemoteObject} yet, it will be created anyway and
     * set offline.
     *
     * @param localConnection the closed local connection to JOD object.
     */
    boolean removeConnection(JSLLocalClient localConnection);

    void setCommunication(JSLCommunication communication);

    void addCloudObject(String objId);

}
