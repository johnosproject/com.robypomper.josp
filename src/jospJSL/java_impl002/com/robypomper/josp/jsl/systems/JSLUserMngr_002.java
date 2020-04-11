package com.robypomper.josp.jsl.systems;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jsl.JSL_002;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.user.JCPUserSrv;


public class JSLUserMngr_002 implements JSLUserMngr, JCPClient_Service.LoginManager {

    // Class constants

    public static final String ANONYMOUS_ID = "00000-00000-00000";
    public static final String ANONYMOUS_USERNAME = "Anonymous";

    // Internal vars

    private final JSL_002.Settings settings;
    private final JCPClient_Service jcpClient;
    private final JCPUserSrv jcpUser;
    private String usrId;
    private String username;


    // Constructor

    public JSLUserMngr_002(JSL_002.Settings settings, JCPClient_Service jcpClient) {
        this.settings = settings;
        this.jcpClient = jcpClient;
        this.jcpClient.setLoginManager(this);
        jcpUser = new JCPUserSrv(jcpClient);
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

        if (usrId != null) {
            try {
                usrId = jcpUser.getUserId();
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

        return usrId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername() {
        if (jcpClient.isCliCredFlowEnabled())
            return ANONYMOUS_USERNAME;

        if (username != null) {
            try {
                username = jcpUser.getUserId();
            } catch (JCPClient.ConnectionException e) {
                System.out.println(String.format("WAR: can't get user id because %s", e.getMessage())); // No connection error???
                e.printStackTrace();
                return ANONYMOUS_USERNAME;

            } catch (JCPClient.RequestException e) {
                System.out.println(String.format("WAR: can't get user id because %s", e.getMessage())); // User not authenticated error???
                e.printStackTrace();
                return ANONYMOUS_USERNAME;
            }
        }

        return username;
    }

    /**
     * Reset all user's cached info.
     * <p>
     * This method is call on user logout.
     */
    private void invalidateCachedData() {
        usrId = null;
        username = null;
    }


    // LoginManager impl

    public void onLogin() {
        // Cache user's info
        String loggedUsrId = getUserId();
        getUsername();

        System.out.println(String.format("INF: user '%s' logged in.", loggedUsrId));

        // Set JCP Client user id header
        jcpClient.setUserId(loggedUsrId);
    }

    public void onLogout() {
        String loggedUsrId = getUserId();

        invalidateCachedData();

        // Set JCP Client user id header
        jcpClient.setUserId(null);

        System.out.println(String.format("INF: user '%s' logged out.", loggedUsrId));
    }

}
