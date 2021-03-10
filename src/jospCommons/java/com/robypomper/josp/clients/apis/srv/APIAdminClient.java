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

package com.robypomper.josp.clients.apis.srv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.AbsAPISrv;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.params.jcp.*;
import com.robypomper.josp.paths.APIAdmin;

import java.util.List;


/**
 * Support class for API JOSP GWs for service's requests.
 */
public class APIAdminClient extends AbsAPISrv {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public APIAdminClient(JCPAPIsClientSrv jcpClient) {
        super(jcpClient);
    }


    // JCP APIs status

    public APIsStatus getJCPAPIsStatus() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_APIS_STATUS_INSTANCE, APIsStatus.class, isSecure());
    }

    public APIsStatus.Objects getJCPAPIsStatusObjsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_APIS_STATUS_OBJS, APIsStatus.Objects.class, isSecure());
    }

    public APIsStatus.Services getJCPAPIsStatusSrvsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_APIS_STATUS_SRVS, APIsStatus.Services.class, isSecure());
    }

    public APIsStatus.Users getJCPAPIsStatusUsrsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_APIS_STATUS_USRS, APIsStatus.Users.class, isSecure());
    }

    public String getJCPAPIsStatusExecOnlineReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_ONLINE, String.class, isSecure());
    }

    public JCPStatus.CPU getJCPAPIsStatusExecCPUReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_CPU, JCPStatus.CPU.class, isSecure());
    }

    public JCPStatus.Disks getJCPAPIsStatusExecDisksReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_DISKS, JCPStatus.Disks.class, isSecure());
    }

    public JCPStatus.Java getJCPAPIsStatusExecJavaReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_JAVA, JCPStatus.Java.class, isSecure());
    }

    public List<JCPStatus.JavaThread> getJCPAPIsStatusExecJavaThreadsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_JAVA_THS, List.class, isSecure());
    }

    public JCPStatus.Memory getJCPAPIsStatusExecMemoryReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_MEMORY, JCPStatus.Memory.class, isSecure());
    }

    public JCPStatus.Network getJCPAPIsStatusExecNetworkReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_NETWORK, JCPStatus.Network.class, isSecure());
    }

    public List<JCPStatus.NetworkIntf> getJCPAPIsStatusExecNetworkIntfsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_NETWORK_INTFS, List.class, isSecure());
    }

    public JCPStatus.Os getJCPAPIsStatusExecOsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_OS, JCPStatus.Os.class, isSecure());
    }

    public JCPStatus.Process getJCPAPIsStatusExecProcessReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_APIS_STATUS_EXEC_PROCESS, JCPStatus.Process.class, isSecure());
    }


    // JCP GWs status

    public List<GWsStatus> getJCPGWsStatusReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        String str = jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_GWS_STATUS_INSTANCE, String.class, isSecure());
        try {
            return new ObjectMapper().readValue(str, new TypeReference<List<GWsStatus>>() {
            });

        } catch (JsonProcessingException e) {
            throw new JCPClient2.ResponseParsingException(APIAdmin.FULL_PATH_JCP_GWS_STATUS_INSTANCE, e);
        }
    }

    public List<GWsStatus.Server> getJCPGWsStatusCliReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_GWS_STATUS_SERVERS, List.class, isSecure());
    }

    public String getJCPGWsStatusExecOnlineReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_ONLINE, String.class, isSecure());
    }

    public JCPStatus.CPU getJCPGWsStatusExecCPUReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_CPU, JCPStatus.CPU.class, isSecure());
    }

    public JCPStatus.Disks getJCPGWsStatusExecDisksReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_DISKS, JCPStatus.Disks.class, isSecure());
    }

    public JCPStatus.Java getJCPGWsStatusExecJavaReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_JAVA, JCPStatus.Java.class, isSecure());
    }

    public List<JCPStatus.JavaThread> getJCPGWsStatusExecJavaThreadsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_JAVA_THS, List.class, isSecure());
    }

    public JCPStatus.Memory getJCPGWsStatusExecMemoryReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_MEMORY, JCPStatus.Memory.class, isSecure());
    }

    public JCPStatus.Network getJCPGWsStatusExecNetworkReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_NETWORK, JCPStatus.Network.class, isSecure());
    }

    public List<JCPStatus.NetworkIntf> getJCPGWsStatusExecNetworkIntfsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_NETWORK_INTFS, List.class, isSecure());
    }

    public JCPStatus.Os getJCPGWsStatusExecOsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_OS, JCPStatus.Os.class, isSecure());
    }

    public JCPStatus.Process getJCPGWsStatusExecProcessReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_GWS_STATUS_EXEC_PROCESS, JCPStatus.Process.class, isSecure());
    }


    // JCP JSL Web Bridge status

    public JSLWebBridgeStatus getJCPJSLWBStatusReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_INSTANCE, JSLWebBridgeStatus.class, isSecure());
    }

    public String getJCPJSLWBStatusExecOnlineReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_ONLINE, String.class, isSecure());
    }

    public JCPStatus.CPU getJCPJSLWBStatusExecCPUReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_CPU, JCPStatus.CPU.class, isSecure());
    }

    public JCPStatus.Disks getJCPJSLWBStatusExecDisksReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_DISKS, JCPStatus.Disks.class, isSecure());
    }

    public JCPStatus.Java getJCPJSLWBStatusExecJavaReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_JAVA, JCPStatus.Java.class, isSecure());
    }

    public List<JCPStatus.JavaThread> getJCPJSLWBStatusExecJavaThreadsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_JAVA_THS, List.class, isSecure());
    }

    public JCPStatus.Memory getJCPJSLWBStatusExecMemoryReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_MEMORY, JCPStatus.Memory.class, isSecure());
    }

    public JCPStatus.Network getJCPJSLWBStatusExecNetworkReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_NETWORK, JCPStatus.Network.class, isSecure());
    }

    public List<JCPStatus.NetworkIntf> getJCPJSLWBStatusExecNetworkIntfsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_NETWORK_INTFS, List.class, isSecure());
    }

    public JCPStatus.Os getJCPJSLWBStatusExecOsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_OS, JCPStatus.Os.class, isSecure());
    }

    public JCPStatus.Process getJCPJSLWBStatusExecProcessReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_JSL_WB_STATUS_EXEC_PROCESS, JCPStatus.Process.class, isSecure());
    }


    // JCP Front End status

    public FEStatus getJCPFEStatusReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_FE_STATUS_INSTANCE, FEStatus.class, isSecure());
    }

    public String getJCPFEStatusExecOnlineReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_ONLINE, String.class, isSecure());
    }

    public JCPStatus.CPU getJCPFEStatusExecCPUReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_CPU, JCPStatus.CPU.class, isSecure());
    }

    public JCPStatus.Disks getJCPFEStatusExecDisksReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_DISKS, JCPStatus.Disks.class, isSecure());
    }

    public JCPStatus.Java getJCPFEStatusExecJavaReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_JAVA, JCPStatus.Java.class, isSecure());
    }

    public List<JCPStatus.JavaThread> getJCPFEStatusExecJavaThreadsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_JAVA_THS, List.class, isSecure());
    }

    public JCPStatus.Memory getJCPFEStatusExecMemoryReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_MEMORY, JCPStatus.Memory.class, isSecure());
    }

    public JCPStatus.Network getJCPFEStatusExecNetworkReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_NETWORK, JCPStatus.Network.class, isSecure());
    }

    public List<JCPStatus.NetworkIntf> getJCPFEStatusExecNetworkIntfsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_NETWORK_INTFS, List.class, isSecure());
    }

    public JCPStatus.Os getJCPFEStatusExecOsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_OS, JCPStatus.Os.class, isSecure());
    }

    public JCPStatus.Process getJCPFEStatusExecProcessReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIAdmin.FULL_PATH_JCP_FE_STATUS_EXEC_PROCESS, JCPStatus.Process.class, isSecure());
    }

}
