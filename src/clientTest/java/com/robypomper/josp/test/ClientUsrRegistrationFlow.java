package com.robypomper.josp.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.robypomper.josp.jcp.db.entities.User;
import com.robypomper.josp.jcp.db.entities.UserProfile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Execute a requests sequence to register logged user to the JCP db.
 */
public class ClientUsrRegistrationFlow implements ClientRequestFlow {

    //@formatter:off
    public static final String BASE           = "/apis/user/ver";
    public static final String PROFILE        = BASE + "";
    //@formatter:on

    @Override
    public void exec(OAuth20Service service, OAuth2AccessToken accessToken, String urlDomain) throws IOException, ExecutionException, InterruptedException {
        Map<String, String> headers = new HashMap<>();
        //headers.put("Content-Type", "application/json");
        ObjectMapper mapper = new ObjectMapper();


        System.out.println("User registration (via get get usrId/username):");
        RequestsMaker.execAndPrintReq(service, accessToken, urlDomain + PROFILE, headers);
        // 2nd request (for SESSION test)
        RequestsMaker.execAndGetReq(service, accessToken, urlDomain + PROFILE, headers, User.class);
        // 3rd request (for SESSION test)
        RequestsMaker.execAndGetReq(service, accessToken, urlDomain + PROFILE, headers, User.class);
    }

}
