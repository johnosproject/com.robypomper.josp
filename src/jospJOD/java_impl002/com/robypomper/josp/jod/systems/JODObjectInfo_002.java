package com.robypomper.josp.jod.systems;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jod.JOD_002;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.objinfo.JCPObjectInfo;
import com.robypomper.josp.jod.objinfo.LocalObjectInfo;

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

    // Class constants

    private static final String DEF_STRUCTURE = "";


    // Internal vars

    private final JOD_002.Settings locSettings;
    private final JCPObjectInfo jcpObjInfo;
    private JODStructure structure;
    private JODExecutorMngr executorMngr;
    private JODCommunication comm;
    private JODPermissions permissions;


    // Constructor

    /**
     * Create new object info.
     * <p>
     * This constructor create an instance of {@link JCPObjectInfo} and request
     * common/mandatory info for caching them.
     *
     * @param settings  the JOD settings.
     * @param jcpClient the JCP client.
     */
    public JODObjectInfo_002(JOD_002.Settings settings, JCPClient_Object jcpClient) {
        System.out.println("DEB: JOD Object Info initialization...");
        this.locSettings = settings;
        this.jcpObjInfo = new JCPObjectInfo(jcpClient);

        // force value caching
        getObjIdHw();
        String objId = getObjId();
        getObjName();

        jcpClient.setObjectId(objId);

        System.out.println("DEB: JOD Object Info initialized");
    }


    // Object's systems

    /**
     * {@inheritDoc}
     */
    public void setSystems(JODStructure structure, JODExecutorMngr executorMngr, JODCommunication comm, JODPermissions permissions) {
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
    public String getObjId() {
        if (!locSettings.getObjIdCloud().isEmpty())
            return locSettings.getObjIdCloud();

        String gen = generateObjId();
        locSettings.setObjIdCloud(gen);
        return gen;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getObjName() {
        if (locSettings.getObjName().isEmpty()) {
            String generated = LocalObjectInfo.generateObjName();
            System.out.println(String.format("INF: locally generated object name '%s'.", generated));
            locSettings.setObjName(generated);
        }

        return locSettings.getObjName();
    }


    // Users's info

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOwnerId() {
        if (locSettings.getOwnerId().isEmpty()) {
            try {
                locSettings.setOwnerId(permissions.getOwner());
            } catch (Throwable ignore) {}
        }
        return locSettings.getOwnerId();
    }


    // Structure's info

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStructureStr() {
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
    public String getPermissionsStr() {
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

        try {
            if (!jcpObjInfo.isRegistered()) {
                if (jcpObjInfo.register(this))
                    System.out.println(String.format("INF: object '%s' registered to JCP successfully.", getObjId()));
                else
                    System.out.println(String.format("ERR: error on register object '%s' to JCP successfully.", getObjId()));
            } else {
                if (jcpObjInfo.update(this))
                    System.out.println(String.format("INF: object '%s' updated to JCP successfully.", getObjId()));
                else
                    System.out.println(String.format("ERR: error on update object '%s' to JCP successfully.", getObjId()));
            }

        } catch (JCPClient.ConnectionException e) {
            System.out.println("WAR: Can't check if objects is registred on JCP, because JCP not available.");
        } catch (JCPClient.RequestException e) {
            System.out.println("ERR: Error on JCP request for object registration check.");
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopAutoRefresh() {
        System.out.println("WAR: JOD Object Info AutoRefresh can't stopped: not implemented");
        // ToDo: implement object disconnection to API (send disconnect status...)
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

    /**
     * The Hardware ID is the id that allow to identify a physical object.
     * <p>
     * It help to identify same physical object also when the Object ID was reset.
     *
     * @return the object's Hardware ID.
     */
    private String getObjIdHw() {
        if (locSettings.getObjIdHw().isEmpty()) {
            String generated = LocalObjectInfo.generateObjIdHw();
            System.out.println(String.format("INF: locally generated object Hardware ID '%s'.", generated));
            locSettings.setObjIdHw(generated);
        }

        return locSettings.getObjIdHw();
    }

    /**
     * The Cloud ID is the id generated from the JCP using the Hardware ID and
     * the object's owner User Id.
     *
     * @return the object's Cloud ID.
     */
    private String generateObjId() {
        String generated;

        try {
            generated = jcpObjInfo.generateObjIdCloud(getObjIdHw(), getOwnerId());
            System.out.println(String.format("INF: generated object ID '%s'", generated));
        } catch (JCPClient.RequestException | JCPClient.ConnectionException ignore) {
            generated = String.format("%s-00000-00000", getObjIdHw());
            System.out.println(String.format("INF: generated object ID (locally)'%s'", generated));
        }

        return generated;
    }

}
