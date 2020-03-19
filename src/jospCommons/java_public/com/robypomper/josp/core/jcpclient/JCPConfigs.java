package com.robypomper.josp.core.jcpclient;


/**
 * Main interface for {@link JCPClient} configs.
 */
public interface JCPConfigs {

    /**
     * @return the OAuth client id.
     */
    String getClientId();

    /**
     * @return the OAuth client secret.
     */
    String getClientSecrets();

    /**
     * @return the OAuth requested scopes.
     */
    String getScopes();

    /**
     * @return the url for OAuth callback flow.
     */
    String getCallback();

    /**
     * @return the auth server base url.
     */
    String getBaseUrl();

    /**
     * @return the auth server realm.
     */
    String getRealm();

}
