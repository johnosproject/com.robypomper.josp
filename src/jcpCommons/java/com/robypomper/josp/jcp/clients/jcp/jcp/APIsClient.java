/* *****************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.josp.jcp.clients.jcp.jcp;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.AbsAPIJCP;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.clients.JCPAPIsClient;
import com.robypomper.josp.jcp.params.jcp.JCPGWsStartup;
import com.robypomper.josp.jcp.paths.apis.JCPAPIsGWRegistration;


/**
 * Support class for ...
 */
public class APIsClient extends AbsAPIJCP {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public APIsClient(JCPAPIsClient jcpClient) {
        super(jcpClient);
    }


    // JCP APIs Status

    // N/A use internal reference


    // JCP GWs Registration

    /**
     * Request to the JCP GWs object's access info for Gateway O2S connection.
     * <p>
     * JCP APIs forward object's public certificate and instance id to the GW O2S and
     * the JCP respond with the GW O2S's address, port and public certificate.
     *
     * @return the GW O2S access info.
     */
    public boolean postStartup(JCPGWsStartup gwStartup, String gwId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        jcpClient.addDefaultHeader(JCPAPIsGWRegistration.HEADER_JCPGWID, gwId);
        return jcpClient.execReq(Verb.POST, JCPAPIsGWRegistration.FULL_PATH_STARTUP, Boolean.class, gwStartup, isSecure());
    }

    public boolean postStatus(com.robypomper.josp.jcp.params.jcp.JCPGWsStatus gwStatus, String gwId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        jcpClient.addDefaultHeader(JCPAPIsGWRegistration.HEADER_JCPGWID, gwId);
        return jcpClient.execReq(Verb.POST, JCPAPIsGWRegistration.FULL_PATH_STATUS, Boolean.class, gwStatus, isSecure());
    }

    public boolean postShutdown(String gwId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        jcpClient.addDefaultHeader(JCPAPIsGWRegistration.HEADER_JCPGWID, gwId);
        return jcpClient.execReq(Verb.POST, JCPAPIsGWRegistration.FULL_PATH_SHUTDOWN, Boolean.class, isSecure());
    }

}
