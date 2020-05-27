package com.robypomper.josp.jsl.srvinfo;

import com.robypomper.josp.jsl.JSL_002;
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
        this.locSettings = settings;
        this.instanceId = instanceId;
        this.jcpSrvInfo = new JCPServiceInfo(jcpClient);

        // force value caching
        log.debug(Mrk_JSL.JSL_INFO, "Getting service's id and name");
        String srvId = getSrvId();
        getSrvName(!jcpClient.isConnected());
        log.debug(Mrk_JSL.JSL_INFO, "Service's id and name got");

        log.debug(Mrk_JSL.JSL_INFO, "Setting service's id to JCPClient");
        jcpClient.setServiceId(srvId);
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
        if (!locSettings.getSrvId().isEmpty())
            return locSettings.getSrvId();

        String gen;
        try {
            log.debug(Mrk_JSL.JSL_INFO, "Getting service id from JCP");
            gen = jcpSrvInfo.getId();
            log.debug(Mrk_JSL.JSL_INFO, String.format("Service id '%s' get from JCP", gen));

        } catch (Throwable e) {
            log.warn(Mrk_JSL.JSL_INFO, String.format("Error on getting service id from JCP because %s", e.getMessage()), e);
            return SRVID_OFFLINE;
        }

        locSettings.setSrvId(gen);
        return gen;
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
        if (!locSettings.getSrvName().isEmpty() && cached)
            return locSettings.getSrvName();

        String gen;
        try {
            log.debug(Mrk_JSL.JSL_INFO, "Getting service name from JCP");
            gen = jcpSrvInfo.getName();
            log.debug(Mrk_JSL.JSL_INFO, String.format("Service name '%s' get from JCP", gen));

        } catch (Throwable e) {
            log.warn(Mrk_JSL.JSL_INFO, String.format("Error on getting service name from JCP because %s", e.getMessage()), e);
            return SRVNAME_OFFLINE;
        }

        locSettings.setSrvName(gen);
        return gen;
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
