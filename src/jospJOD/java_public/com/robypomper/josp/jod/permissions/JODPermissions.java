package com.robypomper.josp.jod.systems;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.params.permissions.ObjPermission;
import com.robypomper.josp.jcp.apis.params.permissions.PermissionsTypes;

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
    boolean canExecuteAction(String srvId, String usrId, PermissionsTypes.Connection connection);

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
    boolean canSendLocalUpdate(String srvId, String usrId);

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
    boolean canActAsLocalCoOwner(String srvId, String usrId);

    /**
     * Start object's permission syncronization between local and cloud permissions.
     */
    void syncObjPermissions();

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
    boolean addPermissions(String usrId, String srvId, PermissionsTypes.Connection connection, PermissionsTypes.Type type);

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
     *
     * @return true if the owner was reset successfully, false otherwise.
     */
    boolean resetOwnerId() throws JCPClient.ConnectionException, JsonProcessingException, JCPClient.RequestException;


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


    // Exceptions

    /**
     * Exceptions for {@link JODStructure#setCommunication(JODCommunication)}
     * called twice.
     */
    class FileException extends Throwable {
        public FileException(String msg, Exception e) {
            super(msg, e);
        }
    }

}
