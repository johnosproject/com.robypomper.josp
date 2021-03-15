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

package com.robypomper.josp.jsl.admin;

import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.clients.apis.srv.APIAdminClient;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.josp.params.jcp.*;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


/**
 *
 */
public class JSLAdmin_002 implements JSLAdmin {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JSLSettings_002 locSettings;
    private final APIAdminClient apiAdminClient;
    private final JSLUserMngr userMngr;


    // Constructor

    public JSLAdmin_002(JSLSettings_002 settings, JCPAPIsClientSrv jcpClient, JSLUserMngr userMngr) {
        this.locSettings = settings;
        this.apiAdminClient = new APIAdminClient(jcpClient);
        this.userMngr = userMngr;

        log.info(Mrk_JSL.JSL_INFO, "Initialized JSLAdmin instance");
    }


    public void checkUserIsAdmin(String resource) throws UserNotAuthException, UserNotAdminException {
        if (!userMngr.isUserAuthenticated())
            throw new UserNotAuthException(resource);

        if (!userMngr.isAdmin())
            throw new UserNotAdminException(userMngr.getUserId(), userMngr.getUsername(), resource);
    }


    // JCP APIs status

    @Override
    public APIsStatus getJCPAPIsStatus() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs status");
        return apiAdminClient.getJCPAPIsStatus();
    }

    @Override
    public APIsStatus.Objects getJCPAPIsObjects() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs objects");
        return apiAdminClient.getJCPAPIsStatusObjsReq();
    }

    @Override
    public APIsStatus.Services getJCPAPIsServices() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs service");
        return apiAdminClient.getJCPAPIsStatusSrvsReq();
    }

    @Override
    public APIsStatus.Users getJCPAPIsUsers() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs users");
        return apiAdminClient.getJCPAPIsStatusUsrsReq();
    }

    @Override
    public String getJCPAPIsExecOnline() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs Online");
        return apiAdminClient.getJCPAPIsStatusExecOnlineReq();
    }

    @Override
    public JCPStatus.CPU getJCPAPIsExecCPU() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs CPU");
        return apiAdminClient.getJCPAPIsStatusExecCPUReq();
    }

    @Override
    public JCPStatus.Disks getJCPAPIsExecDisks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs Disks");
        return apiAdminClient.getJCPAPIsStatusExecDisksReq();
    }

    @Override
    public JCPStatus.Java getJCPAPIsExecJava() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs Java");
        return apiAdminClient.getJCPAPIsStatusExecJavaReq();
    }

    @Override
    public List<JCPStatus.JavaThread> getJCPAPIsExecJavaThs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs JavaThs");
        return apiAdminClient.getJCPAPIsStatusExecJavaThreadsReq();
    }

    @Override
    public JCPStatus.Memory getJCPAPIsExecMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs Memory");
        return apiAdminClient.getJCPAPIsStatusExecMemoryReq();
    }

    @Override
    public JCPStatus.Network getJCPAPIsExecNetwork() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs Network");
        return apiAdminClient.getJCPAPIsStatusExecNetworkReq();
    }

    @Override
    public List<JCPStatus.NetworkIntf> getJCPAPIsExecNetworkIntfs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs NetworkIntfs");
        return apiAdminClient.getJCPAPIsStatusExecNetworkIntfsReq();
    }

    @Override
    public JCPStatus.Os getJCPAPIsExecOs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs Os");
        return apiAdminClient.getJCPAPIsStatusExecOsReq();
    }

    @Override
    public JCPStatus.Process getJCPAPIsExecProcess() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs Process");
        return apiAdminClient.getJCPAPIsStatusExecProcessReq();
    }


    // JCP GWs status

    @Override
    public List<GWsStatus> getJCPGWsStatus() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP GWs status");
        return apiAdminClient.getJCPGWsStatusReq();
    }

    @Override
    public List<GWsStatus.Server> getJCPGWsClients() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP GWs clients");
        return apiAdminClient.getJCPGWsStatusCliReq();
    }

    @Override
    public List<String> getJCPGWsExecOnline() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP GWs Online");
        return apiAdminClient.getJCPGWsStatusExecOnlineReq();
    }

    @Override
    public List<JCPStatus.CPU> getJCPGWsExecCPU() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP GWs CPU");
        return apiAdminClient.getJCPGWsStatusExecCPUReq();
    }

    @Override
    public List<JCPStatus.Disks> getJCPGWsExecDisks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP GWs Disks");
        return apiAdminClient.getJCPGWsStatusExecDisksReq();
    }

    @Override
    public List<JCPStatus.Java> getJCPGWsExecJava() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP GWs Java");
        return apiAdminClient.getJCPGWsStatusExecJavaReq();
    }

    @Override
    public List<List<JCPStatus.JavaThread>> getJCPGWsExecJavaThs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP GWs JavaThs");
        return apiAdminClient.getJCPGWsStatusExecJavaThreadsReq();
    }

    @Override
    public List<JCPStatus.Memory> getJCPGWsExecMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP GWs Memory");
        return apiAdminClient.getJCPGWsStatusExecMemoryReq();
    }

    @Override
    public List<JCPStatus.Network> getJCPGWsExecNetwork() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP GWs Network");
        return apiAdminClient.getJCPGWsStatusExecNetworkReq();
    }

    @Override
    public List<List<JCPStatus.NetworkIntf>> getJCPGWsExecNetworkIntfs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP GWs NetworkIntfs");
        return apiAdminClient.getJCPGWsStatusExecNetworkIntfsReq();
    }

    @Override
    public List<JCPStatus.Os> getJCPGWsExecOs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP GWs Os");
        return apiAdminClient.getJCPGWsStatusExecOsReq();
    }

    @Override
    public List<JCPStatus.Process> getJCPGWsExecProcess() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP GWs Process");
        return apiAdminClient.getJCPGWsStatusExecProcessReq();
    }


    // JCP JSL WebBridge status

    @Override
    public JSLWebBridgeStatus getJCPJSLWebBridgeStatus() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL WebBridge status");
        return apiAdminClient.getJCPJSLWBStatusReq();
    }

    @Override
    public String getJCPJSLWBExecOnline() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge Online");
        return apiAdminClient.getJCPJSLWBStatusExecOnlineReq();
    }

    @Override
    public JCPStatus.CPU getJCPJSLWBExecCPU() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge CPU");
        return apiAdminClient.getJCPJSLWBStatusExecCPUReq();
    }

    @Override
    public JCPStatus.Disks getJCPJSLWBExecDisks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge Disks");
        return apiAdminClient.getJCPJSLWBStatusExecDisksReq();
    }

    @Override
    public JCPStatus.Java getJCPJSLWBExecJava() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge Java");
        return apiAdminClient.getJCPJSLWBStatusExecJavaReq();
    }

    @Override
    public List<JCPStatus.JavaThread> getJCPJSLWBExecJavaThs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge JavaThs");
        return apiAdminClient.getJCPJSLWBStatusExecJavaThreadsReq();
    }

    @Override
    public JCPStatus.Memory getJCPJSLWBExecMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge Memory");
        return apiAdminClient.getJCPJSLWBStatusExecMemoryReq();
    }

    @Override
    public JCPStatus.Network getJCPJSLWBExecNetwork() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge Network");
        return apiAdminClient.getJCPJSLWBStatusExecNetworkReq();
    }

    @Override
    public List<JCPStatus.NetworkIntf> getJCPJSLWBExecNetworkIntfs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge NetworkIntfs");
        return apiAdminClient.getJCPJSLWBStatusExecNetworkIntfsReq();
    }

    @Override
    public JCPStatus.Os getJCPJSLWBExecOs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge Os");
        return apiAdminClient.getJCPJSLWBStatusExecOsReq();
    }

    @Override
    public JCPStatus.Process getJCPJSLWBExecProcess() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge Process");
        return apiAdminClient.getJCPJSLWBStatusExecProcessReq();
    }


    // JCP FE status

    @Override
    public FEStatus getJCPFEStatus() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP FrontEnd status");
        return apiAdminClient.getJCPFEStatusReq();
    }

    @Override
    public String getJCPFEExecOnline() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End Online");
        return apiAdminClient.getJCPFEStatusExecOnlineReq();
    }

    @Override
    public JCPStatus.CPU getJCPFEExecCPU() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End CPU");
        return apiAdminClient.getJCPFEStatusExecCPUReq();
    }

    @Override
    public JCPStatus.Disks getJCPFEExecDisks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End Disks");
        return apiAdminClient.getJCPFEStatusExecDisksReq();
    }

    @Override
    public JCPStatus.Java getJCPFEExecJava() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End Java");
        return apiAdminClient.getJCPFEStatusExecJavaReq();
    }

    @Override
    public List<JCPStatus.JavaThread> getJCPFEExecJavaThs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End JavaThs");
        return apiAdminClient.getJCPFEStatusExecJavaThreadsReq();
    }

    @Override
    public JCPStatus.Memory getJCPFEExecMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End Memory");
        return apiAdminClient.getJCPFEStatusExecMemoryReq();
    }

    @Override
    public JCPStatus.Network getJCPFEExecNetwork() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End Network");
        return apiAdminClient.getJCPFEStatusExecNetworkReq();
    }

    @Override
    public List<JCPStatus.NetworkIntf> getJCPFEExecNetworkIntfs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End NetworkIntfs");
        return apiAdminClient.getJCPFEStatusExecNetworkIntfsReq();
    }

    @Override
    public JCPStatus.Os getJCPFEExecOs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End Os");
        return apiAdminClient.getJCPFEStatusExecOsReq();
    }

    @Override
    public JCPStatus.Process getJCPFEExecProcess() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End Process");
        return apiAdminClient.getJCPFEStatusExecProcessReq();
    }

}
