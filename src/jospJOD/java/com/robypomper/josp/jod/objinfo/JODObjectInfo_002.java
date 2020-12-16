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

package com.robypomper.josp.jod.objinfo;

import com.robypomper.josp.clients.JCPAPIsClientObj;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.clients.apis.obj.APIObjsClient;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.events.Events;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.charset.Charset;
import java.nio.file.Files;


/**
 * This is the JOD object info implementation.
 * <p>
 * This implementation collect all object's info from local
 * {@link com.robypomper.josp.jod.JOD.Settings} or via JCP Client request at
 * API Objs via the support class {@link APIObjsClient}.
 */
public class JODObjectInfo_002 implements JODObjectInfo {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JODSettings_002 locSettings;
    private final JCPAPIsClientObj jcpClient;
    private final APIObjsClient apiObjsClient;
    private JODStructure structure;
    private JODExecutorMngr executorMngr;
    private JODCommunication comm;
    private JODPermissions permissions;
    private final String jodVersion;
    private final GenerateConnectionListener generatingListener = new GenerateConnectionListener();


    // Constructor

    /**
     * Create new object info.
     * <p>
     * This constructor create an instance of {@link APIObjsClient} and request
     * common/mandatory info for caching them.
     *
     * @param settings   the JOD settings.
     * @param jcpClient  the JCP client.
     * @param jodVersion the current JOD implementation version.
     */
    public JODObjectInfo_002(JODSettings_002 settings, JCPAPIsClientObj jcpClient, String jodVersion) {
        this.locSettings = settings;
        this.jcpClient = jcpClient;
        this.apiObjsClient = new APIObjsClient(jcpClient);
        this.jodVersion = jodVersion;

        // force value caching
        if (getObjIdHw().isEmpty())
            generateObjIdHw();
        if (!isObjIdSet()) {
            log.info(Mrk_JOD.JOD_INFO, "Object's id not set, generating new one");
            generateObjId();
        } else {
            String objId = getObjId();
            saveObjId(objId);
            if (!objId.endsWith("00000-00000")) {
                log.info(Mrk_JOD.JOD_INFO, String.format("Object's id '%s' load and set", objId));
            } else {
                log.info(Mrk_JOD.JOD_INFO, String.format("Object's id '%s' is local, try re-generate cloud obj id", objId));
                regenerateObjId(objId);
            }
        }
        if (getObjName().isEmpty())
            generateObjName();

        log.info(Mrk_JOD.JOD_INFO, String.format("Initialized JODObjectInfo instance for '%s' object with '%s' id", getObjName(), getObjId()));
        log.debug(Mrk_JOD.JOD_INFO, String.format("                                   and '%s' id HW ", getObjIdHw()));
    }


    // Object's systems

    /**
     * {@inheritDoc}
     */
    public void setSystems(JODStructure structure, JODExecutorMngr executorMngr, JODCommunication comm, JODPermissions permissions) {
        log.trace(Mrk_JOD.JOD_INFO, String.format("JODObjectInfo for object '%s' set JOD's systems", getObjId()));

        this.structure = structure;
        this.executorMngr = executorMngr;
        this.comm = comm;
        this.permissions = permissions;
    }


    // Obj's info

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJODVersion() {
        return jodVersion;
    }

