package com.robypomper.josp.jsl.user;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.params.permissions.PermissionsTypes;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 */
public class JSLUserMngr_002 implements JSLUserMngr, JCPClient_Service.LoginManager {

    // Class constants

    public static final String ANONYMOUS_ID = PermissionsTypes.WildCards.USR_ANONYMOUS_ID.toString();
    public static final String ANONYMOUS_USERNAME = PermissionsTypes.WildCards.USR_ANONYMOUS_NAME.toString();


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JSLSettings_002 locSettings;
    private final JCPClient_Service jcpClient;
    private final JCPUserSrv jcpUser;
    private String usrId;
    private String usrName;
    private JSLCommunication comm = null;


    // Constructor

    public JSLUserMngr_002(JSLSettings_002 settings, JCPClient_Service jcpClient) {
        this.locSettings = settings;
        this.jcpClient = jcpClient;
        jcpUser = new JCPUserSrv(jcpClient, settings);

        if (jcpClient.isAuthCodeFlowEnabled()) {
            log.trace(Mrk_JSL.JSL_USR, "Perform JSLUserMngr login");
            if (jcpClient.isConnected())
                onLogin();
            else
                onLocalLogin();
        } else {
            onLogout();
            log.trace(Mrk_JSL.JSL_USR, "Set JSLUserMngr with anonymous user");
        }

        log.debug(Mrk_JSL.JSL_USR, "Setting login manager to JCPClient");
        this.jcpClient.setLoginManager(this);
        log.debug(Mrk_JSL.JSL_USR, "Login manager set to JCPClient");

        log.info(Mrk_JSL.JSL_USR, String.format("Initialized JSLUserMngr instance for '%s' user with '%s' id", getUsername(), getUserId()));
    }


    // User's info

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUserAuthenticated() {
        return jcpClient.isAuthCodeFlowEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserId() {
        return usrId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername() {
        return usrName;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setCommunication(JSLCommunication communication) {
        this.comm = communication;
    }


    // LoginManager impl

    /**
     * {@inheritDoc}
     * <p>
     * Method to handle the user login.
     * <p>
     * This method is called onLogin event from
     * {@link com.robypomper.josp.jsl.jcpclient.JCPClient_Service.LoginManager}
     * and update the current user of JSL library.
     */
    @Override
    public void onLogin() {
        // Cache user's info
        log.debug(Mrk_JSL.JSL_USR, "Caching user's info from JCP");
        try {
            usrId = jcpUser.getUserId();
            usrName = jcpUser.getUsername();
            locSettings.setUsrId(usrId);
            locSettings.setUsrName(usrName);

        } catch (JCPClient.ConnectionException | JCPClient.RequestException e) {
            log.warn(Mrk_JSL.JSL_USR, String.format("Error on getting user id and name from JCP because %s", e.getMessage()), e);
            log.trace(Mrk_JSL.JSL_USR, "Set anonymous user");
            usrId = ANONYMOUS_ID;
            usrName = ANONYMOUS_USERNAME;
        }

        // Set JCP Client user id header
        jcpClient.setUserId(usrId);

        // Store user refresh token
        locSettings.setRefreshToken(jcpClient.getRefreshToken());

        log.info(Mrk_JSL.JSL_USR, String.format("Logged in user '%s' with id '%s'", usrName, usrId));

        if (comm.isCloudConnected()) {
            comm.disconnectCloud();
            try {
                comm.connectCloud();
            } catch (JSLCommunication.CloudCommunicationException e) {
                log.warn(Mrk_JSL.JSL_USR, String.format("Error on starting cloud communication on updating user id because %s", e.getMessage()), e);
            }
        }
        if (comm.isLocalRunning()) {
            try {
                comm.stopLocal();
                comm.startLocal();
            } catch (JSLCommunication.LocalCommunicationException e) {
                log.warn(Mrk_JSL.JSL_USR, String.format("Error on restart local communication on updating user id because %s", e.getMessage()), e);
            }
        }

    }

    /**
     * {@inheritDoc}
     *
     * Method to handle the user logout.
     * <p>
     * This method is called onLogout event from
     * {@link com.robypomper.josp.jsl.jcpclient.JCPClient_Service.LoginManager}
     * and reset the current user of JSL library to anonymous user.
     */
    @Override
    public void onLogout() {
        String loggedUsrId = usrId;
        String loggedUsername = usrName;

        usrId = ANONYMOUS_ID;
        usrName = ANONYMOUS_USERNAME;
        locSettings.setUsrId(null);
        locSettings.setUsrName(null);

        // Set JCP Client user id header
        jcpClient.setUserId(null);

        // Reset user refresh token
        locSettings.setRefreshToken(jcpClient.getRefreshToken());

        log.info(Mrk_JSL.JSL_USR, String.format("Logged out user '%s' with id '%s'", loggedUsername, loggedUsrId));

        if (comm != null) {
            if (comm.isCloudConnected()) {
                comm.disconnectCloud();
                try {
                    comm.connectCloud();
                } catch (JSLCommunication.CloudCommunicationException e) {
                    log.warn(Mrk_JSL.JSL_USR, String.format("Error on starting cloud communication on updating user id because %s", e.getMessage()), e);
                }
            }
            if (comm.isLocalRunning()) {
                try {
                    comm.stopLocal();
                    comm.startLocal();
                } catch (JSLCommunication.LocalCommunicationException e) {
                    log.warn(Mrk_JSL.JSL_USR, String.format("Error on restart local communication on updating user id because %s", e.getMessage()), e);
                }
            }
        }
    }

    /**
     * Method to handle the local user login.
     * <p>
     * This method is called by {@link JSLUserMngr_002} constructor when user is
     * already logged but the JCP client is not connected.
     * <p>
     * It read user's id and username from local settings.
     */
    private void onLocalLogin() {
        // Cache user's info
        log.debug(Mrk_JSL.JSL_USR, "Set user's info from settings");
        usrId = locSettings.getUsrId();
        usrName = locSettings.getUsrName();

        // Set JCP Client user id header
        jcpClient.setUserId(usrId);

        log.info(Mrk_JSL.JSL_USR, String.format("Logged in user '%s' with id '%s'", usrName, usrId));
    }

}
