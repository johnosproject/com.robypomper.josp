package com.robypomper.josp.jod.systems;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.Collections;
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

    private final JOD_002.Settings settings;
    private final JODObjectInfo objInfo;
    private final JCPClient_Object jcpClient;
    private final JCPPermObj jcpPermissions;
    private final List<ObjPermission> permissions = new LinkedList<>();
    private Timer timer = null;


    // Constructor

    /**
     * Default constructor.
     *
     * @param settings  the JOD settings.
     * @param objInfo   the object's info.
     * @param jcpClient the jcp object client.
     */
    public JODPermissions_002(JOD_002.Settings settings, JODObjectInfo objInfo, JCPClient_Object jcpClient) {
        System.out.println("DEB: JOD Permissions initialization...");

        this.objInfo = objInfo;
        this.settings = settings;
        this.jcpClient = jcpClient;
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

        System.out.println("DEB: JOD Permissions initialized");
    }


    // JOD Component's interaction methods (from communication)

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSendLocalUpdate(String srvId, String usrId) {
        ObjPermission p = search(srvId, usrId);

        // If not found, no permission to given srvId + usrId
        if (p == null)
            return false;

        // True only if local is allowed
        if (p.connection != PermissionsTypes.Connection.LocalAndCloud
                && p.connection != PermissionsTypes.Connection.OnlyLocal)
            return false;

        return p.type == PermissionsTypes.Type.Status
                || p.type == PermissionsTypes.Type.Actions
                || p.type == PermissionsTypes.Type.CoOwner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void syncObjPermissions() {
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
        }
    }


    // Access methods

    /**
     * {@inheritDoc}
     */
    public List<ObjPermission> getPermissions() {
        return Collections.unmodifiableList(permissions);
    }

    /**
     * {@inheritDoc}
     */
    public boolean addPermissions(String usrId, String srvId, PermissionsTypes.Connection connection, PermissionsTypes.Type type) {
        if (usrId == null || usrId.isEmpty())
            return false;
        if (srvId == null || srvId.isEmpty())
            return false;

        ObjPermission duplicate = search(srvId, usrId);
        if (duplicate != null) {
            ObjPermission newPerm = new ObjPermission(objInfo.getObjId(), usrId, srvId, connection, type, new Date());
            permissions.remove(duplicate);
            permissions.add(newPerm);
            return true;
        }

        ObjPermission newPerm = new ObjPermission(objInfo.getObjId(), usrId, srvId, connection, type, new Date());
        permissions.add(newPerm);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean deletePermissions(String usrId, String srvId) {
        ObjPermission duplicate = search(srvId, usrId);
        if (duplicate == null)
            return false;

        ObjPermission newDelPerm = new ObjPermission(objInfo.getObjId(), usrId, srvId, duplicate.connection, duplicate.type, new Date(0));
        permissions.remove(duplicate);
        permissions.add(newDelPerm);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getOwner() throws JCPClient.ConnectionException, JCPClient.RequestException {
        return jcpPermissions.getOwner();
    }

    /**
     * {@inheritDoc}
     */
    public boolean setOwner(String ownerId) throws JCPClient.ConnectionException, JCPClient.RequestException, JsonProcessingException {
        if (!jcpPermissions.setOwner(ownerId))
            return false;
        settings.setOwnerId(ownerId);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean resetOwner() throws JCPClient.ConnectionException, JsonProcessingException, JCPClient.RequestException {
        if (!jcpPermissions.resetOwner())
            return false;
        settings.setOwnerId("");
        return true;
    }


    // Mngm methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void startAutoRefresh() {
        if (timer != null)
            return;

        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (jcpClient.isConnected())
                    syncObjPermissions();
            }
        }, 0, settings.getPermissionsRefreshTime() * 1000);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopAutoRefresh() {
        if (timer == null)
            return;

        timer.cancel();
        timer = null;
    }


    // Permission mngm

    /**
     * Load permissions from file specified from
     * {@link com.robypomper.josp.jod.JOD_002.Settings#getPermissionsPath()}
     * to the local {@link #permissions} field.
     */
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

    /**
     * Save {@link #permissions} field to file specified from
     * {@link com.robypomper.josp.jod.JOD_002.Settings#getPermissionsPath()}.
     */
    private void savePermissionsToFile() throws FileException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(settings.getPermissionsPath(), permissions);
        } catch (IOException e) {
            throw new FileException(String.format("Error writing permission file '%s'.", settings.getPermissionsPath().getAbsolutePath()), e);
        }
    }

    /**
     * Request to JCP a valid set of object's permissions and set them to
     * {@link #permissions} field.
     */
    private void generatePermissionsFromJCP() throws JCPClient.ConnectionException, JCPClient.RequestException {
        List<ObjPermission> newPerms = jcpPermissions.generatePermissionsFromJCP();
        permissions.clear();
        permissions.addAll(newPerms);
    }

    /**
     * Generate a valid set of object's permissions and set them to
     * {@link #permissions} field.
     */
    private void generatePermissionsLocally() {
        permissions.add(new ObjPermission(objInfo.getObjId(), PermissionsTypes.WildCards.USR_OWNER.toString(), PermissionsTypes.WildCards.SRV_ALL.toString(), PermissionsTypes.Connection.LocalAndCloud, PermissionsTypes.Type.CoOwner, new Date()));
        permissions.add(new ObjPermission(objInfo.getObjId(), PermissionsTypes.WildCards.USR_ALL.toString(), PermissionsTypes.WildCards.SRV_ALL.toString(), PermissionsTypes.Connection.OnlyLocal, PermissionsTypes.Type.CoOwner, new Date()));
    }

    /**
     * Sync object's permissions with JCP object's permissions.
     */
    private void refreshPermissionsFromJCP() throws JCPClient.ConnectionException, JCPClient.RequestException {
        List<ObjPermission> newPerms = jcpPermissions.refreshPermissionsFromJCP(permissions);
        permissions.clear();
        permissions.addAll(newPerms);
    }

    /**
     * Return the object's permission with the same srvId and usrId like that
     * once passed as params.
     *
     * @param srvId the service's id.
     * @param usrId the user's id.
     * @return the object's permission reference or null if no permission
     * correspond to given params.
     */
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
