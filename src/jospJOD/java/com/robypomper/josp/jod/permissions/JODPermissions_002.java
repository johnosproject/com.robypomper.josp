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

import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.comm.JODLocalClientInfo;
import com.robypomper.josp.jod.events.Events;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * ToDo: doc JODPermissions_002
 */
public class JODPermissions_002 implements JODPermissions {

    // Class constants

    public static final String ANONYMOUS_ID = JOSPPerm.WildCards.USR_ANONYMOUS_ID.toString();
    public static final String ANONYMOUS_USERNAME = JOSPPerm.WildCards.USR_ANONYMOUS_NAME.toString();


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JODSettings_002 locSettings;
    private final JODObjectInfo objInfo;
    private final JCPClient_Object jcpClient;
    private final JCPPermObj jcpPermissions;
    private List<JOSPPerm> permissions;
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

        logPermissions();
        log.debug(Mrk_JOD.JOD_PERM, "Object's permissions loaded");

        log.info(Mrk_JOD.JOD_PERM, String.format("Initialized JODPermissions instance for '%s' ('%s') object with '%s' owner", objInfo.getObjName(), objInfo.getObjId(), getOwnerId()));
    }

    private void logPermissions() {
        String all = JOSPPerm.logPermissions(permissions);
        for (String s : all.split("\n"))
            log.trace(Mrk_JOD.JOD_PERM, s);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void syncObjPermissions() {
        String permStr = JOSPPerm.toString(permissions);
        comm.sendToServices(JOSPProtocol_ObjectToService.createObjectPermsMsg(objInfo.getObjId(), permStr), JOSPPerm.Type.CoOwner);

        for (JODLocalClientInfo locConn : comm.getAllLocalClientsInfo()) {
            if (!locConn.isConnected())
                continue;

            JOSPPerm.Type permType = getServicePermission(locConn.getSrvId(), locConn.getUsrId(), JOSPPerm.Connection.OnlyLocal);
            try {
                comm.sendToSingleLocalService(locConn, JOSPProtocol_ObjectToService.createServicePermMsg(objInfo.getObjId(), permType, JOSPPerm.Connection.OnlyLocal), permType);
            } catch (JODCommunication.ServiceNotConnected e) {
                log.warn(Mrk_JOD.JOD_PERM, String.format("Error on sending service's '%s' permission for object '%s' from JCP because %s", locConn.getFullSrvId(), objInfo.getObjId(), e.getMessage()));
            }
        }
    }

    // Access methods

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JOSPPerm> getPermissions() {
        return Collections.unmodifiableList(permissions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkPermission(String srvId, String usrId, JOSPPerm.Type minReqPerm, JOSPPerm.Connection connType) {
        if (getOwnerId().equals(JODSettings_002.JODPERM_OWNER_DEF)) {
            log.debug(Mrk_JOD.JOD_PERM, String.format("Permission %s for srvID %s and usrID %s GRANTED because no obj's owner set", minReqPerm, srvId, usrId));
            return true;
        }

        List<JOSPPerm> inherentPermissions = search(srvId, usrId);
        if (inherentPermissions.isEmpty()) {
            log.debug(Mrk_JOD.JOD_PERM, String.format("Permission %s for srvID %s and usrID %s DENIED  because no permission found for specified srv/usr", minReqPerm, srvId, usrId));
            return false;
        }

        for (JOSPPerm p : inherentPermissions) {
            if (connType == JOSPPerm.Connection.LocalAndCloud
                    && p.getConnType() == JOSPPerm.Connection.OnlyLocal)
                continue;

            if (p.getPermType().compareTo(minReqPerm) >= 0) {
                log.debug(Mrk_JOD.JOD_PERM, String.format("Permission %s for srvID %s and usrID %s GRANTED", minReqPerm, srvId, usrId));
                return true;
            }
        }

        log.debug(Mrk_JOD.JOD_PERM, String.format("Permission %s for srvID %s and usrID %s DENIED", minReqPerm, srvId, usrId));
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JOSPPerm.Type getServicePermission(String srvId, String usrId, JOSPPerm.Connection connType) {
        if (getOwnerId().equals(JODSettings_002.JODPERM_OWNER_DEF))
            return JOSPPerm.Type.CoOwner;

        List<JOSPPerm> inherentPermissions = search(srvId, usrId);
        if (inherentPermissions.isEmpty())
            return JOSPPerm.Type.None;

        JOSPPerm.Type highPerm = JOSPPerm.Type.None;
        for (JOSPPerm p : inherentPermissions) {
            if (connType == JOSPPerm.Connection.LocalAndCloud
                    && p.getConnType() == JOSPPerm.Connection.OnlyLocal)
                continue;

            if (p.getPermType().compareTo(highPerm) > 0)
                highPerm = p.getPermType();
        }

        return highPerm;
    }

    /**
     * {@inheritDoc}
     */
    public boolean addPermissions(String srvId, String usrId, JOSPPerm.Type type, JOSPPerm.Connection connection) {
        log.info(Mrk_JOD.JOD_PERM, String.format("Add permission to '%s' object with srvID %s, usrID %s connection '%s' and type '%s'", objInfo.getObjId(), srvId, usrId, connection, type));

        if (usrId == null || usrId.isEmpty()) {
            log.warn(Mrk_JOD.JOD_PERM, String.format("Error on adding permission for '%s' object because usrId not set", objInfo.getObjId()));
            return false;
        }
        if (srvId == null || srvId.isEmpty()) {
            log.warn(Mrk_JOD.JOD_PERM, String.format("Error on adding permission for '%s' object because srvId not set", objInfo.getObjId()));
            return false;
        }

        JOSPPerm newPerm = new JOSPPerm(objInfo.getObjId(), srvId, usrId, type, connection, new Date());
        permissions.add(newPerm);
        Events.registerPermAdded(newPerm);

        comm.syncObject();
        try {
            savePermissionsToFile();
        } catch (PermissionsNotSavedException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean updPermissions(String permId, String srvId, String usrId, JOSPPerm.Type type, JOSPPerm.Connection connection) {
        log.info(Mrk_JOD.JOD_PERM, String.format("Update permission to '%s' object with srvID %s, usrID %s connection '%s' and type '%s'", objInfo.getObjId(), srvId, usrId, connection, type));


        JOSPPerm existingPerm = search(permId);
        if (existingPerm == null)
            return false;

        // replace existing with (toDELETE) permission
        JOSPPerm newDelPerm = new JOSPPerm(existingPerm.getId(), existingPerm.getObjId(), srvId, usrId, type, connection, JOSPProtocol.getNowDate());
        permissions.remove(existingPerm);
        permissions.add(newDelPerm);
        Events.registerPermUpdated(existingPerm,newDelPerm);

        comm.syncObject();
        try {
            savePermissionsToFile();
        } catch (PermissionsNotSavedException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean remPermissions(String permId) {
        log.info(Mrk_JOD.JOD_PERM, String.format("Remove permission to '%s' object with permId %s", objInfo.getObjId(), permId));

        JOSPPerm oldPerm = search(permId);
        if (oldPerm == null)
            return false;

        permissions.remove(oldPerm);
        Events.registerPermRemoved(oldPerm);

        comm.syncObject();
        try {
            savePermissionsToFile();
        } catch (PermissionsNotSavedException e) {
            e.printStackTrace();
        }
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

        String oldOwner = getOwnerId();
        locSettings.setOwnerId(ownerId);

        Events.registerInfoUpd("objOwner", oldOwner, ownerId);
        Thread regenerateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                objInfo.regenerateObjId();
                // cause regenerate Permissions and object info sync
            }
        });
        regenerateThread.setName("REGENERATE_OBJ_ID");
        regenerateThread.start();
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
    public void startAutoRefresh() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopAutoRefresh() {}


    // Permission mngm

    /**
     * Load permissions from file specified from
     * {@link JODSettings_002#getPermissionsPath()}
     * to the local {@link #permissions} field.
     */
    private void loadPermissionsFromFile() throws PermissionsNotLoadedException, FileNotFoundException {
        try {
            String permsStr = String.join("\n", Files.readAllLines(locSettings.getPermissionsPath().toPath()));
            List<JOSPPerm> loadedPerms = JOSPPerm.listFromString(permsStr);
            List<JOSPPerm> validatedPerms = new ArrayList<>();
            for (JOSPPerm p : loadedPerms)
                if (p.getObjId().compareTo(objInfo.getObjId()) == 0)
                    validatedPerms.add(p);
            permissions = validatedPerms;
            Events.registerPermLoaded("Load permissions from file",permissions);

        } catch (FileNotFoundException e) {
            Events.registerPermLoaded("Load permissions from file", e);
            throw e;

        } catch (JOSPProtocol.ParsingException | IOException e) {
            Events.registerPermLoaded("Load permissions from file", e);
            throw new PermissionsNotLoadedException(locSettings.getPermissionsPath().getAbsolutePath(), e);
        }
    }

    /**
     * Save {@link #permissions} field to file specified from
     * {@link JODSettings_002#getPermissionsPath()}.
     */
    private void savePermissionsToFile() throws PermissionsNotSavedException {
        try {
            Files.write(locSettings.getPermissionsPath().toPath(), Arrays.asList(JOSPPerm.toString(permissions).split("\n")));
            Events.registerPermLoaded("Save permissions to file",permissions);

        } catch (IOException retry) {
            try {
                Files.write(locSettings.getPermissionsPath().toPath(), Arrays.asList(JOSPPerm.toString(permissions).split("\n")));
                Events.registerPermLoaded("Save permissions to file",permissions);

            } catch (IOException e) {
                Events.registerPermLoaded("Save permissions to file", e);
                throw new PermissionsNotSavedException(locSettings.getPermissionsPath().getAbsolutePath(), e);
            }
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
//        List<JOSPPerm> newObjIdPerms = new ArrayList<>();
//        for (JOSPPerm p : permissions)
//            if (!p.objId.equals(objInfo.getObjId()))
//                newObjIdPerms.add( new JOSPPerm(objInfo.getObjId(),p.usrId,p.srvId,p.connection,p.type,p.updatedAt));
//        permissions = newObjIdPerms;
//        savePermissionsToFile();

        try {
            loadPermissionsFromFile();
        } catch (FileNotFoundException ignore) {}

        syncObjPermissions();
    }

    private void generatePermissions() throws PermissionsNotSavedException {
        boolean genCloud = false;

        if (jcpClient.isConnected()) {
            try {
                log.debug(Mrk_JOD.JOD_PERM, "Generating object permissions from JCP");
                generatePermissionsFromJCP();
                genCloud = true;
                log.debug(Mrk_JOD.JOD_PERM, "Object permissions generated from JCP");
                Events.registerPermLoaded("Gen permissions on cloud",permissions);

            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
                log.warn(Mrk_JOD.JOD_PERM, String.format("Error on generating object permission from JCP because %s", e.getMessage()), e);
                Events.registerPermLoaded("Gen permissions on cloud", e);
            }
        }

        if (!genCloud) {
            log.debug(Mrk_JOD.JOD_PERM, "Generating object permissions locally");
            generatePermissionsLocally();
            log.debug(Mrk_JOD.JOD_PERM, "Object permissions generated locally");
            Events.registerPermLoaded("Gen permissions locally",permissions);
        }

        savePermissionsToFile();
    }

    /**
     * Request to JCP a valid set of object's permissions and set them to
     * {@link #permissions} field.
     */
    private void generatePermissionsFromJCP() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.RequestException, JCPClient2.ResponseException {
        permissions = jcpPermissions.generatePermissionsFromJCP();
    }

    /**
     * Generate a valid set of object's permissions and set them to
     * {@link #permissions} field.
     */
    private void generatePermissionsLocally() {
        permissions = new ArrayList<>();
        permissions.add(new JOSPPerm(objInfo.getObjId(), JOSPPerm.WildCards.SRV_ALL.toString(), JOSPPerm.WildCards.USR_OWNER.toString(), JOSPPerm.Type.CoOwner, JOSPPerm.Connection.LocalAndCloud, JOSPProtocol.getNowDate()));
        permissions.add(new JOSPPerm(objInfo.getObjId(), JOSPPerm.WildCards.SRV_ALL.toString(), JOSPPerm.WildCards.USR_ALL.toString(), JOSPPerm.Type.CoOwner, JOSPPerm.Connection.OnlyLocal, JOSPProtocol.getNowDate()));
    }

    private JOSPPerm search(String permId) {
        for (JOSPPerm p : permissions)
            if (p.getId().equals(permId))
                return p;

        return null;
    }

    /**
     * Return the object's permission corresponding to given srvId and usrId.
     *
     * @param srvId the service's id.
     * @param usrId the user's id.
     * @return the object's permissions list.
     */
    @Deprecated
    private List<JOSPPerm> search(String srvId, String usrId) {
        List<JOSPPerm> inherentPermissions = new ArrayList<>();

        for (JOSPPerm p : permissions) {
            boolean exact_usr = p.getUsrId().equals(usrId);
            boolean all_usr = p.getUsrId().equals(JOSPPerm.WildCards.USR_ALL.toString());
            boolean owner = p.getUsrId().equals(JOSPPerm.WildCards.USR_OWNER.toString())
                    && getOwnerId().equals(usrId);
            if (exact_usr || all_usr || owner) {
                boolean exact_srv = p.getSrvId().equals(srvId);
                boolean all_srv = p.getSrvId().equals(JOSPPerm.WildCards.SRV_ALL.toString());
                if (exact_srv || all_srv) {
                    inherentPermissions.add(p);
                }
            }
        }

        return inherentPermissions;
    }

}
