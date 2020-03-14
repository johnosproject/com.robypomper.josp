package com.robypomper.josp.core.jcpclient;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


/**
 * JCPClient implementation for OAuth2 Client Credentials Flow.
 */
public abstract class JCPClient_CliCredFlow extends AbsJCPClient {

    // Constructor

    /**
     * Default constructor.
     *
     * @param configs     configs to use for JCPClient creation.
     * @param autoConnect if <code>true</code>, then the client will connect to
     *                    JCP immediately after clienti initialization.
     */
    protected JCPClient_CliCredFlow(JCPConfigs configs, boolean autoConnect) {
        super(configs, autoConnect);
    }


    // Authentication function

    /**
     * {@inheritDoc}
     * <p>
     * Send an access token request to authorization server.
     */
    @Override
    protected OAuth2AccessToken getAccessToken(OAuth20Service service) throws InterruptedException, ExecutionException, IOException {
        final OAuth2AccessToken accessToken;
        accessToken = service.getAccessTokenClientCredentialsGrant();
        return accessToken;
    }

}
