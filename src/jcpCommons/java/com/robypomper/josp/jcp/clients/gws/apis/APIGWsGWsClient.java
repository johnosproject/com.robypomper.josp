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

package com.robypomper.josp.jcp.clients.gws.apis;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.AbsAPIJCP;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.clients.JCPGWsClient;
import com.robypomper.josp.jcp.paths.gws.APIGWsGWs;
import com.robypomper.josp.params.jospgws.O2SAccessInfo;
import com.robypomper.josp.params.jospgws.O2SAccessRequest;
import com.robypomper.josp.params.jospgws.S2OAccessInfo;
import com.robypomper.josp.params.jospgws.S2OAccessRequest;


/**
 * Support class for API Events access from the JODEvents synchronization.
 */
public class APIGWsGWsClient extends AbsAPIJCP {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public APIGWsGWsClient(JCPGWsClient jcpClient) {
        super(jcpClient);
    }


    // Generator methods

    /**
     * Request to the JCP object's access info for Gateway O2S connection.
     * <p>
     * Object send his public certificate and instance id to the GW O2S and
     * the JCP respond with the GW O2S's address, port and public certificate.
     *
     * @return the GW O2S access info.
     */
    public O2SAccessInfo getO2SAccessInfo(String objId, O2SAccessRequest accessRequestParams) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        getClient().setObjectId(objId);
        O2SAccessInfo response = jcpClient.execReq(Verb.POST, APIGWsGWs.FULL_PATH_O2S_ACCESS, O2SAccessInfo.class, accessRequestParams, isSecure());
        getClient().setObjectId(null);
        return response;
    }

    /**
     * Request to the JCP service's access info for Gateway S2O connection.
     * <p>
     * Service send his public certificate and instance id to the GW S2O and
     * the JCP respond with the GW S2O's address, port and public certificate.
     *
     * @return the GW S2O access info.
     */
    public S2OAccessInfo getS2OAccessInfo(String srvId, S2OAccessRequest accessRequestParams) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        getClient().setServiceId(srvId);
        S2OAccessInfo response = jcpClient.execReq(Verb.POST, APIGWsGWs.FULL_PATH_S2O_ACCESS, S2OAccessInfo.class, accessRequestParams, isSecure());
        getClient().setServiceId(null);
        return response;
    }

}
