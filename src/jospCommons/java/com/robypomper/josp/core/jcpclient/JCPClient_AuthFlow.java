package com.robypomper.josp.core.jcpclient;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


/**
 * JCPClient implementation for OAuth2 Authentication Code Flow.
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

}
