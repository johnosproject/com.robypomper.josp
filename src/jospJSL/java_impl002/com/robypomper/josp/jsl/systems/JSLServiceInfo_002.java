package com.robypomper.josp.jsl.systems;

import com.robypomper.josp.jsl.JSL_002;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.srvinfo.JCPServiceInfo;


/**
 *
 */
public class JSLServiceInfo_002 implements JSLServiceInfo {

    // Class constants

    String FULL_ID_FORMATTER = "%s/%s/%s";
    private static final String SRVNAME_OFFLINE = "NoSrvName-Offline";
    private static final String SRVID_OFFLINE = "ZZZZZ-ZZZZZ-ZZZZZ";


    // Internal vars

    private final JSL_002.Settings locSettings;
    private final JCPServiceInfo jcpSrvInfo;
    private JSLUserMngr userMngr;
    private JSLObjsMngr objs;
    private JSLCommunication comm;
    private String instanceId;


    // Constructor

    /**
     * Create new service info.
     * <p>
     * This constructor create an instance of {@link JCPServiceInfo} and request
     * common/mandatory info for caching them.
     *
     * @param settings   the JSL settings.
     * @param jcpClient  the JCP client.
     * @param instanceId the service instance id.
     */
    public JSLServiceInfo_002(JSL_002.Settings settings, JCPClient_Service jcpClient, String instanceId) {
        System.out.println("DEB: JSL Service Info initialization...");
        this.locSettings = settings;
        this.jcpSrvInfo = new JCPServiceInfo(jcpClient);
        this.instanceId = instanceId;

        // force value caching
        String srvId = getSrvId();
        getSrvName(!jcpClient.isConnected());

        jcpClient.setServiceId(srvId);

        System.out.println("DEB: JSL Service Info initialized");
    }


    // Service's systems

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSystems(JSLUserMngr userMngr, JSLObjsMngr objs) {
        this.userMngr = userMngr;
        this.objs = objs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCommunication(JSLCommunication comm) {
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
                if (!locSettings.getSrvId().isEmpty())
                    return locSettings.getSrvId();

                System.out.println("Service must be online at his first boot to get his id and other service's staffs.");
                return SRVID_OFFLINE;
            }
        }

        return locSettings.getSrvId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSrvName() {
        return getSrvName(true);
    }

    /**
     * Human readable service's name from cache if given param is <code>true</code>.
     * Otherwise it require the service's name from the JCP cloud.
     *
     * @param cached if <code>true</code>, then it get the value from local cache copy.
     * @return the service's name.
     */
    public String getSrvName(boolean cached) {
        if (!cached || locSettings.getSrvName().isEmpty())
            try {
                locSettings.setSrvName(jcpSrvInfo.getName());

            } catch (Throwable ignore) {
                if (!locSettings.getSrvName().isEmpty())
                    return locSettings.getSrvName();

                System.out.println("Service must be online at his first boot to get his name and other service's staffs.");
                ignore.printStackTrace();
                return SRVNAME_OFFLINE;
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


    // Instance and fullId

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInstanceId() {
        return instanceId;
    }


    /**
     * The full service id is composed by service and user ids.
     *
     * @return an id composed by service and user id.
     */
    public String getFullId() {
        return String.format(FULL_ID_FORMATTER, getSrvId(), getUserId(), getInstanceId());
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
