package com.robypomper.josp.test;

import com.github.scribejava.apis.KeycloakApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.robypomper.java.JavaSSLIgnoreChecks;
import javafx.util.Pair;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public abstract class AbsClientFlow {

    // OAuth2 server configs
    private final String baseUrl;
    private final String realm;
    private final String callback;

    // OAuth2 client credentials
    private final Pair<String,String> client;

    // Protected resources
    private final String proto;
    private final String urlBase;
    // Authentication
    private final String urlUserId;
    private final String urlUserName;
    private final String urlUserClientId;
    private final String urlUserRoles;
    private final String urlUserScopes;
    // Authorization
    private final String urlPublic;
    private final String urlPrivate;
    private final String urlRolesObj;
    private final String urlRolesUsr;
    private final String urlRolesMng;


    // Constructors

    public AbsClientFlow(ClientSettings clientSettings) {
        this(clientSettings.getClientProtocol(),clientSettings.getClientPort(),clientSettings.getResServerPort(),clientSettings.getAuthServerPort(),clientSettings.getAuthServerRealm(),clientSettings.getClientCredentials());
    }

    protected AbsClientFlow(String protocol, int clientPort, int resServerPort, int authServerPort, String authServerRealm, Pair<String,String> clientCredentials) {
        client = clientCredentials;

        proto = protocol;
        baseUrl = "https://localhost:" + authServerPort;
        realm = authServerRealm;
        callback = proto + "://localhost:" + clientPort + "/callback";

        urlBase = proto + "://localhost:" + resServerPort + "/apis/examples/test";

        urlPublic = urlBase + "/public";
        urlPrivate = urlBase + "/private";
        urlRolesObj = urlBase + "/private/roles/obj";
        urlRolesUsr = urlBase + "/private/roles/srv";
        urlRolesMng = urlBase + "/private/roles/mng";

        urlUserId = urlBase + "/user";
        urlUserName = urlBase + "/user/username";
        urlUserClientId = urlBase + "/user/clientid";
        urlUserRoles = urlBase + "/user/roles";
        urlUserScopes = urlBase + "/user/scopes";
    }


    // Main run function

    public void run() throws IOException, InterruptedException, ExecutionException, NoSuchAlgorithmException, KeyManagementException, JavaSSLIgnoreChecks.JavaSSLIgnoreChecksException {
        // Setup OAuth2 layer
        final OAuth20Service service = new ServiceBuilder(client.getKey())
                .apiSecret(client.getValue())
                .defaultScope("openid")
                .callback(callback)
                .build(KeycloakApi.instance(baseUrl, realm));

        // Start Auth flow
        final OAuth2AccessToken accessToken = getAccessToken(service);

        // Request protected resources
        System.out.println("Authentication - User:");
        execAndPrintReq(service,accessToken,urlUserId);
        execAndPrintReq(service,accessToken,urlUserName);
        execAndPrintReq(service,accessToken,urlUserClientId);
        execAndPrintReq(service,accessToken,urlUserRoles);
        execAndPrintReq(service,accessToken,urlUserScopes);

        System.out.println("Authorization - Resources:");
        execAndPrintReq(service,accessToken,urlPublic);
        execAndPrintReq(service,accessToken,urlPrivate);
        System.out.println("Authorization - Roles:");
        execAndPrintReq(service,accessToken,urlRolesObj);
        execAndPrintReq(service,accessToken,urlRolesUsr);
        execAndPrintReq(service,accessToken,urlRolesMng);

        System.out.println("END");
    }

    private static void execAndPrintReq(OAuth20Service service, OAuth2AccessToken accessToken, String url)
            throws InterruptedException, ExecutionException, IOException {
        final OAuthRequest req = new OAuthRequest(Verb.GET, url);
        service.signRequest(accessToken, req);
        try (Response response = service.execute(req)) {
            System.out.println(String.format("GET %-50s => [%s] %s", url, response.getCode(), response.getBody()));
        }
    }


    // Abstract authentication function

    protected abstract OAuth2AccessToken getAccessToken(OAuth20Service service)
            throws InterruptedException, ExecutionException, IOException;

}
