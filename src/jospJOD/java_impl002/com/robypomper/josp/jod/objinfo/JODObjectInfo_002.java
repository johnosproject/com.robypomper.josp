package com.robypomper.josp.jod.objinfo;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;


/**
 * This is the JOD object info implementation.
 * <p>
 * This implementation collect all object's info from local
 * {@link com.robypomper.josp.jod.JOD.Settings} or via JCP Client request at
 * API Objs via the support class {@link JCPObjectInfo}.
 */
public class JODObjectInfo_002 implements JODObjectInfo {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JODSettings_002 locSettings;
    private final JCPClient_Object jcpClient;
    private final JCPObjectInfo jcpObjInfo;
    private JODStructure structure;
    private JODExecutorMngr executorMngr;
    private JODCommunication comm;
    private JODPermissions permissions;
    private final String jodVersion;


    // Constructor

    /**
     * Create new object info.
     * <p>
     * This constructor create an instance of {@link JCPObjectInfo} and request
     * common/mandatory info for caching them.
     *
     * @param settings   the JOD settings.
     * @param jcpClient  the JCP client.
     * @param jodVersion the current JOD implementation version.
     */
    public JODObjectInfo_002(JODSettings_002 settings, JCPClient_Object jcpClient, String jodVersion) {
        this.locSettings = settings;
        this.jcpClient = jcpClient;
        this.jcpObjInfo = new JCPObjectInfo(jcpClient, locSettings);
        this.jodVersion = jodVersion;

        // force value caching
        getObjIdHw();
        if (!isObjIdSet())
            generateObjId();
        getObjName();

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
        if (locSettings.getObjName().isEmpty()) {
            log.debug(Mrk_JOD.JOD_INFO, "Generating locally object name");
            String generated = LocalObjectInfo.generateObjName();
            locSettings.setObjName(generated);
            log.debug(Mrk_JOD.JOD_INFO, String.format("Object name generated locally '%s'", generated));
        }

        return locSettings.getObjName();
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
        } catch (IOException e) {
            throw new RuntimeException("Error on structure string loading, check JOD configs.");
        }
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


    // Permissions's info

    /**
     * {@inheritDoc}
     */
    @Override
    public String readPermissionsStr() {
        try {
            return readFile(locSettings.getPermissionsPath());
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

        if (isSync()) {
            jcpClient.removeConnectListener(syncListener);
            isSync = false;
        }
    }

    public void syncObjInfo() {
        try {
            log.debug(Mrk_JOD.JOD_INFO, "Sync object Info to JCP");
            jcpObjInfo.registerOrUpdate(this);
            log.debug(Mrk_JOD.JOD_INFO, "Object Info synchronized to JCP");

            if (isSync()) {
                jcpClient.removeConnectListener(syncListener);
                isSync = false;
            }

        } catch (JCPClient.RequestException | JCPClient.ConnectionException e) {
            log.warn(Mrk_JOD.JOD_INFO, String.format("Error on sync object Info to JCP because %s", e.getMessage()));
            if (!isSync()) {
                jcpClient.addConnectListener(syncListener);
                isSync = true;
            }
        }
    }


    // Sync listener

    private boolean isSync = false;

    private boolean isSync() {
        //return syncTimer != null;
        return isSync;
    }

    @SuppressWarnings("Convert2Lambda")
    private final JCPClient.ConnectListener syncListener = new JCPClient.ConnectListener() {
        @Override
        public void onConnected(JCPClient jcpClient) {
            syncObjInfo();
        }
    };


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

    /**
     * The Hardware ID is the id that allow to identify a physical object.
     * <p>
     * It help to identify same physical object also when the Object ID was reset.
     *
     * @return the object's Hardware ID.
     */
    private String getObjIdHw() {
        if (locSettings.getObjIdHw().isEmpty()) {
            log.debug(Mrk_JOD.JOD_INFO, "Generating locally object HW ID");
            String generated = LocalObjectInfo.generateObjIdHw();
            locSettings.setObjIdHw(generated);
            log.debug(Mrk_JOD.JOD_INFO, String.format("Object HW ID generated locally '%s'", generated));
        }

        return locSettings.getObjIdHw();
    }


    // Obj's id

    /**
     * {@inheritDoc}
     */
    @Override
    public void regenerateObjId() {
        locSettings.setObjIdCloud("");
        generateObjId();
    }

    private void generateObjId() {
        try {
            log.debug(Mrk_JOD.JOD_INFO, "Generating on cloud object ID");
            String generated = jcpObjInfo.generateObjIdCloud(getObjIdHw(), getOwnerId());
            log.debug(Mrk_JOD.JOD_INFO, "Object ID generated on cloud");

            saveObjId(generated);
            if (isGenerating()) {
                jcpClient.removeConnectListener(generatingListener);
                isGenerating = false;
            }
            log.info(Mrk_JOD.JOD_INFO, String.format("Object ID generated on cloud '%s'", getObjId()));
            return;

        } catch (JCPClient.RequestException | JCPClient.ConnectionException e) {
            log.warn(Mrk_JOD.JOD_INFO, String.format("Error on generating on cloud object ID because %s", e.getMessage()));
            if (!isGenerating()) {
                jcpClient.addConnectListener(generatingListener);
                isGenerating = true;
            }
        }

        if (!isObjIdSet()) {
            log.debug(Mrk_JOD.JOD_INFO, "Generating locally a temporay object ID");
            String generated = String.format("%s-00000-00000", getObjIdHw());
            log.debug(Mrk_JOD.JOD_INFO, "Temporary Object ID generated locally");

            saveObjId(generated);
            log.info(Mrk_JOD.JOD_INFO, String.format("Object ID generated locally '%s'", getObjId()));
        }
    }

    private void saveObjId(String generatedObjId) {
        locSettings.setObjIdCloud(generatedObjId);
        jcpClient.setObjectId(generatedObjId);

        if (permissions != null) {
            try {
                permissions.regeneratePermissions();
            } catch (JODPermissions.PermissionsFileException e) {
                log.warn(Mrk_JOD.JOD_INFO, String.format("Error on regenerate permissions because %s", e.getMessage()), e);
            }
        }

        // ToDo update local services
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isObjIdSet() {
        return !getObjId().isEmpty();
    }


    // Generating timer

    private boolean isGenerating = false;

    private boolean isGenerating() {
        return isGenerating;
    }

    @SuppressWarnings("Convert2Lambda")
    private final JCPClient.ConnectListener generatingListener = new JCPClient.ConnectListener() {
        @Override
        public void onConnected(JCPClient jcpClient) {
            generateObjId();
        }
    };

}
