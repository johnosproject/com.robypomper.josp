package com.robypomper.josp.test;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ClientAuthCheckFlow implements ClientRequestFlow {

    //@formatter:off
    public static final String BASE           = "/apis/examples/test";
    public static final String PUBLIC         = BASE + "/public";
    public static final String PRIVATE        = BASE + "/private";
    public static final String ROLES_OBJ      = BASE + "/private/roles/obj";
    public static final String ROLES_USR      = BASE + "/private/roles/srv";
    public static final String ROLES_MNG      = BASE + "/private/roles/mng";
    public static final String USER_ID        = BASE + "/user";
    public static final String USERNAME       = BASE + "/user/username";
    public static final String USER_CLIENT_ID = BASE + "/user/clientid";
    public static final String USER_ROLES     = BASE + "/user/roles";
    public static final String USER_SCOPES    = BASE + "/user/scopes";
    //@formatter:on

    @Override
    public void exec(OAuth20Service service, OAuth2AccessToken accessToken, String urlDomain) throws IOException, ExecutionException, InterruptedException {
        System.out.println("Authentication - User:");
        RequestsMaker.execAndPrintReq(service, accessToken, urlDomain + USER_ID);
        RequestsMaker.execAndPrintReq(service, accessToken, urlDomain + USERNAME);
        RequestsMaker.execAndPrintReq(service, accessToken, urlDomain + USER_CLIENT_ID);
        RequestsMaker.execAndPrintReq(service, accessToken, urlDomain + USER_ROLES);
        RequestsMaker.execAndPrintReq(service, accessToken, urlDomain + USER_SCOPES);

        System.out.println("Authorization - Resources:");
        RequestsMaker.execAndPrintReq(service, accessToken, urlDomain + PUBLIC);
        RequestsMaker.execAndPrintReq(service, accessToken, urlDomain + PRIVATE);

        System.out.println("Authorization - Roles&Scopes:");
        RequestsMaker.execAndPrintReq(service, accessToken, urlDomain + ROLES_OBJ);
        RequestsMaker.execAndPrintReq(service, accessToken, urlDomain + ROLES_USR);
        RequestsMaker.execAndPrintReq(service, accessToken, urlDomain + ROLES_MNG);
    }

}
