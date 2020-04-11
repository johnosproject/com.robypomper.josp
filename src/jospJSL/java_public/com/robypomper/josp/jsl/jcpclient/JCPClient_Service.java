package com.robypomper.josp.jsl.jcpclient;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.paths.APISrvs;


/**
 * Main JCP client interface for Services.
 * <p>
 * JCP Client must act as anonymous user client and as authenticated user client.
 * Depending on user login, all requests send to JCP must be performed using the
 * right client authentication flow. Basically, when the login code (from
 * Authentication Flow) is set ({@link #setLoginCode(String)}) then this class
 * starts to send request using the authenticated client. Otherwise it use the
 * anonymous client, that use the Client Credential Flow.
 * <p>
 * The user authentication process can be handle by external classes via the
 * {@link LoginManager}. It's an observer that throw login and logout events.
 * So his implementations can handle user's logins and logouts.
 */
public interface JCPClient_Service extends JCPClient {

    // Headers default values setters

    /**
     * When set the srvId will used as {@value APISrvs#HEADER_SRVID} header
     * value for each request send to the server.
     *
     * @param srvId the current service id, or null to reset it.
     */
    void setServiceId(String srvId);

    /**
     * When set the usrId will used as {@value APISrvs#HEADER_USRID} header
     * value for each request send to the server.
     *
     * @param usrId the current logged user id, or null to reset it.
     */
    void setUserId(String usrId);


    // User methods

    /**
     * @return <code>true</code> if user was not logged in.
     */
    boolean isCliCredFlowEnabled();

    /**
     * @return <code>true</code> if user was logged in successfully.
     */
    boolean isAuthCodeFlowEnabled();

    /**
     * The login url allow application user to login to the JCP cloud
     * and get the login code to use to get the access token.
     * <p>
     * After the user logged in to the JCP cloud, the server send as a response
     * a HTTP 302 redirect code with an url containing the login code.
     *
     * @return the url to use for user authentication.
     */
    String getLoginUrl();

    /**
     * Set the login code received after user login.
     * <p>
     * When received the login code after user login at url returned by
     * {@link #getLoginUrl()} method, it must be set to the JCPClient instance.
     */
    boolean setLoginCode(String code);

    /**
     * @return the refresh token from AuthFlow.
     */
    String getRefreshToken();

    /**
     * Set the stored refresh token to the AuthFlow client. This is equivalent
     * to user login via {@link #setLoginCode(String)}.
     * <p>
     * This method, called with a valid refresh token allow to prevent user
     * login for each session. The refresh token is stored by
     * <p>
     * When received the login code after user login at url returned by
     * {@link #getLoginUrl()} method, it must be set to the JCPClient instance.
     */
    boolean setRefreshToken(String refreshToken);

    /**
     * Reset the login code and all authentication tokens and credentials.
     * <p>
     * This method allow to logout current logged user.
     */
    boolean userLogout();


    // Login manager

    /**
     * Set the login manager to current JCP Client for services.
     *
     * @param loginMngr the LoginManager that handle user JCP client login and
     *                  logout.
     */
    void setLoginManager(LoginManager loginMngr);

    /**
     * Interface to implement LoginManger.
     * <p>
     * Login manager can handle success authentication and authentication process.
     */
    interface LoginManager {

        /**
         * Method execute on successfully user authentication.
         */
        void onLogin();

        /**
         * Method execute on successfully user deauthentication.
         */
        void onLogout();

    }

}
