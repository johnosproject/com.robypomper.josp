package com.robypomper.josp.jcp.jcpclient;

import com.robypomper.josp.core.jcpclient.DefaultJCPConfigs;
import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.core.jcpclient.JCPClient_CliCredFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Cloud default implementation of {@link JCPClient} interface.
 * <p>
 * This class initialize a JCPClient that can be used by JCP instance to access
 * to him self. As Spring component it can be declared in any other component
 * as variable (and @Autowired annotation) or directly as constructor param.
 * <p>
 * The client configurations are read from Spring Boot <code>application.yml</code>
 * file:
 * <ul>
 *     <li>
 *         <b>jcp.client.id</b>: client's id .
 *     </li>
 *     <li>
 *         <b>jcp.client.secret</b>: client's secret.
 *     </li>
 *     <li>
 *         <b>jcp.urlAuth</b>: auth server url.
 *     </li>
 * </ul>
 * <p>
 * As workaround of development localhost hostname usage, this class disable the
 * SSL checks and the connect to configured the server.
 */
@Component
public class DefaultJCPClient_JCP extends JCPClient_CliCredFlow {

    // Constructor

    /**
     * Default constructor, it read params from <code>application.yml</code> file.
     *
     * @param client  the client id to use to authenticate.
     * @param secret  the client secret to use to authenticate.
     * @param urlAuth the auth server url.
     */
    @Autowired
    public DefaultJCPClient_JCP(@Value("${jcp.client.id}") String client,
                                @Value("${jcp.client.secret}") String secret,
                                @Value("${jcp.urlAuth}") String urlAuth) {
        super(new DefaultJCPConfigs(client, secret, "openid", "", urlAuth, "jcp"), false);
        disableSSLChecks();
        tryConnect();
    }

}
