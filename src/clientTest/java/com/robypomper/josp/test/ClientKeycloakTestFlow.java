package com.robypomper.josp.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.robypomper.josp.jcp.apis.paths.APIAuth;
import org.keycloak.representations.idm.UserRepresentation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Testing request flow on Keycloak's
 * <a href="https://www.keycloak.org/docs-api/9.0/rest-api/index.html">Admin REST Apis</a>.
 */
public class ClientKeycloakTestFlow implements ClientRequestFlow {

    private final String usrId = "3d6bd9ed-8d63-43d6-a465-dbe9029e04c8";    // Roby

    @Override
    public void exec(OAuth20Service service, OAuth2AccessToken accessToken, String urlDomain) throws IOException, ExecutionException, InterruptedException {
        Map<String, String> headers = new HashMap<>();
        //headers.put("Content-Type", "application/json");
        ObjectMapper mapper = new ObjectMapper();

        System.out.println("Get user profile:");
        RequestsMaker.execAndPrintReq(service, accessToken, APIAuth.URL_PATH_USER + "/" + usrId, headers);
        UserRepresentation profile = RequestsMaker.execAndGetReq(service, accessToken, APIAuth.URL_PATH_USER + "/" + usrId, headers, UserRepresentation.class);
        System.out.println(mapper.writeValueAsString(profile));
    }

}
