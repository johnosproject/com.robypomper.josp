package com.robypomper.josp.core.jcpclient;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


/**
 * JCPClient implementation for OAuth2 Authentication Code Flow.
 * <p>
 * This class provide also method for user login. It doesn't perform user login
 * but provide the login url, and a method to set the authorization code received
 * after user login (at url login).<br>
 * This class support also user login across application reboot using the
 * refresh token. The refresh token (that can be stored after user login) can
 * be set after/during application startup and it works as user login but it don't
 * require to the user to type his password again.
 */
public class JCPClient_AuthFlow extends AbsJCPClient {

    // Internal vars

    private String code = null;


    // Constructor

    /**
     * Default constructor.
     *
     * @param configs     configs to use for JCPClient creation.
     * @param autoConnect if <code>true</code>, then the client will connect to
     *                    JCP immediately after clienti initialization.
     */
    public JCPClient_AuthFlow(JCPConfigs configs, boolean autoConnect) throws ConnectionException {
        super(configs, autoConnect);
    }


    // Authentication function

    /**
     * {@inheritDoc}
     */
    @Override
    protected OAuth2AccessToken getAccessToken(OAuth20Service service) throws ConnectionException {
        if (code == null)
            throw new ConnectionException("Before request the access token, must be set the authentication token.");
        final String code = this.code;

        final OAuth2AccessToken accessToken;
        try {
            accessToken = service.getAccessToken(code);

        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new ConnectionException(String.format("Error requiring the access token because %s.", e.getMessage()), e);
        }

        return accessToken;
    }


    // User login methods

    /**
     * The login url allow application user to login to the JCP cloud
     * and get the login code to use to get the access token with
     * {@link #getAccessToken(OAuth20Service)} method.
     * <p>
     * After the user logged in to the JCP cloud, the server send as a response
     * a HTTP 302 redirect code with an url containing the login code.
     *
     * @return the url to use for user authentication.
     */
    public String getLoginUrl() {
        return getOAuthService().getAuthorizationUrl();
    }

    /**
     * Set the login code received after user login.
     * <p>
     * When received the login code after user login at url returned by
     * {@link #getLoginUrl()} method, it must be set to the JCPClient instance.
     */
    public boolean setLoginCode(String code) {
        this.code = code;
        try {
            connect();

        } catch (ConnectionException e) {
            return false;
        }

        return isConnected();
    }

}
