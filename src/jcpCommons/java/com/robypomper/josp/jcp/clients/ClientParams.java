package com.robypomper.josp.jcp.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClientParams {

    // Internal vars

    public final boolean useSSL;
    public final String client;
    public final String secret;
    public final String urlAuth;


    // Constructor

    @Autowired
    public ClientParams(@Value("${jcp.client.ssl}") boolean useSSL,
                        @Value("${jcp.client.id}") String client,
                        @Value("${jcp.client.secret}") String secret,
                        @Value("${jcp.urlAuth}") String urlAuth) {

        this.useSSL = useSSL;
        this.client = client;
        this.secret = secret;
        this.urlAuth = urlAuth;
    }

}
