package com.robypomper.josp.core.jcpclient;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


/**
 * JCPClient implementation for OAuth2 Client Credentials Flow.
 */
public class JCPClient_CliCredFlow extends AbsJCPClient {

    // Internal vars

    private static final Logger log = LogManager.getLogger();


    // Constructor

    /**
     * Default constructor.
     *
     * @param configs     configs to use for JCPClient creation.
     * @param autoConnect if <code>true</code>, then the client will connect to
     *                    JCP immediately after clienti initialization.
     */
    public JCPClient_CliCredFlow(JCPConfigs configs, boolean autoConnect) throws ConnectionException {
        super(configs, autoConnect);
    }


    // Authentication function

    /**
     * {@inheritDoc}
     * <p>
     * Send an access token request to authorization server.
     */
    @Override
    protected OAuth2AccessToken getAccessToken(OAuth20Service service) throws ConnectionException {
        log.debug(Mrk_Commons.COMM_JCPCL, "Getting the JCPClient access token");

        final OAuth2AccessToken accessToken;
        try {
            accessToken = service.getAccessTokenClientCredentialsGrant();

        } catch (IOException | InterruptedException | ExecutionException e) {
            log.warn(Mrk_Commons.COMM_JCPCL, String.format("Error on getting the JCPClient access token because %s", e.getMessage()), e);
            throw new ConnectionException("Error on getting the access token", e);
        }

        log.debug(Mrk_Commons.COMM_JCPCL, "JCPClient access token got");
        return accessToken;
    }

}
