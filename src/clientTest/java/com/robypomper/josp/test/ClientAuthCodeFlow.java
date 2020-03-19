package com.robypomper.josp.test;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;


public class ClientAuthCodeFlow extends AbsClientFlow {

    public static final String AUTH_FLOW_NAME = "AuthCodeFlow";

    public ClientAuthCodeFlow(ClientRequestFlow reqFlow, ClientSettings clientSettings) {
        super(reqFlow, clientSettings);
    }

    @Override
    protected OAuth2AccessToken getAccessToken(OAuth20Service service)
            throws InterruptedException, ExecutionException, IOException {
        System.out.println("=== Auth Code Flow ===");

        final Scanner in = new Scanner(System.in);

        // Obtain the Authorization URL
        //System.out.println("Fetching the Authorization URL...");
        final String authorizationUrl = service.getAuthorizationUrl();
        //System.out.println("Got the Authorization URL!");
        System.out.println("Now go and authorize ScribeJava here:");
        System.out.println(authorizationUrl);
        System.out.println("And paste the authorization code here");
        System.out.print(">>");
        final String code = in.nextLine();

        //System.out.println("Trading the Authorization Code for an Access Token...");
        final OAuth2AccessToken accessToken = service.getAccessToken(code);
        System.out.println(accessToken.getRawResponse());
        return accessToken;
    }

}
