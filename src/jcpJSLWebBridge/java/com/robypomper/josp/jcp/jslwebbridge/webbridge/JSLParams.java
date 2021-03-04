package com.robypomper.josp.jcp.jslwebbridge.webbridge;

import com.robypomper.josp.jsl.JSLSettings_002;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JSLParams {

    // Internal vars

    public final String jslVersion;
    public final boolean useSSL;
    public final String urlAPIs;
    public final String urlAuth;
    public final String clientCallback;


    // Constructors

    public JSLParams(@Value("${jsl.version:2.0.0}") String jslVersion,
                     @Value("${" + JSLSettings_002.JCP_SSL + ".public:" + JSLSettings_002.JCP_SSL_DEF + "}") boolean useSSL,
                     @Value("${" + JSLSettings_002.JCP_URL_APIS + ":" + JSLSettings_002.JCP_URL_DEF_APIS + "}") String urlAPIs,
                     @Value("${" + JSLSettings_002.JCP_URL_AUTH + ":" + JSLSettings_002.JCP_URL_DEF_AUTH + "}") String urlAuth,
                     @Value("${" + JSLSettings_002.JCP_CLIENT_CALLBACK + ":}") String clientCallback) {

        this.jslVersion = jslVersion;
        this.useSSL = useSSL;
        this.urlAPIs = urlAPIs;
        this.urlAuth = urlAuth;
        this.clientCallback = clientCallback;
    }

}
