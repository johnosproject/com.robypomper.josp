package com.robypomper.josp.jod.permissions;

import com.robypomper.josp.jcp.apis.params.permissions.ObjPermission;
import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.protocol.JOSPPermissions;

import java.util.List;


/**
 * Interface for Object's permissions system.
 * <p>
 * According to JCP APIs Permission, this interface implementations check if the
 * pair Service/User can exec action or receive updates to/from current object.
 * <p>
 * This system is based on data read from permission files and JCP APIs Permission,
 * then when activated with {@link #startAutoRefresh()} this class periodically
 * checks for permission updates from both sources (file and APIs). Then, on
 * permissions changes, it update also internal permission representations.
 * <p>
 * When permissions changes, it update also local permissions file. Permissions
 * files and JCP APIs Permissions must be always sync by JODPermissions
 * implementations.
 */
public interface JODPermissions {

    /**
     * Set the {@link JODCommunication} reference to the JODStructure object.
     * <p>
     * This cross-system reference is required by the State Update Flow.
     *
     * @param comm the {@link JODCommunication} reference.
     */
    void setCommunication(JODCommunication comm) throws JODStructure.CommunicationSetException;

    // JOD Component's interaction methods (from communication)

    /**
     * Check if given identities (<code>service/user</code>) can send command
     * execution request to current object.
     * <p>
     * This check is perform by {@link JODCommunication} when receive a message
     * from a service.
     *
     * @param srvId service's id that send the request.
     * @param usrId logged user's id in the service that send the request.
     * @return true if and only if the pair <code>service/user</code>) can send
     * command requests to current object.
     */
    boolean canExecuteAction(String srvId, String usrId, JOSPPermissions.Connection connection);

    /**
     * Check if given identities (<code>service/user</code>) can receive status
     * updates from current object.
     * <p>
     * This check is perform by {@link JODCommunication} when need to send an
     * update to connected services or when the object receive a local service
     * request.
     *
     * @param srvId service's id that can receive the update.
     * @param usrId logged user's id in the service that can receive the update.
     * @return true if and only if the pair <code>service/user</code> can
     * receive the status update from current object.
     */
    boolean canSendUpdate(String srvId, String usrId, JOSPPermissions.Connection connection);

    /**
     * Check if given identities (<code>service/user</code>) can act as object's
     * coOwner.
     * <p>
     * This check is perform by {@link JODCommunication} when receive a service
     * request that need coOwner permission.
     *
     * @param srvId service's id that send the request.
     * @param usrId logged user's id in the service that send the request.
     * @return true if and only if the pair <code>service/user</code> can
     * act as object's owner.
     */
    boolean canActAsCoOwner(String srvId, String usrId, JOSPPermissions.Connection connection);

    void syncObjPermissions();

    /**
     * Start object's permission syncronization between local and cloud permissions.
     */
    void syncObjPermissionsJCP();

    // Access methods

    /**
     * The list of object's permissions.
     *
     * @return object's permissions.
     */
    List<ObjPermission> getPermissions();

    /**
     * Add given permission to object's permissions.
     * <p>
     * If a permission with same <code>usrId</code> and <code>srvId</code> already
     * exist, then it will be updated.
     *
     * @param usrId      the user's id.
     * @param srvId      the user's id.
     * @param connection the connection type allow by created permission.
     * @param type       the permission type of created permission.
     * @return true if the permission was added successfully, false otherwise.
     */
    boolean addPermissions(String usrId, String srvId, JOSPPermissions.Connection connection, JOSPPermissions.Type type);

    /**
     * Set permission corresponding to given <code>usrId</code> and <code>srvId</code>
     * to be deleted.
     *
     * @param usrId the user's id.
     * @param srvId the user's id.
     * @return true if the permission is set to delete successfully, false otherwise.
     */
    boolean deletePermissions(String usrId, String srvId);

    /**
     * The object owner id.
     *
     * @return object owner id.
     */
    String getOwnerId();

    /**
     * Set object's owner.
     *
     * @param ownerId the user's id.
     */
    void setOwnerId(String ownerId);

    /**
     * Set object's owner to unset.
     */
    void resetOwnerId();


    // Mngm methods

    /**
     * Start periodically checks on JCP APIs Permission looking for permissions
     * changes.
     */
    void startAutoRefresh();

    /**
     * Stop periodically checks on JCP APIs Permission for permissions changes.
     */
    void stopAutoRefresh();

    /**
     * Delete current object's permission and genereate new one.
     * <p>
     * This method is called when obj's id or owner change (object's owner
     * update cause object's id re-generation).
     */
    void regeneratePermissions() throws PermissionsFileException;

    // Exceptions

    /**
     * Base class for exception thrown on operations on permission's file (read/write).
     */
    class PermissionsFileException extends Throwable {
        protected PermissionsFileException(String msg, Throwable t) {
            super(msg, t);
        }
    }

    /**
     * Exceptions for permissions file reading errors.
     */
    class PermissionsNotLoadedException extends PermissionsFileException {
        private static final String MSG = "Error reading permissions from file '%s'";

        public PermissionsNotLoadedException(String path, Exception e) {
            super(String.format(MSG, path), e);
        }
    }

    /**
     * Exceptions for permissions file writing errors.
     */
    class PermissionsNotSavedException extends PermissionsFileException {
        private static final String MSG = "Error writing permissions from file '%s'";

        public PermissionsNotSavedException(String path, Exception e) {
            super(String.format(MSG, path), e);
        }
    }

}
