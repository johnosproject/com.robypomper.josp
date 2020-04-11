package com.robypomper.josp.core.jcpclient;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


/**
 * JCPClient implementation for OAuth2 Client Credentials Flow.
 */
public class JCPClient_CliCredFlow extends AbsJCPClient {

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
        final OAuth2AccessToken accessToken;
        try {
            accessToken = service.getAccessTokenClientCredentialsGrant();

        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new ConnectionException(String.format("Error requiring the access token because %s.", e.getMessage()), e);
        }
        return accessToken;
    }

}
