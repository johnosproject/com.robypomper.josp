package com.robypomper.josp.core.jcpclient;

/**
 * Standard implementation of the {@link JCPConfigs} interface.
 */
public class DefaultJCPConfigs implements JCPConfigs {

    // Internal vars

    private final String clientId;
    private final String clientSecrets;
    private final String clientScopes;
    private final String clientCallback;
    private final String clientBaseUrl;
    private final String clientRealm;


    // Constructor

    /**
     * Default constructor.
     *
     * @param id       the OAuth client id.
     * @param secrets  the OAuth client secret.
     * @param scopes   the OAuth requested scopes.
     * @param callback the url for OAuth callback flow.
     * @param baseUrl  the auth server base url.
     * @param realm    the auth server realm.
     */
    public DefaultJCPConfigs(String id, String secrets, String scopes, String callback, String baseUrl, String realm) {
        this.clientId = id;
        this.clientSecrets = secrets;
        this.clientScopes = scopes;
        this.clientCallback = callback;
        this.clientBaseUrl = baseUrl;
        this.clientRealm = realm;
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientId() {
        return clientId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientSecrets() {
        return clientSecrets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getScopes() {
        return clientScopes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCallback() {
        return clientCallback;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBaseUrl() {
        return clientBaseUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRealm() {
        return clientRealm;
    }

}
