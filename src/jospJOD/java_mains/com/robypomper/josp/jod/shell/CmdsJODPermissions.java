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
    public String permissionAdd(String usrId, String srvId, String connectionStr, String typeStr) {
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

        if (permission.addPermissions(usrId, srvId, connection, type))
            return "Permission added successfully.";
        return "Error adding permission.";
    }

    @Command(description = "Delete object's permissions corresponding to given params.")
    public String permissionDelete(String usrId, String srvId) {
        if (permission.deletePermissions(usrId, srvId))
            return "Permission deleted successfully.";
        return "Error deleting permission.";
    }

    @Command(description = "Force object's permissions sync to JCP.")
    public String permissionSync() {
        int pre = permission.getPermissions().size();
        permission.syncObjPermissionsJCP();
        int post = permission.getPermissions().size();

        int tot = post - pre;
        if (tot > 0)
            return String.format("Permission sync, added %s permissions", tot);
        else if (tot < 0)
            return String.format("Permission sync, removed %s permissions", tot);

        return "Permission sync terminated";
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
