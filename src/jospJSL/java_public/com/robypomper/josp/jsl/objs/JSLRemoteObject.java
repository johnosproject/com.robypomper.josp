package com.robypomper.josp.jsl.objs;

import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.structure.JSLAction;
import com.robypomper.josp.jsl.objs.structure.JSLActionParams;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;

import java.util.List;


/**
 * JOD Object representation for JSL library.
 */
public interface JSLRemoteObject {

    // Object's info

    /**
     * @return the object's id.
     */
    String getId();

    /**
     * @return the object's name.
     */
    String getName();

    /**
     * Send a request to set the object's name.
     */
    void setName(String newName) throws ObjectNotConnected;

    /**
     * @return the object's structure.
     */
    JSLRoot getStructure();

    /**
     * @return the object's owner Id.
     */
    String getOwnerId();

    /**
     * Send a request to set the object's owner id.
     */
    void setOwnerId(String newOwnerId) throws ObjectNotConnected;

    /**
     * @return the object's JOD version.
     */
    String getJODVersion();


    // Object's communication

    JSLCommunication getCommunication();

    /**
     * @return <code>true</code> if the objects is reachable via JCP or direct
     * communication.
     */
    boolean isConnected();

    /**
     * @return <code>true</code> if the objects is reachable via JCP.
     */
    boolean isCloudConnected();

    /**
     * @return <code>true</code> if and only if there at least is a valid and
     * open connection to the object.
     */
    boolean isLocalConnected();

    /**
     * Add given client as communication channel between this JSL object representation
     * and corresponding JOD object.
     * <p>
     * This method checks if it's the first client set for current object
     * representation then send object's presentation requests (objectInfo and
     * objectStructure).
     * <p>
     * If there is already a connected client, then this method disconnect given
     * one. Ther must be at least one connected client, others are used as
     * backups.
     *
     * @param localClient the client connected with corresponding JOD object.
     */
    void addLocalClient(JSLLocalClient localClient);

    /**
     * Method called when a client disconnect and reconnect (same JOD IP address
     * but different ports) and update internal client list.
     *
     * @param oldClient the old client info.
     * @param newClient the updated client info.
     */
    void replaceLocalClient(JSLLocalClient oldClient, JSLLocalClient newClient);

    /**
     * @return an array containing all available local connections to the object.
     * if the object is disconnected or works only via cloud, then the
     * returned array will be empty.
     */
    List<JSLLocalClient> getLocalClients();

    /**
     * @return the client connected (if any) to the corresponding JOD object.
     */
    JSLLocalClient getConnectedLocalClient();


    // To Object Msg

    void sendActionCmdMsg(JSLAction component, JSLActionParams command) throws ObjectNotConnected;


    // Process object's data

    /**
     * Process service request's response send by corresponding JOD object.
     *
     * @param msg the message containing the service request's response.
     * @return <code>true</code> if the service request's response was processed
     * successfully.
     */
    boolean processServiceRequestResponse(String msg);

    /**
     * Process cloud data send by corresponding JOD object.
     *
     * @param msg the message containing the cloud data.
     * @return <code>true</code> if the cloud data was processed successfully.
     */
    boolean processCloudData(String msg);


    // Exceptions

    /**
     * Exceptions thrown when accessing to a not connected object.
     */
    class ObjectNotConnected extends Throwable {
        private static final String MSG = "Can't access to '%s' object because not connected.";

        public ObjectNotConnected(JSLRemoteObject obj) {
            super(String.format(MSG, obj.getId()));
        }

        public ObjectNotConnected(JSLRemoteObject obj, Throwable t) {
            super(String.format(MSG, obj.getId()), t);
        }
    }

    /**
     * Exceptions thrown when accessing to a not connected object.
     */
    class ComponentInitException extends Throwable {
        private static final String MSG = "Error for object '%s' on %s.";

        public ComponentInitException(JSLRemoteObject obj, String msg) {
            super(String.format(MSG, obj.getId(), msg));
        }

        public ComponentInitException(JSLRemoteObject obj, String msg, Throwable t) {
            super(String.format(MSG, obj.getId(), msg), t);
        }
    }

    /**
     * Exceptions for structure parsing, file load... errors.
     */
    class ParsingException extends Throwable {
        private static final String MSG_WRAPPER = "Error for object '%s' on %s.";
        private static final String MSG = "(@line: %d; col: %d)";

        public ParsingException(JSLRemoteObject obj, String msg) {
            super(String.format(MSG_WRAPPER, obj.getId(), msg));
        }

        public ParsingException(JSLRemoteObject obj, String msg, Throwable e) {
            super(String.format(MSG_WRAPPER, obj.getId(), msg), e);
        }

        public ParsingException(JSLRemoteObject obj, String msg, Throwable e, int line, int col) {
            super(String.format(MSG_WRAPPER, obj.getId(), msg + String.format(MSG, line, col)));
        }
    }

    /**
     * Exception thrown when the structure initialization try to generate an
     * unknown component type.
     */
    class ParsingUnknownTypeException extends ParsingException {
        private static final String MSG = "Unknown type '%s' for '%s' JOD Component.";

        public ParsingUnknownTypeException(JSLRemoteObject obj, String compType, String compName) {
            super(obj, String.format(MSG, compType, compName));
        }
    }

}
