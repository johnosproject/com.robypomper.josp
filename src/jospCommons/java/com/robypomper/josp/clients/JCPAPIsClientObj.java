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
import com.robypomper.josp.states.StateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@SuppressWarnings("unused")
public class JCPAPIsClientObj extends DefaultJCPClient2 implements JCPClient2.ConnectionListener {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    public static final String JCP_NAME = "JCP APIs";
    public boolean connFailedPrinted;


    // Constructor

    public JCPAPIsClientObj(boolean useSSL, String client, String secret, String urlAPIs, String urlAuth) {
        super(client, secret, urlAPIs, useSSL, urlAuth, "openid", "", "jcp", 30, JCP_NAME);
        addConnectionListener(this);

        connFailedPrinted = true;
        try {
            connect();

        } catch (StateException | AuthenticationException e) {
            log.warn(String.format("Error connecting to JCP %s because %s", getApiName(), e.getMessage()), e);
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


    // Self-Connection observer

    @Override
    public void onConnected(JCPClient2 jcpClient) {
        log.info(String.format("%s connected", JCP_NAME));
        connFailedPrinted = false;
    }

    @Override
    public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
        if (connFailedPrinted) {
            log.debug(String.format("Error on %s connection attempt because %s", JCP_NAME, t.getMessage()));
        } else {
            log.warn(String.format("Error on %s connection attempt because %s", JCP_NAME, t.getMessage()));
            connFailedPrinted = true;
        }
    }

    @Override
    public void onAuthenticationFailed(JCPClient2 jcpClient, Throwable t) {
        log.warn(String.format("Error on %s authentication because %s", JCP_NAME, t.getMessage()));
    }

    @Override
    public void onDisconnected(JCPClient2 jcpClient) {
        log.info(String.format("%s disconnected", JCP_NAME));
    }

}
