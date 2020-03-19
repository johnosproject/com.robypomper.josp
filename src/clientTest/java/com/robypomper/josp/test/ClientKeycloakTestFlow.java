package com.robypomper.josp.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.keycloak.representations.idm.UserRepresentation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ClientKeycloakTestFlow implements ClientRequestFlow {

    //@formatter:off
    public static final String BASE           = "https://localhost:8998/auth/admin/realms/jcp";     //403
    //public static final String BASE           = "https://localhost:8998/auth/jcp";                //404
    public static final String USER           = BASE + "/users/";
//    public static final String PUBLIC         = BASE + "/public";
//    public static final String PRIVATE        = BASE + "/private";
//    public static final String ROLES_OBJ      = BASE + "/private/roles/obj";
//    public static final String ROLES_USR      = BASE + "/private/roles/srv";
//    public static final String ROLES_MNG      = BASE + "/private/roles/mng";
    //@formatter:on

    private final String usrId = "3d6bd9ed-8d63-43d6-a465-dbe9029e04c8";    // Roby

    @Override
    public void exec(OAuth20Service service, OAuth2AccessToken accessToken, String urlDomain) throws IOException, ExecutionException, InterruptedException {
        Map<String, String> headers = new HashMap<>();
        //headers.put("JOSP-Obj-ID", "dddddd");
        //headers.put("Content-Type", "application/json");
        ObjectMapper mapper = new ObjectMapper();


        System.out.println("User registration (via get get usrId/username):");
        RequestsMaker.execAndPrintReq(service, accessToken, USER + usrId, headers);
        UserRepresentation profile = RequestsMaker.execAndGetReq(service, accessToken, USER + usrId, headers, UserRepresentation.class);
        System.out.println(mapper.writeValueAsString(profile));
    }

}
