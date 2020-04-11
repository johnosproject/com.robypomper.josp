package com.robypomper.josp.jsl.systems;

import com.robypomper.josp.jsl.JSL_002;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.srvinfo.JCPServiceInfo;


public class JSLServiceInfo_002 implements JSLServiceInfo {

    // Internal vars

    private final JSL_002.Settings locSettings;
    private final JCPServiceInfo jcpSrvInfo;
    private JSLUserMngr userMngr;
    private JSLObjsMngr objs;
    private JSLCommunication comm;


    // Constructor

    /**
     * Create new service info.
     * <p>
     * This constructor create an instance of {@link JCPServiceInfo} and request
     * common/mandatory info for caching them.
     *
     * @param settings  the JSL settings.
     * @param jcpClient the JCP client.
     */
    public JSLServiceInfo_002(JSL_002.Settings settings, JCPClient_Service jcpClient) {
        System.out.println("DEB: JSL Service Info initialization...");
        this.locSettings = settings;
        this.jcpSrvInfo = new JCPServiceInfo(jcpClient);

        // force value caching
        String srvId = getSrvId();
        getSrvName();

        jcpClient.setServiceId(srvId);

        System.out.println("DEB: JSL Service Info initialized");
    }


    // Service's systems

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSystems(JSLUserMngr userMngr, JSLObjsMngr objs, JSLCommunication comm) {
        this.userMngr = userMngr;
        this.objs = objs;
        this.comm = comm;
    }


    // Srv's info

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSrvId() {
        if (locSettings.getSrvId().isEmpty()) {
            try {
                locSettings.setSrvId(jcpSrvInfo.getId());
            } catch (Throwable ignore) {
                ignore.printStackTrace();
            }
        }

        return locSettings.getSrvId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSrvName() {
        if (locSettings.getSrvName().isEmpty()) {
            try {
                locSettings.setSrvName(jcpSrvInfo.getName());
            } catch (Throwable ignore) {}
        }

        return locSettings.getSrvName();
    }


    // Users's info

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUserLogged() {
        return userMngr.isUserAuthenticated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserId() {
        return userMngr.getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername() {
        return userMngr.getUsername();
    }


    // Objects's info

    /**
     * {@inheritDoc}
     */
    @Override
    public int getConnectedObjectsCount() {
        return objs.getAllConnectedObjects().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getKnownObjectsCount() {
        return objs.getAllObjects().size();
    }


    // Mngm methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void startAutoRefresh() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopAutoRefresh() {

    }

}
