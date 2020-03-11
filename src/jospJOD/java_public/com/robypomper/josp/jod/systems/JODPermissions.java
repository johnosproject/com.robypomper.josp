package com.robypomper.josp.jod.systems;

import com.robypomper.josp.jod.permissions.JODService;
import com.robypomper.josp.jod.permissions.JODUser;


/**
 * Interface for Object's permissions system.
 *
 * According to JCP APIs Permission, this interface implementations check if the
 * pair Service/User can exec action or receive updates to/from current object.
 *
 * This system is based on data read from permission files and JCP APIs Permission,
 * then when activated with {@link #startAutoRefresh()} this class periodically
 * checks for permission updates from both sources (file and APIs). Then, on
 * permissions changes, it update also internal permission representations.
 *
 * When permissions changes, it update also local permissions file. Permissions
 * files and JCP APIs Permissions must be always sync by JODPermissions
 * implementations.
 */
public interface JODPermissions {

    // JOD Component's interaction methods (from communication)

    /**
     * Check if given identities (<code>service/user</code>) can send command
     * execution request to current object.
     *
     * This check is perform by {@link JODCommunication} when receive a message
     * from a service.
     *
     * @param service service that send the request.
     * @param user logged user in the service that send the request.
     * @return true if and only if the pair <code>service/user</code>) can send
     *         command requests to current object.
     */
    boolean canExecuteAction(JODService service, JODUser user);

    /**
     * Check if given identities (<code>service/user</code>) can receive status
     * updates from current object.
     *
     * This check is perform by {@link JODCommunication} when need to send an
     * update to connected services.
     *
     * @param service service that can receive the update.
     * @param user logged user in the service that can receive the update.
     * @return true if and only if the pair <code>service/user</code>) can
     *         receive the status update from current object.
     */
    boolean canSendUpdate(JODService service, JODUser user);


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
     * Load object's structure from JCP APIs Permissions.
     */
    void loadPermissionsFromJCP();

    /**
     * Load object's structure from local permissions file.
     */
    void loadPermissionsFromFile();

}
