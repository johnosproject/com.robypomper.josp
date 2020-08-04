/* *****************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.josp.jod.permissions;

import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.protocol.JOSPPerm;

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

    void syncObjPermissions();

    // Access methods

    /**
     * The list of object's permissions.
     *
     * @return object's permissions.
     */
    List<JOSPPerm> getPermissions();

    boolean checkPermission(String srvId, String usrId, JOSPPerm.Type minReqPerm, JOSPPerm.Connection connType);

    JOSPPerm.Type getServicePermission(String srvId, String usrId, JOSPPerm.Connection connType);

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
    boolean addPermissions(String srvId, String usrId, JOSPPerm.Type type, JOSPPerm.Connection connection);

    boolean updPermissions(String permId, String srvId, String usrId, JOSPPerm.Type type, JOSPPerm.Connection connection);

    /**
     * Set permission corresponding to given <code>permId</code> to be deleted.
     *
     * @param permId the permission's id.
     * @return true if the permission is set to delete successfully, false otherwise.
     */
    boolean remPermissions(String permId);

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

        public PermissionsNotLoadedException(String path, Throwable e) {
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
