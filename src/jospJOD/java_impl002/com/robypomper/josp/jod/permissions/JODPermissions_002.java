package com.robypomper.josp.jod.permissions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.params.permissions.ObjPermission;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.comm.JODLocalClientInfo;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.protocol.JOSPPermissions;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ToDo: doc JODPermissions_002
 */
public class JODPermissions_002 implements JODPermissions {

    // Class constants

    public static final String TH_SYNC_NAME_FORMAT = "_PERM_SYNC_%s";
    public static final String ANONYMOUS_ID = JOSPPermissions.WildCards.USR_ANONYMOUS_ID.toString();
    public static final String ANONYMOUS_USERNAME = JOSPPermissions.WildCards.USR_ANONYMOUS_NAME.toString();


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JODSettings_002 locSettings;
    private final JODObjectInfo objInfo;
    private final JCPClient_Object jcpClient;
    private final JCPPermObj jcpPermissions;
    private List<ObjPermission> permissions;
    private Timer syncTimer = null;
    private boolean jcpPrintNotConnected = false;
    private JODCommunication comm;


    // Constructor

    /**
     * Default constructor.
     *
     * @param settings  the JOD settings.
     * @param objInfo   the object's info.
     * @param jcpClient the jcp object client.
     */
    public JODPermissions_002(JODSettings_002 settings, JODObjectInfo objInfo, JCPClient_Object jcpClient) throws PermissionsFileException {
        this.objInfo = objInfo;
        this.locSettings = settings;
        this.jcpClient = jcpClient;
        this.jcpPermissions = new JCPPermObj(jcpClient, settings);

        if (!locSettings.getPermissionsPath().exists())
            generatePermissions();

        try {
            loadPermissionsFromFile();
        } catch (FileNotFoundException ignore) {}

        logPermissions(permissions);
        log.debug(Mrk_JOD.JOD_PERM, "Object's permissions loaded");

        log.info(Mrk_JOD.JOD_PERM, String.format("Initialized JODPermissions instance for '%s' ('%s') object with '%s' owner", objInfo.getObjName(), objInfo.getObjId(), getOwnerId()));
    }

