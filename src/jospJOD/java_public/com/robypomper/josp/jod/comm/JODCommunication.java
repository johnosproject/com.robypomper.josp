package com.robypomper.josp.jod.comm;

import com.robypomper.josp.jcp.apis.params.permissions.PermissionsTypes;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.JODStateUpdate;
import com.robypomper.josp.jod.structure.JODStructure;

import java.util.List;


/**
 * Interface for Object's communication system.
 * <p>
 * The communication system provide channels for local and cloud communication.
 * <p>
 * The local communication include an SSL TCP server, his publication on the
 * ZeroConf protocol and local client management.
 * <p>
 * Cloud communication provide a channel to remote service via the JCP. When
 * activate, it start a tcp/ws socket to the JOSP GW on the JCP cloud. Then
 * the JOSP GW act as gateway from current object and remote services. Even
 * if the JOSP GW check service/user permissions, current object perform security
 * checks.
 */
public interface JODCommunication {

    // Status upd flow (objStruct - comm)

    /**
     * Dispatch <code>component</code> <code>update</code> to connected and allowed
     * services.
     * <p>
     * This method is required by {@link JODState} when receive an update from
     * his {@link com.robypomper.josp.jod.executor.JODPuller} or
     * {@link com.robypomper.josp.jod.executor.JODListener} object.
     *
     * @param component the object's component that updated his state.
     * @param update    the status update info.
     */
    void dispatchUpdate(JODState component, JODStateUpdate update);


    // Action cmd flow (comm - objStruct)

    /**
     * Parse and exec given action (in the <code>msg</code> string) to the right
     * object's component.
     */
    boolean forwardAction(String msg, PermissionsTypes.Connection connType);


    // Local service requests

    /**
     * Method to process all requests received from local services.
     * <p>
     * If received data are NOT a local service request, then this method return
     * null pointer. Otherwise it return a String containing the response to
     * replay to the sender JSL.
     *
     * @param client the sender JSL info.
     * @param msg    the local service request received from <code>client</code>.
     * @return a String containing the response to reply or null if <code>msg</code>
     * doesn't contain a valid local service request.
     */
    String processServiceRequest(JODLocalClientInfo client, String msg);


    // Cloud requests

    /**
     * Method to process all requests received from GW's O2S.
     * <p>
     * If received data are NOT a cloud request, then this method return
     * null pointer. Otherwise it return a String containing the response to
     * replay to the sender JSL.
     *
     * @param msg the cloud request received.
     * @return a String containing the response to reply or null if <code>msg</code>
     * doesn't contain a valid cloud request.
     */
    String processCloudRequest(String msg);


    // Connections access

    /**
     * @return the Gw O2S connection, null if not connected.
     */
    JODGwO2SClient getGwO2SClient();

    /**
     * @return an array containing all local connections.
     */
    List<JODLocalClientInfo> getAllLocalClientsInfo();


    // Mngm methods

    /**
     * @return <code>true</code> if local communication server is running.
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
     * @return <code>true</code> if cloud communication server is connected.
     */
    boolean isCloudConnected();

    /**
     * Start JOSP Gw O2S Client.
     */
    void connectCloud() throws CloudCommunicationException;

    /**
     * Stop JOSP Gw O2S Client.
     */
    void disconnectCloud();


    // Cross component references

    /**
     * Set the {@link JODStructure} reference to the current object.
     * <p>
     * This cross-system reference is required by the Action Execution Flow.
     *
     * @param structure the {@link JODStructure} reference.
     */
    void setStructure(JODStructure structure) throws StructureSetException;

    /**
     * @return the object's Structure sub system reference.
     */
    JODStructure getStructure() throws StructureSetException;


    // Exceptions

    /**
     * Exceptions for {@link #setStructure(JODStructure)} called twice.
     */
    class StructureSetException extends Throwable {
        private static final String MSG = "Structure already set for current Communication.";

        public StructureSetException() {
            super(MSG);
        }
    }

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


    /**
     * Exceptions for cloud communication errors.
     */
    class MissingPermissionException extends Throwable {
        private static final String MSG = "Can't elaborate '%s' request because missing permission on service '%s' / user '%s'";

        public MissingPermissionException(String reqType, String srvId, String usrId) {
            super(String.format(MSG, reqType, srvId, usrId));
        }
    }

}
