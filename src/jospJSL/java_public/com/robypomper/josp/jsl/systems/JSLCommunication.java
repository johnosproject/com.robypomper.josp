package com.robypomper.josp.jsl.systems;

import com.robypomper.josp.jsl.comm.JSLCloudConnection;
import com.robypomper.josp.jsl.comm.JSLConnection;
import com.robypomper.josp.jsl.comm.JSLLocalConnection;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;

import java.util.List;

/**
 * Interface for JSL Communication system.
 * <p>
 * With this system services can search for local JOD objects and can communicate
 * with cloud JOD objects. This class aim is to create connections and detect
 * when are closed. Then it advise the {@link JSLObjsMngr} system for connections
 * changes.
 */
public interface JSLCommunication {

    // Connections access

    /**
     * @return the Gw S2O connection, null if not connected.
     */
    JSLCloudConnection getCloudConnection();

    /**
     * @return an array containing all local connections.
     */
    List<JSLLocalConnection> getAllLocalConnection();


    // Mngm methods

    /**
     * @return <code>true</code> if the local search system is active.
     */
    boolean isLocalSearchActive();

    /**
     * Start local Object's server and publish it.
     */
    void startSearchLocalObjects();

    /**
     * Stop local Object's server and de-publish it, then close all opened connections.
     */
    void stopSearchLocalObjects();

    /**
     * @return <code>true</code> if current JSL library is connected to the Gw S2O.
     */
    boolean isCloudConnected();

    /**
     * Start JOSP Gw S2O Client.
     */
    void connectCloud();

    /**
     * Stop JOSP Gw S2O Client.
     */
    void disconnectCloud();

}
