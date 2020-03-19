package com.robypomper.josp.test;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface ClientRequestFlow {

    String AUTH_CHECK = "authCheck";
    String OBJ_REG = "objRegistration";
    String USR_REG = "usrRegistration";
    String KEYCLOAK = "keycloakTest";

    void exec(OAuth20Service service, OAuth2AccessToken accessToken, String urlDomain) throws IOException, ExecutionException, InterruptedException;

}
