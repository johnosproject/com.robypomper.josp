package com.robypomper.josp.core.jcpclient;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.robypomper.java.JavaSSLIgnoreChecks;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLHandshakeException;
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

    private static final Logger log = LogManager.getLogger();
    private String code = null;


    // Constructor

    /**
     * Default constructor.
     *
     * @param configs     configs to use for JCPClient creation.
     * @param autoConnect if <code>true</code>, then the client will connect to
     *                    JCP immediately after clienti initialization.
     */
    public JCPClient_AuthFlow(JCPConfigs configs, boolean autoConnect) throws ConnectionSettingsException {
        super(configs, autoConnect);
    }


    // Authentication function

    /**
     * {@inheritDoc}
     */
    @Override
    protected OAuth2AccessToken getAccessToken(OAuth20Service service) throws ConnectionException {
        log.debug(Mrk_Commons.COMM_JCPCL, "Getting the JCPClient access token");

        if (getLoginCode() == null && getRefreshToken() == null)
            throw new ConnectionException("Before request the access token, must be set the authentication code or the refresh token.");
        final String code = getLoginCode();

        OAuth2AccessToken tmpAccessToken;
        try {
            try {
                tmpAccessToken = code != null ? service.getAccessToken(code) : service.refreshAccessToken(getRefreshToken());

            } catch (SSLHandshakeException e) {
                log.trace(Mrk_Commons.COMM_JCPCL, String.format("Error on SSL handshaking with JCP because %s", e.getMessage()));

                try {
                    log.debug(Mrk_Commons.COMM_JCPCL, "Add localhost JCP verifier");
                    JavaSSLIgnoreChecks.disableSSLChecks(JavaSSLIgnoreChecks.LOCALHOST);
                    log.debug(Mrk_Commons.COMM_JCPCL, "Localhost JCP verifier added");

                    tmpAccessToken = code != null ? service.getAccessToken(code) : service.refreshAccessToken(getRefreshToken());

                } catch (SSLHandshakeException e1) {
                    log.warn(Mrk_Commons.COMM_JCPCL, String.format("Error on SSL handshaking with localhost JCP because %s", e.getMessage()), e);
                    throw new ConnectionException("Error on SSL handshaking with localhost JCP", e);

                } catch (JavaSSLIgnoreChecks.JavaSSLIgnoreChecksException javaSSLIgnoreChecksException) {
                    log.warn(Mrk_Commons.COMM_JCPCL, String.format("Error on add localhost JCP verifier because %s", e.getMessage()), e);
                    throw new ConnectionException("Error on add localhost JCP verifier", e);
                }
            }

        } catch (IOException | InterruptedException | ExecutionException e) {
            log.warn(Mrk_Commons.COMM_JCPCL, String.format("Error on getting the JCPClient access token because %s", e.getMessage()), e);
            throw new ConnectionException("Error on getting the access token", e);
        }

        final OAuth2AccessToken accessToken = tmpAccessToken;

        log.debug(Mrk_Commons.COMM_JCPCL, "JCPClient access token got");
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
        if (getOAuthService() == null) {
            try {
                setupService();
            } catch (ConnectionSettingsException e) {/*Already testd in AbsJCPClient constructor*/}
        }
        return getOAuthService().getAuthorizationUrl();
    }

    /**
     * @return the login code received after user login.
     */
    public String getLoginCode() {
        return code;
    }

    /**
     * Set the login code received after user login.
     * <p>
     * When received the login code after user login at url returned by
     * {@link #getLoginUrl()} method, it must be set to the JCPClient instance.
     */
    public boolean setLoginCode(String code) {
        log.debug(Mrk_Commons.COMM_JCPCL, "Setting JCPClient login code");

        this.code = code;
        try {
            connect();

        } catch (ConnectionException e) {
            log.warn(Mrk_Commons.COMM_JCPCL, String.format("Error on connecting after code set because %s", e.getMessage()), e);
            this.code = null;
            return false;
        }

        log.debug(Mrk_Commons.COMM_JCPCL, String.format("JCPClient login code set and%s connected", isConnected() ? "" : "NOT "));
        return isConnected();
    }

}
