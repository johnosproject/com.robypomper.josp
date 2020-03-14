package com.robypomper.josp.core.jcpclient;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


/**
 * JCPClient implementation for OAuth2 Authentication Code Flow.
 */
public abstract class JCPClient_AuthFlow extends AbsJCPClient {

    // Constructor

    /**
     * Default constructor.
     *
     * @param configs     configs to use for JCPClient creation.
     * @param autoConnect if <code>true</code>, then the client will connect to
     *                    JCP immediately after clienti initialization.
     */
    protected JCPClient_AuthFlow(JCPConfigs configs, boolean autoConnect) {
        super(configs, autoConnect);
    }


    // Authentication function

    /**
     * {@inheritDoc}
     * <p>
     * ToDo: implement JCPClient_AuthFlow#getAccessToken method
     */
    @Override
    protected OAuth2AccessToken getAccessToken(OAuth20Service service) {
        throw new NotImplementedException();
    }
}
