package com.robypomper.josp.test;

import com.github.scribejava.apis.KeycloakApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.robypomper.josp.jcp.apis.paths.APIAuth;
import com.robypomper.josp.jcp.apis.paths.JcpAPI;
import javafx.util.Pair;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public abstract class AbsClientFlow {

    // Request flow
    private final ClientRequestFlow reqFlow;

    // OAuth2 server configs
    private final String authUrl = JcpAPI.URL_DOM_AUTH;
    private final String authRealm = APIAuth.REALM;
    private final String authCallback = APIAuth.URL_PATH_CALLBACK;

    // Resource server configs
    private final String baseUrl;

    // OAuth2 client credentials
    private final Pair<String, String> client;


    // Constructors

    public AbsClientFlow(ClientRequestFlow reqFlow, ClientSettings clientSettings) {
        this(reqFlow, clientSettings.getClientProtocol(), clientSettings.getClientPort(), clientSettings.getResServerPort(), clientSettings.getAuthServerPort(), clientSettings.getAuthServerRealm(), clientSettings.getClientCredentials());
    }

    protected AbsClientFlow(ClientRequestFlow reqFlow, String protocol, int clientPort, int resServerPort, int authServerPort, String authServerRealm, Pair<String, String> clientCredentials) {
        this.reqFlow = reqFlow;
        this.client = clientCredentials;

        this.baseUrl = protocol + "://localhost:" + resServerPort;
    }


    // Main run function

    public void run() throws IOException, InterruptedException, ExecutionException {
        // Setup OAuth2 layer
        final OAuth20Service service = new ServiceBuilder(client.getKey())
                .apiSecret(client.getValue())
                .defaultScope("openid")
                .callback(authCallback)
                .build(KeycloakApi.instance(authUrl, authRealm));

        // Start Auth flow
        final OAuth2AccessToken accessToken = getAccessToken(service);

        reqFlow.exec(service, accessToken, baseUrl);
        System.out.println("END");
    }


    // Abstract authentication function

    protected abstract OAuth2AccessToken getAccessToken(OAuth20Service service)
            throws InterruptedException, ExecutionException, IOException;

}
