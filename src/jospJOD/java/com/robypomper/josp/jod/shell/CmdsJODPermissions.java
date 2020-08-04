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

package com.robypomper.josp.jod.shell;

import asg.cliche.Command;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.protocol.JOSPPerm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

// pl
// pa user1 service2 LocalAndCloud Actions
// pd user1 service1
public class CmdsJODPermissions {

    private final JODPermissions permission;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public CmdsJODPermissions(JODPermissions permission) {
        this.permission = permission;
    }


    @Command(description = "Print all object's permissions.")
    public String permissionsList() {
        String s = "OBJECT'S PERMISSIONS LIST\n";
        s += JOSPPerm.logPermissions(permission.getPermissions());
        return s;
    }

    @Command(description = "Add given params as object's permissions.")
    public String permissionAdd(String srvId, String usrId, String typeStr, String connectionStr) {
        JOSPPerm.Connection connection;
        if (
                connectionStr.compareToIgnoreCase("LocalAndCloud") == 0
                        || connectionStr.compareToIgnoreCase("Cloud") == 0
        )
            connection = JOSPPerm.Connection.LocalAndCloud;
        else
            connection = JOSPPerm.Connection.OnlyLocal;

        JOSPPerm.Type type;
        if (
                typeStr.compareToIgnoreCase("CoOwner") == 0
                        || typeStr.compareToIgnoreCase("Owner") == 0
        )
            type = JOSPPerm.Type.CoOwner;
        else if (
                typeStr.compareToIgnoreCase("action") == 0
                        || typeStr.compareToIgnoreCase("actions") == 0
        )
            type = JOSPPerm.Type.Actions;
        else if (
                typeStr.compareToIgnoreCase("state") == 0
                        || typeStr.compareToIgnoreCase("status") == 0
        )
            type = JOSPPerm.Type.Status;
        else
            type = JOSPPerm.Type.None;

        if (permission.addPermissions(srvId, usrId, type, connection))
            return "Permission added successfully.";
        return "Error adding permission.";
    }

    @Command(description = "Update given object's permission.")
    public String permissionUpdate(String permId, String srvId, String usrId, String typeStr, String connectionStr) {
        JOSPPerm.Connection connection;
        if (
                connectionStr.compareToIgnoreCase("LocalAndCloud") == 0
                        || connectionStr.compareToIgnoreCase("Cloud") == 0
        )
            connection = JOSPPerm.Connection.LocalAndCloud;
        else
            connection = JOSPPerm.Connection.OnlyLocal;

        JOSPPerm.Type type;
        if (
                typeStr.compareToIgnoreCase("CoOwner") == 0
                        || typeStr.compareToIgnoreCase("Owner") == 0
        )
            type = JOSPPerm.Type.CoOwner;
        else if (
                typeStr.compareToIgnoreCase("action") == 0
                        || typeStr.compareToIgnoreCase("actions") == 0
        )
            type = JOSPPerm.Type.Actions;
        else if (
                typeStr.compareToIgnoreCase("state") == 0
                        || typeStr.compareToIgnoreCase("status") == 0
        )
            type = JOSPPerm.Type.Status;
        else
            type = JOSPPerm.Type.None;

        if (permission.updPermissions(permId, srvId, usrId, type, connection))
            return "Permission added successfully.";
        return "Error adding permission.";
    }

    @Command(description = "Delete object's permissions with given id.")
    public String permissionRemove(String permId) {
        if (permission.remPermissions(permId))
            return "Permission deleted successfully.";
        return "Error deleting permission.";
    }

    @Command(description = "Set object's owner.")
    public String permissionOwnerSet(String usrId) {
        permission.setOwnerId(usrId);
        return "Owner set successfully.";
    }

    @Command(description = "Reset object's owner to unset.")
    public String permissionOwnerReset() {
        permission.resetOwnerId();
        return "Owner reset successfully.";
    }

}
