package com.robypomper.josp.jsl.user;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 */
public class JSLUserMngr_002 implements JSLUserMngr, JCPClient_Service.LoginManager {

    // Class constants

    public static final String ANONYMOUS_ID = "00000-00000-00000";
    public static final String ANONYMOUS_USERNAME = "Anonymous";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JSLSettings_002 locSettings;
    private final JCPClient_Service jcpClient;
    private final JCPUserSrv jcpUser;


    // Constructor

    public JSLUserMngr_002(JSLSettings_002 settings, JCPClient_Service jcpClient) {
        this.locSettings = settings;
        this.jcpClient = jcpClient;
        jcpUser = new JCPUserSrv(jcpClient);

        if (jcpClient.isAuthCodeFlowEnabled()) {
            log.trace(Mrk_JSL.JSL_USR, "Perform JSLUserMngr login");
            onLogin();
        } else {
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
        if (jcpClient.isCliCredFlowEnabled())
            return ANONYMOUS_ID;

        if (!locSettings.getUsrId().isEmpty())
            return locSettings.getUsrId();

        String gen;
        try {
            gen = jcpUser.getUserId();

        } catch (JCPClient.ConnectionException | JCPClient.RequestException e) {
            log.warn(Mrk_JSL.JSL_USR, String.format("Error on getting user id from JCP because %s", e.getMessage()), e);
            return ANONYMOUS_ID;
        }

        locSettings.setUsrId(gen);
        return gen;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername() {
        return getUsername(true);
    }

    /**
     * Service user's username from cache if given param is <code>true</code>.
     * Otherwise it require the username from the JCP cloud.
     *
     * @param cached if <code>true</code>, then it get the value from local cache copy.
     * @return the service user's username.
     */
    public String getUsername(boolean cached) {
        if (jcpClient.isCliCredFlowEnabled())
            return ANONYMOUS_USERNAME;

        if (!locSettings.getUsrName().isEmpty() && cached)
            return locSettings.getUsrName();

        String gen;
        try {
            log.debug(Mrk_JSL.JSL_USR, "Getting user name from JCP");
            gen = jcpUser.getUsername();
            log.debug(Mrk_JSL.JSL_USR, String.format("User name '%s' get from JCP", gen));

        } catch (Throwable e) {
            log.warn(Mrk_JSL.JSL_USR, String.format("Error on getting service name from JCP because %s", e.getMessage()), e);
            return ANONYMOUS_USERNAME;
        }

        locSettings.setUsrName(gen);
        return gen;
    }


    // LoginManager impl

    /**
     * Method to handle the user login.
     * <p>
     * This method is called onLogin event from
     * {@link com.robypomper.josp.jsl.jcpclient.JCPClient_Service.LoginManager}
     * and update the current user of JSL library.
     */
    public void onLogin() {
        // Cache user's info
        log.debug(Mrk_JSL.JSL_USR, "Caching user's info from JCP");
        String loggedUsrId = getUserId();
        String loggedUsername = getUsername(!jcpClient.isConnected());
        log.debug(Mrk_JSL.JSL_USR, String.format("User's info cached as '%s' username and '%s' user id", loggedUsername, loggedUsrId));


        // Set JCP Client user id header
        jcpClient.setUserId(loggedUsrId);

        // Store user refresh token
        locSettings.setRefreshToken(jcpClient.getRefreshToken());

        log.info(Mrk_JSL.JSL_USR, String.format("Logged in user '%s' with id '%s'", loggedUsername, loggedUsrId));
    }

    /**
     * Method to handle the user logout.
     * <p>
     * This method is called onLogout event from
     * {@link com.robypomper.josp.jsl.jcpclient.JCPClient_Service.LoginManager}
     * and reset the current user of JSL library to anonymous user.
     */
    public void onLogout() {
        String loggedUsrId = getUserId();
        String loggedUsername = getUsername(!jcpClient.isConnected());

        locSettings.setUsrId("");
        locSettings.setUsrName("");

        // Set JCP Client user id header
        jcpClient.setUserId(null);

        // Reset user refresh token
        locSettings.setRefreshToken(jcpClient.getRefreshToken());

        log.info(Mrk_JSL.JSL_USR, String.format("Logged out user '%s' with id '%s'", loggedUsername, loggedUsrId));
    }

}
