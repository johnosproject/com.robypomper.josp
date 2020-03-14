package com.robypomper.josp.jod.systems;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jod.JOD_002;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.objinfo.JCPObjectInfo;
import com.robypomper.josp.jod.objinfo.LocalObjectInfo;


/**
 * This is the JOD object info implementation.
 * <p>
 * This implementation collect all object's info from local
 * {@link com.robypomper.josp.jod.JOD.Settings} or via JCP Client request at
 * API Objs via the support class {@link JCPObjectInfo}.
 */
public class JODObjectInfo_002 implements JODObjectInfo {

    // Internal vars

    private final JOD_002.Settings locSettings;
    private final JCPObjectInfo jcpObjInfo;


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
        getObjId();
        getObjName();

        System.out.println("DEB: JOD Object Info initialized");
    }


    // Obj's info

    /**
     * {@inheritDoc}
     */
    @Override
    public String getObjId() {
        return String.format("%s-%s", getObjIdHw(), getObjIdCloud());
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
        return "";
        // ToDo: implement JODObjectInfo_002::getOwnerId()
    }


    // Mngm methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void startAutoRefresh() {
        System.out.println("DEB: JODObjectInfo_002::startAutoRefresh()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopAutoRefresh() {
        System.out.println("DEB: JODObjectInfo_002::stopAutoRefresh()");
    }


    // Private methods

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
    private String getObjIdCloud() {
        String generated;

        if (!locSettings.getObjIdCloud().isEmpty()) {
            generated = locSettings.getObjIdCloud();

        } else {
            generated = "00000-00000";
            if (!getOwnerId().isEmpty()) {
                try {
                    generated = jcpObjInfo.generateObjIdCloud(getObjIdHw(), getOwnerId());
                    System.out.println(String.format("INF: generated object Cloud ID '%s'", generated));
                    locSettings.setObjIdCloud(generated);
                } catch (JCPClient.RequestException | JCPClient.ConnectionException ignore) {}
            }
        }

        return generated;
    }

}
