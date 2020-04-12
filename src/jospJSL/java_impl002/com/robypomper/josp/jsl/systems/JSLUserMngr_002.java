package com.robypomper.josp.jsl.systems;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jsl.JSL_002;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.user.JCPUserSrv;


/**
 *
 */
public class JSLUserMngr_002 implements JSLUserMngr, JCPClient_Service.LoginManager {

    // Class constants

    public static final String ANONYMOUS_ID = "00000-00000-00000";
    public static final String ANONYMOUS_USERNAME = "Anonymous";


    // Internal vars

    private final JSL_002.Settings settings;
    private final JCPClient_Service jcpClient;
    private final JCPUserSrv jcpUser;


    // Constructor

    public JSLUserMngr_002(JSL_002.Settings settings, JCPClient_Service jcpClient) {
        this.settings = settings;
        this.jcpClient = jcpClient;
        this.jcpClient.setLoginManager(this);
        jcpUser = new JCPUserSrv(jcpClient);

        if (jcpClient.isAuthCodeFlowEnabled())
            onLogin();
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

        if (settings.getUsrId().isEmpty()) {
            try {
                settings.setUsrId(jcpUser.getUserId());

            } catch (JCPClient.ConnectionException e) {
                System.out.println(String.format("WAR: can't get user id because %s", e.getMessage())); // No connection error???
                e.printStackTrace();
                return ANONYMOUS_ID;

            } catch (JCPClient.RequestException e) {
                System.out.println(String.format("WAR: can't get user id because %s", e.getMessage())); // User not authenticated error???
                e.printStackTrace();
                return ANONYMOUS_ID;
            }
        }

        return settings.getUsrId();
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

        if (!cached || settings.getUsrName().isEmpty()) {
            try {
                settings.setUsrName(jcpUser.getUsername());

            } catch (JCPClient.ConnectionException e) {
                System.out.println(String.format("WAR: can't get username because %s", e.getMessage())); // No connection error???
                e.printStackTrace();
                return ANONYMOUS_USERNAME;

            } catch (JCPClient.RequestException e) {
                System.out.println(String.format("WAR: can't get username because %s", e.getMessage())); // User not authenticated error???
                e.printStackTrace();
                return ANONYMOUS_USERNAME;
            }
        }

        return settings.getUsrName();
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
        String loggedUsrId = getUserId();
        String username = getUsername(!jcpClient.isConnected());

        // Set JCP Client user id header
        jcpClient.setUserId(loggedUsrId);

        // Store user refresh token
        settings.setRefreshToken(jcpClient.getRefreshToken());

        System.out.println(String.format("INF: user '%s' logged in.", username));
    }

    /**
     * Method to handle the user logout.
     * <p>
     * This method is called onLogout event from
     * {@link com.robypomper.josp.jsl.jcpclient.JCPClient_Service.LoginManager}
     * and reset the current user of JSL library to anonymous user.
     */
    public void onLogout() {
        String username = getUsername();

        settings.setUsrId("");
        settings.setUsrName("");

        // Set JCP Client user id header
        jcpClient.setUserId(null);

        // Reset user refresh token
        settings.setRefreshToken(jcpClient.getRefreshToken());

        System.out.println(String.format("INF: user '%s' logged out.", username));
    }

}