    private static void logPermissions(List<ObjPermission> permissions) {
        log.trace(Mrk_JOD.JOD_PERM, "  +--------------------+----------------------+----------------------+---------------------------+");
        log.trace(Mrk_JOD.JOD_PERM, "  | ObjID              | SrvId                | UsrId                | Connection and Perm.Type  |");
        log.trace(Mrk_JOD.JOD_PERM, "  +--------------------+----------------------+----------------------+---------------------------+");
        for (ObjPermission p : permissions)
            log.trace(Mrk_JOD.JOD_PERM, String.format("  | %-18s | %-20s | %-20s | %-13s, %-10s |", p.objId, p.srvId, p.usrId, p.connection, p.type));
        log.trace(Mrk_JOD.JOD_PERM, "  +--------------------+----------------------+----------------------+---------------------------+");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setCommunication(JODCommunication comm) throws JODStructure.CommunicationSetException {
        if (this.comm != null)
            throw new JODStructure.CommunicationSetException();
        this.comm = comm;
    }


    // JOD Component's interaction methods (from communication)

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canExecuteAction(String srvId, String usrId, JOSPPermissions.Connection connection) {
        if (getOwnerId().equals(JODSettings_002.JODPERM_OWNER_DEF)) {
            log.debug(Mrk_JOD.JOD_PERM, String.format("Status permission for srvID %s and usrID %s GRANTED because no obj's owner set", srvId, usrId));
            return true;
        }

        List<ObjPermission> inherentPermissions = search(srvId, usrId);

        if (inherentPermissions.isEmpty()) {
            log.debug(Mrk_JOD.JOD_PERM, String.format("Action permission for srvID %s and usrID %s DENIED  because no permission found for specified srv/usr", srvId, usrId));
            return false;
        }

        for (ObjPermission p : inherentPermissions) {
            if (connection == JOSPPermissions.Connection.LocalAndCloud
                    && p.connection == JOSPPermissions.Connection.OnlyLocal) {
                continue;
            }
            if (p.type == JOSPPermissions.Type.Actions
                    || p.type == JOSPPermissions.Type.CoOwner) {
                log.debug(Mrk_JOD.JOD_PERM, String.format("Action permission for srvID %s and usrID %s GRANTED", srvId, usrId));
                return true;
            }
        }

        log.debug(Mrk_JOD.JOD_PERM, String.format("Action permission for srvID %s and usrID %s DENIED", srvId, usrId));
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSendUpdate(String srvId, String usrId, JOSPPermissions.Connection connection) {
        if (getOwnerId().equals(JODSettings_002.JODPERM_OWNER_DEF)) {
            log.debug(Mrk_JOD.JOD_PERM, String.format("Status permission for srvID %s and usrID %s GRANTED because no obj's owner set", srvId, usrId));
            return true;
        }

        List<ObjPermission> inherentPermissions = search(srvId, usrId);

        if (inherentPermissions.isEmpty()) {
            log.debug(Mrk_JOD.JOD_PERM, String.format("Status permission for srvID %s and usrID %s DENIED  because no permission found for specified srv/usr", srvId, usrId));
            return false;
        }

        for (ObjPermission p : inherentPermissions) {
            if (connection == JOSPPermissions.Connection.LocalAndCloud
                    && p.connection == JOSPPermissions.Connection.OnlyLocal) {
                continue;
            }
            if (p.type == JOSPPermissions.Type.Status
                    || p.type == JOSPPermissions.Type.Actions
                    || p.type == JOSPPermissions.Type.CoOwner) {
                log.debug(Mrk_JOD.JOD_PERM, String.format("Status permission for srvID %s and usrID %s GRANTED", srvId, usrId));
                return true;
            }
        }

        log.debug(Mrk_JOD.JOD_PERM, String.format("Status permission for srvID %s and usrID %s DENIED", srvId, usrId));
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canActAsCoOwner(String srvId, String usrId, JOSPPermissions.Connection connection) {
        if (getOwnerId().equals(JODSettings_002.JODPERM_OWNER_DEF)) {
            log.debug(Mrk_JOD.JOD_PERM, String.format("Status permission for srvID %s and usrID %s GRANTED because no obj's owner set", srvId, usrId));
            return true;
        }

        List<ObjPermission> inherentPermissions = search(srvId, usrId);

        if (inherentPermissions.isEmpty()) {
            log.debug(Mrk_JOD.JOD_PERM, String.format("CoOwner permission for srvID %s and usrID %s DENIED  because no permission found for specified srv/usr", srvId, usrId));
            return false;
        }

        for (ObjPermission p : inherentPermissions) {
            if (connection == JOSPPermissions.Connection.LocalAndCloud
                    && p.connection == JOSPPermissions.Connection.OnlyLocal) {
                continue;
            }
            if (p.type == JOSPPermissions.Type.CoOwner) {
                log.debug(Mrk_JOD.JOD_PERM, String.format("CoOwner permission for srvID %s and usrID %s GRANTED", srvId, usrId));
                return true;
            }
        }

        log.debug(Mrk_JOD.JOD_PERM, String.format("CoOwner permission for srvID %s and usrID %s DENIED", srvId, usrId));
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void syncObjPermissions() {
        comm.sendToServices(JOSPProtocol_ObjectToService.createObjectPermsMsg(objInfo.getObjId(), objInfo.readPermissionsStr()), JOSPPermissions.Type.CoOwner);

        for (JODLocalClientInfo locConn : comm.getAllLocalClientsInfo()) {
            JOSPPermissions.Type permType;
            if (canActAsCoOwner(locConn.getSrvId(), locConn.getUsrId(), JOSPPermissions.Connection.OnlyLocal))
                permType = JOSPPermissions.Type.CoOwner;
            else if (canExecuteAction(locConn.getSrvId(), locConn.getUsrId(), JOSPPermissions.Connection.OnlyLocal))
                permType = JOSPPermissions.Type.Actions;
            else if (canSendUpdate(locConn.getSrvId(), locConn.getUsrId(), JOSPPermissions.Connection.OnlyLocal))
                permType = JOSPPermissions.Type.Status;
            else
                permType = JOSPPermissions.Type.None;
            try {
                comm.sendToSingleLocalService(locConn, JOSPProtocol_ObjectToService.createServicePermMsg(objInfo.getObjId(), permType, JOSPPermissions.Connection.OnlyLocal), permType);
            } catch (JODCommunication.ServiceNotConnected e) {
                log.warn(Mrk_JOD.JOD_PERM, String.format("Error on sending service's '%s' permission for object '%s' from JCP because %s", locConn.getFullSrvId(), objInfo.getObjId(), e.getMessage()));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void syncObjPermissionsJCP() {
        log.info(Mrk_JOD.JOD_PERM, String.format("Sync permission for '%s' object with JCP", objInfo.getObjId()));

        List<ObjPermission> prePermissions = permissions;

        try {
            log.debug(Mrk_JOD.JOD_PERM, String.format("Synchronizing permission for object '%s' from JCP", objInfo.getObjId()));
            refreshPermissionsFromJCP();
            log.debug(Mrk_JOD.JOD_PERM, String.format("Permission synchronized for object '%s' from JCP", objInfo.getObjId()));

        } catch (JCPClient.ConnectionException | JCPClient.RequestException e) {
            log.warn(Mrk_JOD.JOD_PERM, String.format("Error on synchronizing permissions for object '%s' from JCP because %s", objInfo.getObjId(), e.getMessage()));
            return;
        }

        if (prePermissions.size() == permissions.size()) {
            boolean allFounded = true;
            for (ObjPermission p1 : prePermissions) {
                boolean p1Founded = false;
                for (ObjPermission p2 : permissions)
                    if (p1.equals(p2)) {
                        p1Founded = true;
                        break;
                    }
                allFounded &= p1Founded;
            }
            if (allFounded)
                return;
        }

        try {
            log.debug(Mrk_JOD.JOD_PERM, String.format("Storing permission for object '%s' to local file '%s'", objInfo.getObjId(), locSettings.getPermissionsPath().getPath()));
            savePermissionsToFile();
            syncObjPermissions();
            log.debug(Mrk_JOD.JOD_PERM, String.format("Permission stored for object '%s' to local file '%s'", objInfo.getObjId(), locSettings.getPermissionsPath().getPath()));
        } catch (PermissionsNotSavedException e) {
            log.warn(Mrk_JOD.JOD_PERM, String.format("Error on storing permissions for object '%s' to local file because %s", objInfo.getObjId(), e.getMessage()));
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
    public boolean addPermissions(String usrId, String srvId, JOSPPermissions.Connection connection, JOSPPermissions.Type type) {
        log.info(Mrk_JOD.JOD_PERM, String.format("Add permission to '%s' object with srvID %s, usrID %s connection '%s' and type '%s'", objInfo.getObjId(), srvId, usrId, connection, type));

        if (usrId == null || usrId.isEmpty()) {
            log.warn(Mrk_JOD.JOD_PERM, String.format("Error on adding permission for '%s' object because usrId not set", objInfo.getObjId()));
            return false;
        }
        if (srvId == null || srvId.isEmpty()) {
            log.warn(Mrk_JOD.JOD_PERM, String.format("Error on adding permission for '%s' object because srvId not set", objInfo.getObjId()));
            return false;
        }

        ObjPermission duplicate = searchExact(srvId, usrId);
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

        ObjPermission duplicate = searchExact(srvId, usrId);
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

        locSettings.setOwnerId(ownerId);
        objInfo.regenerateObjId();
    }

    /**
     * {@inheritDoc}
     */
    public void resetOwnerId() {
        setOwnerId(ANONYMOUS_ID);
    }


    // Mngm methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void startAutoRefresh() {
        log.info(Mrk_JOD.JOD_PERM, String.format("Start JODPermissions auto-refresh for '%s' object", objInfo.getObjId()));

        startSyncTimer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopAutoRefresh() {
        log.info(Mrk_JOD.JOD_PERM, String.format("Stop JODPermissions auto-refresh for '%s' object", objInfo.getObjId()));

        if (isSync())
            stopSyncTimer();
    }


    // Permission mngm

    /**
     * Load permissions from file specified from
     * {@link JODSettings_002#getPermissionsPath()}
     * to the local {@link #permissions} field.
     */
    private void loadPermissionsFromFile() throws PermissionsNotLoadedException, FileNotFoundException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<ObjPermission> loadedPerms = mapper.readValue(locSettings.getPermissionsPath(), new TypeReference<List<ObjPermission>>() {});
            List<ObjPermission> validatedPerms = new ArrayList<>();
            for (ObjPermission p : loadedPerms)
                if (p.objId.compareTo(objInfo.getObjId()) == 0)
                    validatedPerms.add(p);
            permissions = validatedPerms;

        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw new PermissionsNotLoadedException(locSettings.getPermissionsPath().getAbsolutePath(), e);
        }
    }

    /**
     * Save {@link #permissions} field to file specified from
     * {@link JODSettings_002#getPermissionsPath()}.
     */
    private void savePermissionsToFile() throws PermissionsNotSavedException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(locSettings.getPermissionsPath(), permissions);
        } catch (IOException e) {
            throw new PermissionsNotSavedException(locSettings.getPermissionsPath().getAbsolutePath(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void regeneratePermissions() throws PermissionsFileException {
        // Clean old owner permissions
        generatePermissions();

        // Keep old owner permissions
//        List<ObjPermission> newObjIdPerms = new ArrayList<>();
//        for (ObjPermission p : permissions)
//            if (!p.objId.equals(objInfo.getObjId()))
//                newObjIdPerms.add( new ObjPermission(objInfo.getObjId(),p.usrId,p.srvId,p.connection,p.type,p.updatedAt));
//        permissions = newObjIdPerms;
//        savePermissionsToFile();

        try {
            loadPermissionsFromFile();
        } catch (FileNotFoundException ignore) {}

        syncObjPermissionsJCP();
    }

    private void generatePermissions() throws PermissionsNotSavedException {
        boolean genCloud = false;

        if (jcpClient.isConnected()) {
            try {
                log.debug(Mrk_JOD.JOD_PERM, "Generating object permissions from JCP");
                generatePermissionsFromJCP();
                genCloud = true;
                log.debug(Mrk_JOD.JOD_PERM, "Object permissions generated from JCP");

            } catch (JCPClient.ConnectionException | JCPClient.RequestException e) {
                log.warn(Mrk_JOD.JOD_PERM, String.format("Error on generating object permission from JCP because %s", e.getMessage()), e);
            }
        }

        if (!genCloud) {
            log.debug(Mrk_JOD.JOD_PERM, "Generating object permissions locally");
            generatePermissionsLocally();
            log.debug(Mrk_JOD.JOD_PERM, "Object permissions generated locally");
        }

        savePermissionsToFile();
    }

    /**
     * Request to JCP a valid set of object's permissions and set them to
     * {@link #permissions} field.
     */
    private void generatePermissionsFromJCP() throws JCPClient.ConnectionException, JCPClient.RequestException {
        permissions = jcpPermissions.generatePermissionsFromJCP();
    }

    /**
     * Generate a valid set of object's permissions and set them to
     * {@link #permissions} field.
     */
    private void generatePermissionsLocally() {
        permissions = new ArrayList<>();
        permissions.add(new ObjPermission(objInfo.getObjId(), JOSPPermissions.WildCards.USR_OWNER.toString(), JOSPPermissions.WildCards.SRV_ALL.toString(), JOSPPermissions.Connection.LocalAndCloud, JOSPPermissions.Type.CoOwner, new Date()));
        permissions.add(new ObjPermission(objInfo.getObjId(), JOSPPermissions.WildCards.USR_ALL.toString(), JOSPPermissions.WildCards.SRV_ALL.toString(), JOSPPermissions.Connection.OnlyLocal, JOSPPermissions.Type.CoOwner, new Date()));
    }

    /**
     * Sync object's permissions with JCP object's permissions.
     */
    private void refreshPermissionsFromJCP() throws JCPClient.ConnectionException, JCPClient.RequestException {
        permissions = jcpPermissions.refreshPermissionsFromJCP(permissions);
    }

    /**
     * Return the object's permission corresponding to given srvId and usrId.
     *
     * @param srvId the service's id.
     * @param usrId the user's id.
     * @return the object's permissions list.
     */
    private List<ObjPermission> search(String srvId, String usrId) {
        List<ObjPermission> inherentPermissions = new ArrayList<>();

        for (ObjPermission p : permissions) {
            boolean exact_usr = p.usrId.equals(usrId);
            boolean all_usr = p.usrId.equals(JOSPPermissions.WildCards.USR_ALL.toString());
            boolean owner = p.usrId.equals(JOSPPermissions.WildCards.USR_OWNER.toString())
                    && getOwnerId().equals(usrId);
            if (exact_usr || all_usr || owner) {
                boolean exact_srv = p.srvId.equals(srvId);
                boolean all_srv = p.srvId.equals(JOSPPermissions.WildCards.SRV_ALL.toString());
                if (exact_srv || all_srv) {
                    inherentPermissions.add(p);
                }
            }
        }

        return inherentPermissions;
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
    private ObjPermission searchExact(String srvId, String usrId) {
        for (ObjPermission p : permissions) {
            boolean exact_usr = p.usrId.equals(usrId);
            //boolean all_usr = p.usrId.equals(PermissionsTypes.WildCards.USR_ALL.toString());
            //boolean owner = p.usrId.equals(PermissionsTypes.WildCards.USR_OWNER.toString())
            //        && getOwnerId().equals(usrId);
            if (exact_usr) {
                boolean exact_srv = p.srvId.equals(srvId);
                //boolean all_srv = p.srvId.equals(PermissionsTypes.WildCards.SRV_ALL.toString());
                if (exact_srv) {
                    return p;
                }
            }
        }

        return null;
    }


    // Sync timer

    private boolean isSync() {
        return syncTimer != null;
    }

    private void startSyncTimer() {
        if (isSync())
            return;

        log.debug(Mrk_JOD.JOD_INFO, "Starting permissions sync's timer");
        syncTimer = new Timer(true);
        syncTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Thread.currentThread().setName(String.format(TH_SYNC_NAME_FORMAT, objInfo.getObjId()));
                if (jcpClient.isConnected()) {
                    syncObjPermissionsJCP();
                    jcpPrintNotConnected = false;

                } else if (!jcpPrintNotConnected) {
                    log.warn(Mrk_JOD.JOD_PERM, String.format("Can't sync object '%s''s permissions to JCP because JCP client disconnected", objInfo.getObjId()));
                    jcpPrintNotConnected = true;
                }
            }
        }, 0, locSettings.getPermissionsRefreshTime() * 1000);
        log.debug(Mrk_JOD.JOD_INFO, "Permissions sync's timer started");
    }

    private void stopSyncTimer() {
        if (!isSync())
            return;

        log.debug(Mrk_JOD.JOD_INFO, "Stopping permissions sync's timer");
        syncTimer.cancel();
        syncTimer = null;
        log.debug(Mrk_JOD.JOD_INFO, "Permissions sync's timer stopped");
    }

}
