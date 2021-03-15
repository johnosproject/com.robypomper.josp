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

package com.robypomper.josp.clients;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.params.jcp.JCPStatus;
import com.robypomper.josp.params.jcp.ServiceStatus;
import com.robypomper.josp.paths.jcp.JCPStatusAbs;

import java.util.List;

public class AbsAPIJCP extends AbsAPI {

    public AbsAPIJCP(JCPAPIsClientJCP jcpClient) {
        super(jcpClient);
    }

    public JCPAPIsClientJCP getClient() {
        return (JCPAPIsClientJCP) jcpClient;
    }


    // Status methods

    public ServiceStatus getStatusReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, JCPStatusAbs.FULL_PATH_STATUS_INSTANCE, ServiceStatus.class, isSecure());
    }

    public String getStatusOnlineReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, JCPStatusAbs.FULL_PATH_STATUS_ONLINE, String.class, isSecure());
    }

    public JCPStatus.Process getStatusProcessReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, JCPStatusAbs.FULL_PATH_STATUS_PROCESS, JCPStatus.Process.class, isSecure());
    }

    public JCPStatus.Java getStatusJavaReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, JCPStatusAbs.FULL_PATH_STATUS_JAVA, JCPStatus.Java.class, isSecure());
    }

    public List<JCPStatus.JavaThread> getStatusJavaThsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, JCPStatusAbs.FULL_PATH_STATUS_JAVA_THS, List.class, isSecure());
    }

    public JCPStatus.Os getStatusOSReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, JCPStatusAbs.FULL_PATH_STATUS_OS, JCPStatus.Os.class, isSecure());
    }

    public JCPStatus.CPU getStatusCpuReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, JCPStatusAbs.FULL_PATH_STATUS_CPU, JCPStatus.CPU.class, isSecure());
    }

    public JCPStatus.Memory getStatusMemoryReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, JCPStatusAbs.FULL_PATH_STATUS_MEMORY, JCPStatus.Memory.class, isSecure());
    }

    public JCPStatus.Disks getStatusDisksReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, JCPStatusAbs.FULL_PATH_STATUS_DISKS, JCPStatus.Disks.class, isSecure());
    }

    public JCPStatus.Network getStatusNetworkReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, JCPStatusAbs.FULL_PATH_STATUS_NETWORK, JCPStatus.Network.class, isSecure());
    }

    public List<JCPStatus.NetworkIntf> getStatusNetworkIntfsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, JCPStatusAbs.FULL_PATH_STATUS_NETWORK_INTFS, List.class, isSecure());
    }

}
