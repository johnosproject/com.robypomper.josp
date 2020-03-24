package com.robypomper.josp.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.robypomper.josp.jcp.apis.params.usrs.UsrName;
import com.robypomper.josp.jcp.apis.paths.APIUsrs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Execute a requests sequence to register logged user to the JCP db.
 */
public class ClientUsrRegistrationFlow implements ClientRequestFlow {

    @Override
    public void exec(OAuth20Service service, OAuth2AccessToken accessToken, String urlDomain) throws IOException, ExecutionException, InterruptedException {
        Map<String, String> headers = new HashMap<>();
        //headers.put("Content-Type", "application/json");
        ObjectMapper mapper = new ObjectMapper();

        System.out.println("User registration (via get usrId/username):");
        RequestsMaker.execAndPrintReq(service, accessToken, APIUsrs.URL_PATH_USERNAME, headers);
        // 2nd request (for SESSION test)
        RequestsMaker.execAndGetReq(service, accessToken, APIUsrs.URL_PATH_USERNAME, headers, UsrName.class);
        // 3rd request (for SESSION test)
        RequestsMaker.execAndGetReq(service, accessToken, APIUsrs.URL_PATH_USERNAME, headers, UsrName.class);
    }

}
