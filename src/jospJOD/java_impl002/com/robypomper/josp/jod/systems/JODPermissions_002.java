package com.robypomper.josp.jod.systems;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.params.permissions.ObjPermission;
import com.robypomper.josp.jcp.apis.params.permissions.PermissionsTypes;
import com.robypomper.josp.jod.JOD_002;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.permissions.JCPPermObj;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ToDo: doc JODPermissions_002
 */
public class JODPermissions_002 implements JODPermissions {

    // Internal vars

    private final JODObjectInfo objInfo;
    private final JOD_002.Settings settings;
    private final JCPPermObj jcpPermissions;
    private final List<ObjPermission> permissions = new LinkedList<>();
    private Timer timer = null;


    // Constructor

    public JODPermissions_002(JOD_002.Settings settings, JODObjectInfo objInfo, JCPClient_Object jcpClient) {
        System.out.println("DEB: JOD Permissions initialization...");

        this.objInfo = objInfo;
        this.settings = settings;
        jcpPermissions = new JCPPermObj(jcpClient, settings);

        try {
            loadPermissionsFromFile();
            System.out.println(String.format("DEB: Loaded %s permissions (obj-srv-usr):", permissions.size()));
            for (ObjPermission p : permissions) {
                System.out.println(String.format("DEB: %s - %s - %s: %s and %s", p.objId, p.srvId, p.usrId, p.connection, p.type));
            }

        } catch (FileException e) {
            if (e.getCause() instanceof FileNotFoundException)
                System.out.println("INF: No permission saved locally, it will generate them on JCP.");
            else
                System.out.println("WAR: Error on load permission locally.");
        }

        if (permissions.size() == 0) {
            try {
                generatePermissionsFromJCP();
            } catch (JCPClient.ConnectionException | JCPClient.RequestException e) {
                generatePermissionsLocally();
            }
        }
    }


    // ...

    @Override
    public boolean canExecuteAction(String srvId, String usrId, PermissionsTypes.Connection connection) {
        ObjPermission p = search(srvId, usrId);

        if (p == null)
            return false;

        if (connection == PermissionsTypes.Connection.LocalAndCloud)
            if (p.connection == PermissionsTypes.Connection.OnlyLocal)
                return false;

        return p.type == PermissionsTypes.Type.Actions
                || p.type == PermissionsTypes.Type.CoOwner;
    }

    @Override
    public boolean canSendUpdate(String srvId, String usrId, PermissionsTypes.Connection connection) {
        ObjPermission p = search(srvId, usrId);

        if (p == null)
            return false;

        if (connection == PermissionsTypes.Connection.LocalAndCloud)
            if (p.connection == PermissionsTypes.Connection.OnlyLocal)
                return false;

        return p.type == PermissionsTypes.Type.Status
                || p.type == PermissionsTypes.Type.Actions
                || p.type == PermissionsTypes.Type.CoOwner;
    }


    // ...

    @Override
    public void startAutoRefresh() {
        if (timer != null)
            return;

        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(String.format("DEB: refresh object's permission each %d seconds.", settings.getPermissionsRefreshTime()));

                try {
                    refreshPermissionsFromJCP();
                } catch (JCPClient.ConnectionException e) {
                    System.out.println("WAR: Connection error on refresh permissions on JCP");
                    return;
                } catch (JCPClient.RequestException e) {
                    System.out.println(String.format("WAR: Error on refresh permissions on JCP (%s)", e.getMessage()));
                    return;
                }

                try {
                    savePermissionsToFile();
                } catch (FileException e) {
                    System.out.println(String.format("WAR: error on save permission locally (%s).", e.getMessage()));
                    return;
                }
            }
        }, 0, settings.getPermissionsRefreshTime() * 1000);

    }

    @Override
    public void stopAutoRefresh() {
        if (timer == null)
            return;

        timer.cancel();
        timer = null;
    }


    // ...

    private void loadPermissionsFromFile() throws FileException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<ObjPermission> loadedPerms = mapper.readValue(settings.getPermissionsPath(), new TypeReference<List<ObjPermission>>() {});
            List<ObjPermission> validatedPerms = new LinkedList<>();
            for (ObjPermission p : loadedPerms)
                if (p.objId.compareTo(objInfo.getObjId()) == 0)
                    validatedPerms.add(p);
            permissions.addAll(validatedPerms);
        } catch (FileNotFoundException e) {
            throw new FileException(String.format("Can't find permission file at '%s' path.", settings.getPermissionsPath().getAbsolutePath()), e);
        } catch (IOException e) {
            throw new FileException(String.format("Error reading permission file '%s'.", settings.getPermissionsPath().getAbsolutePath()), e);
        }
    }

    private void savePermissionsToFile() throws FileException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(settings.getPermissionsPath(), permissions);
        } catch (IOException e) {
            throw new FileException(String.format("Error writing permission file '%s'.", settings.getPermissionsPath().getAbsolutePath()), e);
        }
    }

    private void generatePermissionsFromJCP() throws JCPClient.ConnectionException, JCPClient.RequestException {
        List<ObjPermission> newPerms = jcpPermissions.generatePermissionsFromJCP();
        permissions.clear();
        permissions.addAll(newPerms);
    }

    private void generatePermissionsLocally() {
        permissions.add(new ObjPermission(objInfo.getObjId(), PermissionsTypes.WildCards.USR_OWNER.toString(), PermissionsTypes.WildCards.SRV_ALL.toString(), PermissionsTypes.Connection.LocalAndCloud, PermissionsTypes.Type.CoOwner, new Date()));
        permissions.add(new ObjPermission(objInfo.getObjId(), PermissionsTypes.WildCards.USR_ALL.toString(), PermissionsTypes.WildCards.SRV_ALL.toString(), PermissionsTypes.Connection.OnlyLocal, PermissionsTypes.Type.CoOwner, new Date()));
    }

    private void refreshPermissionsFromJCP() throws JCPClient.ConnectionException, JCPClient.RequestException {
        List<ObjPermission> newPerms = jcpPermissions.refreshPermissionsFromJCP(permissions);
        permissions.clear();
        permissions.addAll(newPerms);
    }

    private ObjPermission search(String srvId, String usrId) {
        for (ObjPermission p : permissions) {
            if (
                    p.usrId.compareTo(usrId) == 0
                            || p.usrId.compareTo(PermissionsTypes.WildCards.USR_ALL.toString()) == 0
                            || (
                            p.usrId.compareTo(PermissionsTypes.WildCards.USR_OWNER.toString()) == 0
                                    && p.usrId.compareTo(objInfo.getOwnerId()) == 0
                    )
            ) {
                if (
                        p.srvId.compareTo(srvId) == 0
                                || p.srvId.compareTo(PermissionsTypes.WildCards.SRV_ALL.toString()) == 0
                )
                    return p;
            }
        }
        return null;
    }

}
