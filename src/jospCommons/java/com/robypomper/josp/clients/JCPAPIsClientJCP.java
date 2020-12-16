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

package com.robypomper.josp.clients;

import com.robypomper.josp.paths.APIObjs;
import com.robypomper.josp.paths.APISrvs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Cloud JCP APIs implementation of {@link JCPClient2} interface.
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
 *         <b>jcp.url.apis</b>: jcp apis server url.
 *     </li>
 *     <li>
 *         <b>jcp.url.auth</b>: auth server url.
 *     </li>
 * </ul>
 * <p>
 * As workaround of development localhost hostname usage, this class disable the
 * SSL checks and the connect to configured the server.
 */
public abstract class JCPAPIsClientJCP extends DefaultJCPClient2 implements JCPClient2.ConnectListener, JCPClient2.DisconnectListener {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    public final String apiName;
    public boolean connFailedPrinted = false;


    // Constructor

    public JCPAPIsClientJCP(boolean useSSL, String client, String secret, String urlAPIs, String urlAuth, String apiName, String callBack) {
        super(client, secret, urlAPIs, useSSL, urlAuth, "openid", callBack, "jcp", 30);
        this.apiName = apiName;
        addConnectListener(this);
        addDisconnectListener(this);

        connFailedPrinted = true;
        try {
            connect();

        } catch (ConnectionException | AuthenticationException | JCPNotReachableException e) {
            if (e instanceof JCPNotReachableException) {
                startConnectionTimer(true);
            }
        }
        connFailedPrinted = false;
    }


    // Headers default values setters

    public void setObjectId(String objId) {
        if (objId != null && !objId.isEmpty())
            addDefaultHeader(APIObjs.HEADER_OBJID, objId);
        else
            removeDefaultHeader(APIObjs.HEADER_OBJID);
    }

    public void setServiceId(String objId) {
        if (objId != null && !objId.isEmpty())
            addDefaultHeader(APISrvs.HEADER_SRVID, objId);
        else
            removeDefaultHeader(APISrvs.HEADER_SRVID);
    }

    public void setUserId(String usrId) {
        if (usrId != null && !usrId.isEmpty())
            addDefaultHeader(APISrvs.HEADER_USRID, usrId);
        else
            removeDefaultHeader(APISrvs.HEADER_USRID);
    }


    // Self-Connection observer

    @Override
    public void onConnected(JCPClient2 jcpClient) {
        log.info(String.format("%s connected", apiName));
        connFailedPrinted = false;
    }

    @Override
    public void onDisconnected(JCPClient2 jcpClient) {
        log.info(String.format("%s disconnected", apiName));
    }

    @Override
    public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
        if (connFailedPrinted) {
            log.debug(String.format("Error on %s connection attempt because %s", apiName, t.getMessage()));
        } else {
            log.warn(String.format("Error on %s connection attempt because %s", apiName, t.getMessage()));
            connFailedPrinted = true;
        }
    }

}