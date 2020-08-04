/* *****************************************************************************
 * The John Cloud Platform set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.josp.jcp.jcpclient;

import com.robypomper.josp.core.jcpclient.DefaultJCPClient2;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Cloud default implementation of {@link JCPClient2} interface.
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
 *         <b>jcp.urlAPIs</b>: apis server url.
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
public class DefaultJCPClient_JCP extends DefaultJCPClient2 implements JCPClient2.ConnectListener {

    // Internal vars

    private static final Logger log = LogManager.getLogger();


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
                                @Value("${jcp.urlAPIs}") String urlAPIs,
                                @Value("${jcp.urlAuth}") String urlAuth) {
        super(client,
                secret,
                urlAPIs,
                urlAuth,
                "openid",
                "",
                "jcp",
                5);
        addConnectListener(this);

        try {
            connect();

        } catch (ConnectionException | AuthenticationException e) {
            log.warn(String.format("Error on self connecting because %s", e.getMessage()), e);

        } catch (JCPNotReachableException ignore) {
            startConnectionTimer();
        }
    }


    // Self-Connection observer

    @Override
    public void onConnected(JCPClient2 jcpClient) {
        log.info("Self connected");
    }

    @Override
    public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
        log.warn(String.format("Error on self connecting because %s", t.getMessage()), t);
    }

}
