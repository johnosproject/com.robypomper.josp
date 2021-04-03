/* *****************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2020 Roberto Pompermaier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **************************************************************************** */

package com.robypomper.josp.callers.apis.admin.gateways.status;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.AbsAPISrv;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.defs.admin.gateways.status.Params20;
import com.robypomper.josp.defs.admin.gateways.status.Paths20;


/**
 * JOSP Admin - Gateways / Status 2.0
 */
public class Caller20 extends AbsAPISrv {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public Caller20(JCPAPIsClientSrv jcpClient) {
        super(jcpClient);
    }


    // List methods

    public Params20.GatewaysServers getGWsListReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_STATUS_LIST, Params20.GatewaysServers.class, isSecure());
    }


    // Index methods

    public Params20.Index getIndex(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_STATUS(gwServerId), Params20.Index.class, isSecure());
    }


    // GWs status methods

    public Params20.GWs getGWsReq(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_STATUS_GWS(gwServerId), Params20.GWs.class, isSecure());
    }

    public Params20.GW getGWReq(String gwServerId, String gwId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_STATUS_GW(gwServerId, gwId), Params20.GW.class, isSecure());
    }

    public Params20.GWClient getGWsClientReq(String gwServerId, String gwId, String gwClientId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_STATUS_GW_CLIENT(gwServerId, gwId, gwClientId), Params20.GWClient.class, isSecure());
    }


    // Broker status methods

    public Params20.Broker getBrokerReq(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER(gwServerId), Params20.Broker.class, isSecure());
    }

    public Params20.BrokerObject getBrokerObjectReq(String gwServerId, String objId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER_OBJ(gwServerId, objId), Params20.BrokerObject.class, isSecure());
    }

    public Params20.BrokerService getBrokerServiceReq(String gwServerId, String srvId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER_SRV(gwServerId, srvId), Params20.BrokerService.class, isSecure());
    }

    public Params20.BrokerObjectDB getBrokerObjectDBReq(String gwServerId, String objId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER_OBJ_DB(gwServerId, objId), Params20.BrokerObjectDB.class, isSecure());
    }

}
