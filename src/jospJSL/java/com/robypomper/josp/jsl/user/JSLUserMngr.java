package com.robypomper.josp.jsl.user;


import com.robypomper.josp.jsl.comm.JSLCommunication;

/**
 * Interface for JSL User management system.
 * <p>
 * This system allow to manage current user: get user info and access to his
 * preferences.
 * <p>
 * Before a service can access to user info and preference, it must require the
 * permission to the user. This class allow service to require this permissions.
 *
 * <b>NB!</b>: user must be logged in/out via
 * {@link com.robypomper.josp.jsl.jcpclient.JCPClient_Service}
 */
public interface JSLUserMngr {

    // User's info

    /**
     * Check to {@link com.robypomper.josp.jsl.jcpclient.JCPClient_Service} if
     * current service authenticated user or not.
     *
     * @return <code>true</code> if current service authenticated user with
     * user's login.
     */
    boolean isUserAuthenticated();

    /**
     * The logged user ID.
     *
     * @return logged user ID, if user was not logged then it return
     * <code>null</code>.
     */
    String getUserId();

    /**
     * The logged user ID.
     *
     * @return logged user ID, if user was not logged then it return
     * <code>null</code>.
     */
    String getUsername();


    void setCommunication(JSLCommunication comm);


    // User's settings

    // ToDo: implements user's specific settings (local/cloud)
    // String getUserSetting(String key);


    // User/Service settings

    // ToDo: implements user's related to current service settings (local/cloud)
    // String getUserSrvSetting(String key);

}
