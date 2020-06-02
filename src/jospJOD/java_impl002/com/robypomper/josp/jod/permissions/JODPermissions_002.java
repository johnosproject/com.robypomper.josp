package com.robypomper.josp.jod.permissions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.params.permissions.ObjPermission;
import com.robypomper.josp.jcp.apis.params.permissions.PermissionsTypes;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    // Class constants

    public static final String TH_SYNC_NAME_FORMAT = "_PERM_SYNC_%s";
    public static final String ANONYMOUS_ID = "00000-00000-00000";
    public static final String ANONYMOUS_USERNAME = "Anonymous";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JODSettings_002 locSettings;
    private final JODObjectInfo objInfo;
    private final JCPClient_Object jcpClient;
    private final JCPPermObj jcpPermissions;
    private final List<ObjPermission> permissions = new LinkedList<>();
    private Timer timer = null;
    private boolean jcpPrintNotConnected = false;


    // Constructor

    /**
     * Default constructor.
     *
     * @param settings  the JOD settings.
     * @param objInfo   the object's info.
     * @param jcpClient the jcp object client.
     */
    public JODPermissions_002(JODSettings_002 settings, JODObjectInfo objInfo, JCPClient_Object jcpClient) {
        this.objInfo = objInfo;
        this.locSettings = settings;
        this.jcpClient = jcpClient;
        jcpPermissions = new JCPPermObj(jcpClient, settings);

        log.trace(Mrk_JOD.JOD_PERM, "Try to sync owner id to cloud");
        setOwnerId(getOwnerId());


        log.debug(Mrk_JOD.JOD_PERM, "Loading object's permissions");
        boolean loadLocally = false;
        boolean genCloud = false;
        try {
            loadPermissionsFromFile();
            loadLocally = true;
            log.trace(Mrk_JOD.JOD_PERM, String.format("JODPermissions %d permissions loaded from file:", permissions.size()));

        } catch (FileException e) {
            log.warn(Mrk_JOD.JOD_PERM, String.format("Error on loading permissions from file '%s' because %s", settings.getPermissionsPath().getPath(), e.getMessage()), e);
        }

        if (!loadLocally) {
            try {
                generatePermissionsFromJCP();
                genCloud = true;
                log.trace(Mrk_JOD.JOD_PERM, String.format("JODPermissions %d permissions generated on cloud:", permissions.size()));

            } catch (JCPClient.ConnectionException | JCPClient.RequestException e) {
                log.warn(Mrk_JOD.JOD_PERM, String.format("Error generating permission from JCP because %s", e.getMessage()), e);
            }
        }

        if (!loadLocally && !genCloud) {
            generatePermissionsLocally();
            log.trace(Mrk_JOD.JOD_PERM, String.format("JODPermissions %d permissions generated locally:", permissions.size()));
        }

        logPermissions(permissions);
        log.debug(Mrk_JOD.JOD_PERM, "Object's permissions loaded");

        log.info(Mrk_JOD.JOD_PERM, String.format("Initialized JODPermissions instance for '%s' ('%s') object with '%s' owner", objInfo.getObjName(), objInfo.getObjId(), getOwnerId()));
        log.debug(Mrk_JOD.JOD_PERM, String.format("                                    %s '%d' permissions", loadLocally ? "loaded locally" : genCloud ? "generated from cloud" : "generated locally", permissions.size()));
        if (loadLocally)
            log.debug(Mrk_JOD.JOD_PERM, String.format("                                    permissions loaded from file '%s'", locSettings.getPermissionsPath()));
    }

    private static void logPermissions(List<ObjPermission> permissions) {
        log.trace(Mrk_JOD.JOD_PERM, "  +--------------------+----------------------+----------------------+---------------------------+");
        log.trace(Mrk_JOD.JOD_PERM, "  | ObjID              | SrvId                | UsrId                | Connection and Perm.Type  |");
        log.trace(Mrk_JOD.JOD_PERM, "  +--------------------+----------------------+----------------------+---------------------------+");
        for (ObjPermission p : permissions)
            log.trace(Mrk_JOD.JOD_PERM, String.format("  | %-18s | %-20s | %-20s | %-13s, %-10s |", p.objId, p.srvId, p.usrId, p.connection, p.type));
        log.trace(Mrk_JOD.JOD_PERM, "  +--------------------+----------------------+----------------------+---------------------------+");
    }


    // JOD Component's interaction methods (from communication)

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canExecuteAction(String srvId, String usrId, PermissionsTypes.Connection connection) {
        ObjPermission p = search(srvId, usrId);

        if (p == null) {
            log.debug(Mrk_JOD.JOD_PERM, String.format("Action permission for srvID %s and usrID %s DENIED  because not found", srvId, usrId));
            return false;
        }

        if (connection == PermissionsTypes.Connection.LocalAndCloud)
            if (p.connection == PermissionsTypes.Connection.OnlyLocal) {
                log.debug(Mrk_JOD.JOD_PERM, String.format("Action permission for srvID %s and usrID %s DENIED  because connection type not allowed", srvId, usrId));
                return false;
            }

        boolean perm = p.type == PermissionsTypes.Type.Actions
                || p.type == PermissionsTypes.Type.CoOwner;

        log.debug(Mrk_JOD.JOD_PERM, String.format("Action permission for srvID %s and usrID %s %s", srvId, usrId, perm ? "GRANTED" : "DENIED "));
        return perm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSendLocalUpdate(String srvId, String usrId) {
        if (getOwnerId().equals(JODSettings_002.JODPERM_OWNER_DEF)) {
            log.info(Mrk_JOD.JOD_PERM, String.format("Status permission for srvID %s and usrID %s GRANTED because no obj's owner set", srvId, usrId));
            return true;
        }

        ObjPermission p = search(srvId, usrId);

        // If not found, no permission to given srvId + usrId
        if (p == null) {
            log.debug(Mrk_JOD.JOD_PERM, String.format("Status permission for srvID %s and usrID %s DENIED  because not found", srvId, usrId));
            return false;
        }

        // True only if local is allowed
        if (p.connection != PermissionsTypes.Connection.LocalAndCloud
                && p.connection != PermissionsTypes.Connection.OnlyLocal) {
            log.debug(Mrk_JOD.JOD_PERM, String.format("Status permission for srvID %s and usrID %s DENIED  because only local connection allowed", srvId, usrId));
            return false;
        }

        boolean perm = p.type == PermissionsTypes.Type.Status
                || p.type == PermissionsTypes.Type.Actions
                || p.type == PermissionsTypes.Type.CoOwner;

        log.info(Mrk_JOD.JOD_PERM, String.format("Status permission for srvID %s and usrID %s %s", srvId, usrId, perm ? "GRANTED" : "DENIED "));
        return perm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canActAsLocalCoOwner(String srvId, String usrId) {
        if (getOwnerId().isEmpty()) {
            log.debug(Mrk_JOD.JOD_PERM, String.format("CoOwner permission for srvID %s and usrID %s GRANTED because no obj's owner set", srvId, usrId));
            return true;
        }

        ObjPermission p = search(srvId, usrId);

        // If not found, no permission to given srvId + usrId
        if (p == null) {
            log.debug(Mrk_JOD.JOD_PERM, String.format("CoOwner permission for srvID %s and usrID %s DENIED  because not found", srvId, usrId));
            return false;
        }

        // True only if local is allowed
        if (p.connection != PermissionsTypes.Connection.LocalAndCloud
                && p.connection != PermissionsTypes.Connection.OnlyLocal) {
            log.debug(Mrk_JOD.JOD_PERM, String.format("CoOwner permission for srvID %s and usrID %s DENIED  because only local connection allowed", srvId, usrId));
            return false;
        }

        boolean perm = p.type == PermissionsTypes.Type.CoOwner;

        log.debug(Mrk_JOD.JOD_PERM, String.format("CoOwner permission for srvID %s and usrID %s %s", srvId, usrId, perm ? "GRANTED" : "DENIED "));
        return perm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void syncObjPermissions() {
        log.info(Mrk_JOD.JOD_PERM, String.format("Sync permission for '%s' object with JCP", objInfo.getObjId()));

        try {
            log.debug(Mrk_JOD.JOD_PERM, String.format("Synchronizing permission for object '%s' from JCP", objInfo.getObjId()));
            refreshPermissionsFromJCP();
            log.debug(Mrk_JOD.JOD_PERM, String.format("Permission synchronized for object '%s' from JCP", objInfo.getObjId()));

        } catch (JCPClient.ConnectionException | JCPClient.RequestException e) {
            log.warn(Mrk_JOD.JOD_PERM, String.format("Error on synchronizing permissions for object '%s' from JCP because %s", objInfo.getObjId(), e.getMessage()), e);
            return;
        }

        try {
            log.debug(Mrk_JOD.JOD_PERM, String.format("Storing permission for object '%s' to local file '%s'", objInfo.getObjId(), locSettings.getPermissionsPath().getPath()));
            savePermissionsToFile();
            log.debug(Mrk_JOD.JOD_PERM, String.format("Permission stored for object '%s' to local file '%s'", objInfo.getObjId(), locSettings.getPermissionsPath().getPath()));
        } catch (FileException e) {

            log.warn(Mrk_JOD.JOD_PERM, String.format("Error on storing permissions for object '%s' to local file because %s", objInfo.getObjId(), e.getMessage()), e);
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
        log.info(Mrk_JOD.JOD_PERM, String.format("Add permission to '%s' object with srvID %s, usrID %s connection '%s' and type '%s'", objInfo.getObjId(), srvId, usrId, connection, type));

        if (usrId == null || usrId.isEmpty()) {
            log.warn(Mrk_JOD.JOD_PERM, String.format("Error on adding permission for '%s' object because usrId not set", objInfo.getObjId()));
            return false;
        }
        if (srvId == null || srvId.isEmpty()) {
            log.warn(Mrk_JOD.JOD_PERM, String.format("Error on adding permission for '%s' object because srvId not set", objInfo.getObjId()));
            return false;
        }

        ObjPermission duplicate = search(srvId, usrId);
        if (duplicate != null) {
            log.trace(Mrk_JOD.JOD_PERM, String.format("Updating existing permission to '%s' object with srvID %s, usrID %s connection '%s' and type '%s'", objInfo.getObjId(), srvId, usrId, connection, type));
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
        log.info(Mrk_JOD.JOD_PERM, String.format("Remove permission to '%s' object with srvID %s and usrID %s", objInfo.getObjId(), srvId, usrId));

        if (usrId == null || usrId.isEmpty()) {
            log.warn(Mrk_JOD.JOD_PERM, String.format("Error on removing permission for '%s' object because usrId not set", objInfo.getObjId()));
            return false;
        }
        if (srvId == null || srvId.isEmpty()) {
            log.warn(Mrk_JOD.JOD_PERM, String.format("Error on removing permission for '%s' object because srvId not set", objInfo.getObjId()));
            return false;
        }

        ObjPermission duplicate = search(srvId, usrId);
        if (duplicate == null)
            return false;

        // replace existing with (toDELETE) permission
        ObjPermission newDelPerm = new ObjPermission(objInfo.getObjId(), usrId, srvId, duplicate.connection, duplicate.type, new Date(0));
        permissions.remove(duplicate);
        permissions.add(newDelPerm);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getOwnerId() {
        return locSettings.getOwnerId();
    }

    /**
     * {@inheritDoc}
     */
    public void setOwnerId(String ownerId) {
        log.info(Mrk_JOD.JOD_PERM, String.format("Permission for '%s' object set ownerID '%s'", objInfo.getObjId(), ownerId));

        try {
            jcpPermissions.setOwnerId(ownerId);
        } catch (JsonProcessingException | JCPClient.ConnectionException | JCPClient.RequestException e) {
            log.warn(Mrk_JOD.JOD_PERM, String.format("Error on setting object's owner id because %s", e.getMessage()), e);
        }

        locSettings.setOwnerId(ownerId);
    }

    /**
     * {@inheritDoc}
     */
    public boolean resetOwnerId() throws JCPClient.ConnectionException, JsonProcessingException, JCPClient.RequestException {
        log.info(Mrk_JOD.JOD_PERM, String.format("Permission for '%s' object reset ownerID", objInfo.getObjId()));

        if (!jcpPermissions.resetOwner())
            return false;
        locSettings.setOwnerId("");
        return true;
    }


    // Mngm methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void startAutoRefresh() {
        log.info(Mrk_JOD.JOD_PERM, String.format("Start JODPermissions auto-refresh for '%s' object", objInfo.getObjId()));

        if (timer != null)
            return;

        log.debug(Mrk_JOD.JOD_PERM, "Starting auto-refresh permission's timer");
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Thread.currentThread().setName(String.format(TH_SYNC_NAME_FORMAT, objInfo.getObjId()));
                if (jcpClient.isConnected()) {
                    log.debug(Mrk_JOD.JOD_PERM, "Synchronizing object's permission to JCP");
                    syncObjPermissions();
                    log.debug(Mrk_JOD.JOD_PERM, "Object's permission synchronized to JCP");
                    jcpPrintNotConnected = false;
                } else if (!jcpPrintNotConnected) {
                    log.warn(Mrk_JOD.JOD_PERM, String.format("Error on sync object '%s''s permissions to JCP because JCP client not connected", objInfo.getObjId()));
                    jcpPrintNotConnected = true;
                }
            }
        }, 0, locSettings.getPermissionsRefreshTime() * 1000);
        log.debug(Mrk_JOD.JOD_PERM, "Auto-refresh permission's timer started");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopAutoRefresh() {
        log.info(Mrk_JOD.JOD_PERM, String.format("Stop JODPermissions auto-refresh for '%s' object", objInfo.getObjId()));

        if (timer == null)
            return;

        log.debug(Mrk_JOD.JOD_PERM, "Stopping auto-refresh permission's timer");
        timer.cancel();
        timer = null;
        log.debug(Mrk_JOD.JOD_PERM, "Auto-refresh permission's timer stopped");
    }


    // Permission mngm

    /**
     * Load permissions from file specified from
     * {@link JODSettings_002#getPermissionsPath()}
     * to the local {@link #permissions} field.
     */
    private void loadPermissionsFromFile() throws FileException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<ObjPermission> loadedPerms = mapper.readValue(locSettings.getPermissionsPath(), new TypeReference<List<ObjPermission>>() {});
            List<ObjPermission> validatedPerms = new LinkedList<>();
            for (ObjPermission p : loadedPerms)
                if (p.objId.compareTo(objInfo.getObjId()) == 0)
                    validatedPerms.add(p);
            permissions.addAll(validatedPerms);
        } catch (FileNotFoundException e) {
            throw new FileException(String.format("Can't find permission file at '%s' path.", locSettings.getPermissionsPath().getAbsolutePath()), e);
        } catch (IOException e) {
            throw new FileException(String.format("Error reading permission file '%s'.", locSettings.getPermissionsPath().getAbsolutePath()), e);
        }
    }

    /**
     * Save {@link #permissions} field to file specified from
     * {@link JODSettings_002#getPermissionsPath()}.
     */
    private void savePermissionsToFile() throws FileException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(locSettings.getPermissionsPath(), permissions);
        } catch (IOException e) {
            throw new FileException(String.format("Error writing permission file '%s'.", locSettings.getPermissionsPath().getAbsolutePath()), e);
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
                                    && p.usrId.compareTo(getOwnerId()) == 0
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
