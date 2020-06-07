package com.robypomper.josp.jsl.srvinfo;

import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 */
public class JSLServiceInfo_002 implements JSLServiceInfo {

    // Class constants

    String FULL_ID_FORMATTER = "%s/%s/%s";
    private static final String SRVNAME_OFFLINE = "NoSrvName-Offline";
    private static final String SRVID_OFFLINE = "ZZZZZ-ZZZZZ-ZZZZZ";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JSLSettings_002 locSettings;
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
    public JSLServiceInfo_002(JSLSettings_002 settings, JCPClient_Service jcpClient, String instanceId) {
        this.locSettings = settings;
        this.instanceId = instanceId;
        this.jcpSrvInfo = new JCPServiceInfo(jcpClient, settings);

        log.debug(Mrk_JSL.JSL_INFO, "Setting service's id to JCPClient");
        jcpClient.setServiceId(getSrvId());
        log.debug(Mrk_JSL.JSL_INFO, "Service's id set to JCPClient");

        log.info(Mrk_JSL.JSL_INFO, String.format("Initialized JSLServiceInfo instance for '%s' service with '%s' id", getSrvName(), getSrvId()));
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
        return locSettings.getSrvId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSrvName() {
        return locSettings.getSrvName();
    }


    // Users's info

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUserLogged() {
        if (userMngr == null)
            throw new SystemNotSetException("UserMngr");

        return userMngr.isUserAuthenticated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserId() {
        if (userMngr == null)
            throw new SystemNotSetException("UserMngr");

        return userMngr.getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername() {
        if (userMngr == null)
            throw new SystemNotSetException("UserMngr");

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
        if (objs == null)
            throw new SystemNotSetException("ObjsMngr");

        return objs.getAllConnectedObjects().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getKnownObjectsCount() {
        if (objs == null)
            throw new SystemNotSetException("ObjsMngr");

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
