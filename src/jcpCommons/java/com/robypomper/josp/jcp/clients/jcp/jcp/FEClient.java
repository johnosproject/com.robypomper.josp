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
import com.robypomper.josp.jcp.clients.JCPFEClient;
import com.robypomper.josp.params.jcp.FEStatus;
import com.robypomper.josp.paths.jcp.JCPStatusAbs;


/**
 * Support class for ...
 */
public class FEClient extends AbsAPIJCP {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public FEClient(JCPFEClient jcpClient) {
        super(jcpClient);
    }


    // Generator methods

    public FEStatus getJCPFEStatusReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, JCPStatusAbs.FULL_PATH_STATUS_INSTANCE, FEStatus.class, isSecure());
    }

}
