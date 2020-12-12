///* *****************************************************************************
// * The John Object Daemon is the agent software to connect "objects"
// * to an IoT EcoSystem, like the John Operating System Platform one.
// * Copyright (C) 2020 Roberto Pompermaier
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <https://www.gnu.org/licenses/>.
// **************************************************************************** */
//
//package com.robypomper.josp.jcp.clients.jcp.jcp;
//
//import com.github.scribejava.core.model.Verb;
//import com.robypomper.josp.clients.AbsAPIJCP;
//import com.robypomper.josp.clients.JCPClient2;
//import com.robypomper.josp.jcp.clients.JCPJSLWebBridgeClient;
//import com.robypomper.josp.params.jcp.JCPJSLWebBridgeStatus;
//import com.robypomper.josp.params.jcp.JCPStatus;
//import com.robypomper.josp.paths.jcp.APIJCP;
//
//import java.util.List;
//
//
///**
// * Support class for ...
// */
//public class JCPClient extends AbsAPIJCP {
//
//    // Constructor
//
//    /**
//     * Default constructor.
//     *
//     * @param jcpClient the JCP client.
//     */
//    public JCPClient(JCPJSLWebBridgeClient jcpClient) {
//        super(jcpClient);
//    }
//
//
//    // Generator methods
//
//    public String getStateReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
//        return jcpClient.execReq(Verb.GET, APIJCP.FULL_PATH_JSLWB_STATUS, String.class, isSecure());
//    }
//
//    public JCPStatus.Process getProcessReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
//        return jcpClient.execReq(Verb.GET, APIJCP.FULL_PATH_JSLWB_STATUS, JCPStatus.Process.class, isSecure());
//    }
//
//    public JCPStatus.Java getJavaReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
//        return jcpClient.execReq(Verb.GET, APIJCP.FULL_PATH_JSLWB_STATUS, JCPStatus.Java.class, isSecure());
//    }
//
//    public List<JCPStatus.JavaThread> getJavaThreadReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
//        return jcpClient.execReq(Verb.GET, APIJCP.FULL_PATH_JSLWB_STATUS, List.class, isSecure());
//    }
//
//    public JCPStatus.Os getOsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
//        return jcpClient.execReq(Verb.GET, APIJCP.FULL_PATH_JSLWB_STATUS, JCPStatus.Os.class, isSecure());
//    }
//
//    public JCPStatus.CPU getCPUReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
//        return jcpClient.execReq(Verb.GET, APIJCP.FULL_PATH_JSLWB_STATUS, JCPStatus.CPU.class, isSecure());
//    }
//
//    public JCPStatus.Memory getMemoryReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
//        return jcpClient.execReq(Verb.GET, APIJCP.FULL_PATH_JSLWB_STATUS, JCPStatus.Memory.class, isSecure());
//    }
//
//    public JCPStatus.Disks getDisksReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
//        return jcpClient.execReq(Verb.GET, APIJCP.FULL_PATH_JSLWB_STATUS, JCPStatus.Disks.class, isSecure());
//    }
//
//    public JCPStatus.Network getNetworkReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
//        return jcpClient.execReq(Verb.GET, APIJCP.FULL_PATH_JSLWB_STATUS, JCPStatus.Network.class, isSecure());
//    }
//
//    public JCPStatus.NetworkIntf getNetworkIntfsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
//        return jcpClient.execReq(Verb.GET, APIJCP.FULL_PATH_JSLWB_STATUS, JCPStatus.NetworkIntf.class, isSecure());
//    }
//
//}
