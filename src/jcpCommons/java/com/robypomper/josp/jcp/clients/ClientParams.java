package com.robypomper.josp.jcp.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClientParams {

    // Internal vars

    public final boolean useSSLInternal;
    public final boolean useSSLPublic;
    public final String client;
    public final String secret;
    public final String urlAuth;
    public final String callBack;


    // Constructor

    @Autowired
    public ClientParams(@Value("${jcp.client.ssl.internal}") boolean useSSLInternal,
                        @Value("${jcp.client.ssl.public}") boolean useSSLPublic,
                        @Value("${jcp.client.id}") String client,
                        @Value("${jcp.client.secret}") String secret,
                        @Value("${jcp.urlAuth}") String urlAuth,
                        @Value("${jcp.client.callback:''}") String callBack) {
        this.useSSLInternal = useSSLInternal;
        this.useSSLPublic = useSSLPublic;
        this.client = client;
        this.secret = secret;
        this.urlAuth = urlAuth;
        this.callBack = callBack;
    }

}
