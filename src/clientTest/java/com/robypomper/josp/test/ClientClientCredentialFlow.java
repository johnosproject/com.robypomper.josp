package com.robypomper.josp.test;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class ClientClientCredentialFlow extends AbsClientFlow {

    public static final String AUTH_FLOW_NAME = "ClientCredentialsFlow";

    public ClientClientCredentialFlow(ClientSettings clientSettings) {
        super(clientSettings);
    }

    @Override
    protected OAuth2AccessToken getAccessToken(OAuth20Service service)
            throws InterruptedException, ExecutionException, IOException {
        System.out.println("=== Client Credentials Flow ===");

        final OAuth2AccessToken accessToken = service.getAccessTokenClientCredentialsGrant();
        System.out.println(accessToken.getRawResponse());
        return accessToken;
    }

}
