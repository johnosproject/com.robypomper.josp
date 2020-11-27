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
import com.robypomper.josp.jcp.clients.JCPGWsClient;
import com.robypomper.josp.params.jcp.JCPAPIsStatus;
import com.robypomper.josp.params.jcp.JCPGWsStatus;
import com.robypomper.josp.paths.jcp.APIJCP;

import java.util.List;


/**
 * Support class for ...
 */
public class GWsClient extends AbsAPIJCP {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public GWsClient(JCPGWsClient jcpClient) {
        super(jcpClient);
    }


    // Generator methods

    public JCPGWsStatus getJCPGWsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIJCP.FULL_PATH_GWS_STATUS, JCPGWsStatus.class, isSecure());
    }

    public List<JCPAPIsStatus.GWs> getJCPAPIsStatusGWsCliReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIJCP.FULL_PATH_GWS_STATUS_CLI, List.class, isSecure());
    }

}
