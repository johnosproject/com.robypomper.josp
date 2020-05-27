package com.robypomper.josp.jsl.comm;

import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLAction;

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

    // Action cmd flow (objMng - comm)

    /**
     * Forward <code>component</code> <code>action</code> to corresponding object.
     * <p>
     * This method is required by {@link JSLAction} when must send a command to
     * the corresponding object.
     *
     * @param object    the object to send the action command.
     * @param component the object's component to send the action command.
     *                  //@param command   the action command info.
     */
    void forwardAction(JSLRemoteObject object, JSLAction component/*, JSLActionCommand command*/);


    // Connections access

    /**
     * @return the Gw S2O connection, null if not connected.
     */
    JSLGwS2OClient getCloudConnection();

    /**
     * @return an array containing all local connections.
     */
    List<JSLLocalClient> getAllLocalServers();


    // Mngm methods

    /**
     * @return <code>true</code> if the local search system is active.
     */
    boolean isLocalRunning();

    /**
     * Start local Object's server and publish it.
     */
    void startLocal() throws LocalCommunicationException;

    /**
     * Stop local Object's server and de-publish it, then close all opened connections.
     */
    void stopLocal() throws LocalCommunicationException;

    /**
     * @return <code>true</code> if current JSL library is connected to the Gw S2O.
     */
    boolean isCloudConnected();

    /**
     * Start JOSP Gw S2O Client.
     */
    void connectCloud() throws CloudCommunicationException;

    /**
     * Stop JOSP Gw S2O Client.
     */
    void disconnectCloud();


    // Exceptions

    /**
     * Exceptions for local communication errors.
     */
    class LocalCommunicationException extends Throwable {
        public LocalCommunicationException(String msg) {
            super(msg);
        }

        public LocalCommunicationException(String msg, Throwable e) {
            super(msg, e);
        }
    }

    /**
     * Exceptions for cloud communication errors.
     */
    class CloudCommunicationException extends Throwable {
        public CloudCommunicationException(String msg) {
            super(msg);
        }

        public CloudCommunicationException(String msg, Throwable e) {
            super(msg, e);
        }
    }

}