    /**
     * The Hardware ID is the id that allow to identify a physical object.
     * <p>
     * It help to identify same physical object also when the Object ID was reset.
     *
     * @return the object's Hardware ID.
     */
    private String getObjIdHw() {
        return locSettings.getObjIdHw();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getObjId() {
        return locSettings.getObjIdCloud();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getObjName() {
        return locSettings.getObjName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setObjName(String newName) {
        locSettings.setObjName(newName);
        syncObjInfo();
    }


    // Users's info

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOwnerId() {
        return locSettings.getOwnerId();
    }


    // Structure's info

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStructurePath() {
        return locSettings.getStructurePath().getPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readStructureStr() {
        try {
            return readFile(locSettings.getStructurePath());
        } catch (ClosedByInterruptException ignore) {
            return "";
        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_INFO, String.format("Error on structure string loading from '%s' file because %s check JOD configs", locSettings.getStructurePath(), e.getMessage()), e);
            throw new RuntimeException(String.format("Error on structure string loading from '%s' file check JOD configs", locSettings.getStructurePath()), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStructForJSL() throws JODStructure.ParsingException {
        return structure.getStructForJSL();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermsForJSL() {
        return JOSPPerm.toString(permissions.getPermissions());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBrand() {
        return structure.getRoot().getBrand();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModel() {
        return structure.getRoot().getModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLongDescr() {
        return structure.getRoot().getDescr();
    }


    // Permissions info

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermissionsPath() {
        return locSettings.getPermissionsPath().getPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readPermissionsStr() {
        try {
            return readFile(locSettings.getPermissionsPath());
        } catch (ClosedByInterruptException ignore) {
            return "";
        } catch (IOException e) {
            throw new RuntimeException("Error on permissions string loading, check JOD configs.");
        }
    }


    // Mngm methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void startAutoRefresh() {
        assert structure != null
                && executorMngr != null
                && comm != null
                && permissions != null;

        log.info(Mrk_JOD.JOD_INFO, String.format("Start JODObjectInfo auto-refresh for '%s' object", getObjId()));

        syncObjInfo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopAutoRefresh() {
        log.info(Mrk_JOD.JOD_INFO, String.format("Stop JODObjectInfo auto-refresh for '%s' object", getObjId()));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void syncObjInfo() {
        comm.sendToServices(JOSPProtocol_ObjectToService.createObjectInfoMsg(getObjId(), getObjName(), getJODVersion(), getOwnerId(), getModel(), getBrand(), getLongDescr(), comm.isCloudConnected()), JOSPPerm.Type.Status);
    }


    // Private methods

    /**
     * Read given file and return his content in a String.
     *
     * @return the string with file content.
     */
    private String readFile(File file) throws IOException {
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded, Charset.defaultCharset());
    }


    // Obj's id

    @Override
    public void regenerateObjId() {
        regenerateObjId(getObjId());
    }

    private void generateObjId() {
        regenerateObjId(null);
    }

    private void regenerateObjId(String oldObjId) {
        try {
            log.debug(Mrk_JOD.JOD_INFO, "Generating new object ID on cloud");
            String generated = apiObjsClient.generateObjIdCloud(getObjIdHw(), getOwnerId(), oldObjId);
            log.debug(Mrk_JOD.JOD_INFO, "Object ID generated on cloud");

            Events.registerInfoUpd("objId", oldObjId, generated);

            saveObjId(generated);
            if (isGenerating()) {
                jcpClient.removeConnectionListener(generatingListener);
                isGenerating = false;
            }
            log.info(Mrk_JOD.JOD_INFO, String.format("Object ID generated on cloud '%s'", getObjId()));
            return;

        } catch (JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException | JCPClient2.ResponseException e) {
            log.warn(Mrk_JOD.JOD_INFO, String.format("Error on generating on cloud object ID because %s", e.getMessage()));
            Events.registerInfoUpd("objId", e);
            if (!isGenerating()) {
                jcpClient.addConnectionListener(generatingListener);
                isGenerating = true;
            }
        }

        if (!isObjIdSet()) {
            log.debug(Mrk_JOD.JOD_INFO, "Generating locally a temporary object ID");
            String generated = String.format("%s-00000-00000", getObjIdHw());
            log.debug(Mrk_JOD.JOD_INFO, "Temporary Object ID generated locally");
            Events.registerInfoUpd("objId", generated);

            saveObjId(generated);
            log.info(Mrk_JOD.JOD_INFO, String.format("Object ID generated locally '%s'", getObjId()));
        }
    }

    private void saveObjId(String generatedObjId) {
        // Stop local communication
        boolean wasLocalRunning = false;
        if (comm != null && comm.isLocalRunning()) {
            wasLocalRunning = true;
            try {
                comm.stopLocal();

            } catch (JODCommunication.LocalCommunicationException e) {
                log.warn(Mrk_JOD.JOD_INFO, "Error on stopping local communication on save object's id");
            }
        }
        // Stop cloud communication
        boolean wasCloudRunning = false;
        if (comm != null && comm.isCloudConnected()) {
            wasCloudRunning = true;
            comm.disconnectCloud();
        }

        // Store and dispatch new obj id
        locSettings.setObjIdCloud(generatedObjId);
        jcpClient.setObjectId(generatedObjId);
        if (permissions != null) {
            try {
                permissions.regeneratePermissions();
            } catch (JODPermissions.PermissionsFileException e) {
                log.warn(Mrk_JOD.JOD_INFO, String.format("Error on regenerate permissions because %s", e.getMessage()), e);
            }
        }
        if (structure != null)
            syncObjInfo();

        // Start cloud communication
        if (wasCloudRunning) {
            try {
                comm.connectCloud();

            } catch (JODCommunication.CloudCommunicationException e) {
                log.warn(Mrk_JOD.JOD_INFO, "Error on re-connecting to cloud on save object's id");
            }
        }

        // Start local communication
        if (wasLocalRunning) {
            try {
                comm.startLocal();

            } catch (JODCommunication.LocalCommunicationException e) {
                log.warn(Mrk_JOD.JOD_INFO, "Error on starting local communication on save object's id");
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isObjIdSet() {
        return !getObjId().isEmpty();
    }


    // Generators

    private void generateObjIdHw() {
        log.debug(Mrk_JOD.JOD_INFO, "Generating locally object HW ID");
        String generated = LocalObjectInfo.generateObjIdHw();
        locSettings.setObjIdHw(generated);
        log.debug(Mrk_JOD.JOD_INFO, String.format("Object HW ID generated locally '%s'", generated));
    }

    private void generateObjName() {
        log.debug(Mrk_JOD.JOD_INFO, "Generating locally object name");
        String generated = LocalObjectInfo.generateObjName();
        locSettings.setObjName(generated);
        log.debug(Mrk_JOD.JOD_INFO, String.format("Object name generated locally '%s'", generated));
    }


    // Generating listener

    private boolean isGenerating = false;

    private boolean isGenerating() {
        return isGenerating;
    }

    class GenerateConnectionListener implements JCPClient2.ConnectionListener {

        @Override
        public void onConnected(JCPClient2 jcpClient) {
            regenerateObjId(JODObjectInfo_002.this.getObjId());
        }

        @Override
        public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
        }

        @Override
        public void onAuthenticationFailed(JCPClient2 jcpClient, Throwable t) {
        }

        @Override
        public void onDisconnected(JCPClient2 jcpClient) {
        }
    }

}
