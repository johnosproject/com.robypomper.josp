package com.robypomper.josp.jod.shell;

import asg.cliche.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.params.permissions.ObjPermission;
import com.robypomper.josp.jcp.apis.params.permissions.PermissionsTypes;
import com.robypomper.josp.jod.systems.JODPermissions;

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
        StringBuilder s = new StringBuilder();
        s.append("OBJECT'S PERMISSIONS\n");
        s.append("User       Service    Connection Type       UpdatedAt\n");
        System.out.println("PERM# " + permission.getPermissions().size());
        for (ObjPermission p : permission.getPermissions())
            s.append(String.format("%-10s %-10s %-10s %-10s %s\n",
                    p.usrId.substring(0, Math.min(p.usrId.length(), 10)),
                    p.srvId.substring(0, Math.min(p.srvId.length(), 10)),
                    p.connection.toString().substring(0, Math.min(p.connection.toString().length(), 10)),
                    p.type.toString(),
                    dateFormat.format(p.updatedAt)));
        return s.toString();
    }

    @Command(description = "Add given params as object's permissions.")
    public String permissionAdd(String usrId, String srvId, String connectionStr, String typeStr) {
        PermissionsTypes.Connection connection;
        if (
                connectionStr.compareToIgnoreCase("LocalAndCloud") == 0
                        || connectionStr.compareToIgnoreCase("Cloud") == 0
        )
            connection = PermissionsTypes.Connection.LocalAndCloud;
        else
            connection = PermissionsTypes.Connection.OnlyLocal;

        PermissionsTypes.Type type;
        if (
                typeStr.compareToIgnoreCase("CoOwner") == 0
                        || typeStr.compareToIgnoreCase("Owner") == 0
        )
            type = PermissionsTypes.Type.CoOwner;
        else if (
                typeStr.compareToIgnoreCase("action") == 0
                        || typeStr.compareToIgnoreCase("actions") == 0
        )
            type = PermissionsTypes.Type.Actions;
        else if (
                typeStr.compareToIgnoreCase("statu") == 0
                        || typeStr.compareToIgnoreCase("status") == 0
        )
            type = PermissionsTypes.Type.Status;
        else
            type = PermissionsTypes.Type.None;

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
        permission.syncObjPermissions();
        int post = permission.getPermissions().size();

        int tot = post - pre;
        if (tot > 0)
            return String.format("Permission sync, added %s permissions", tot);
        else if (tot < 0)
            return String.format("Permission sync, removed %s permissions", tot);

        return "Permission sync terminated";
    }

    @Command(description = "Set object's owner.")
    public String permissionSetOwner(String usrId) {
        try {
            if (permission.setOwner(usrId))
                return "Owner set successfully.";
        } catch (JCPClient.ConnectionException | JCPClient.RequestException | JsonProcessingException e) {
            return "Error on setting owner: " + e.getMessage();
        }
        return "Unknown error on setting owner.";
    }

    @Command(description = "Reset object's owner to unset.")
    public String permissionResetOwner() {
        try {
            if (permission.resetOwner())
                return "Owner reset successfully.";
        } catch (JCPClient.ConnectionException | JCPClient.RequestException | JsonProcessingException e) {
            return "Error on resetting owner: " + e.getMessage();
        }
        return "Unknown error on resetting owner.";
    }


}
