package com.robypomper.josp.jcp.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClientParams {

    //@formatter:off
    @Value("${jcp.client.id}")                  public String clientId;
    @Value("${jcp.client.secret}")              public String clientSecret;
    @Value("${jcp.client.callback:'NA'}")       public String clientCallBack;
    @Value("${jcp.client.ssl.private}")         public boolean sslPrivate;
    @Value("${jcp.client.ssl.public}")          public boolean sslPublic;
    //@Value("${jcp.client.auth.private}")        public String authHostPrivate;
    @Value("${jcp.client.auth.public}")         public String authHostPublic;
    @Value("${jcp.client.auth.port}")           public String authPort;
    @Value("${jcp.client.apis.private:'NA'}")   public String apisHostPrivate;
    @Value("${jcp.client.apis.public:'NA'}")    public String apisHostPublic;
    @Value("${jcp.client.apis.port:'NA'}")      public String apisPort;
    @Value("${jcp.client.jslWB.private:'NA'}")  public String jslWBHostPrivate;
    @Value("${jcp.client.jslWB.public:'NA'}")   public String jslWBHostPublic;
    @Value("${jcp.client.jslWB.port:'NA'}")     public String jslWBPort;
    @Value("${jcp.client.fe.private:'NA'}")     public String feHostPrivate;
    @Value("${jcp.client.fe.public:'NA'}")      public String feHostPublic;
    @Value("${jcp.client.fe.port:'NA'}")        public String fePort;
    //@formatter:on

}
