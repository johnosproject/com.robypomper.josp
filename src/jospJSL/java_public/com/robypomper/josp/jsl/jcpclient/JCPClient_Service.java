package com.robypomper.josp.jsl.jcpclient;

import com.robypomper.josp.core.jcpclient.JCPClient2;
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
 * {@link com.robypomper.josp.core.jcpclient.JCPClient2.LoginListener}. It's an observer that throw login and logout events.
 * So his implementations can handle user's logins and logouts.
 */
public interface JCPClient_Service extends JCPClient2 {

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

}
