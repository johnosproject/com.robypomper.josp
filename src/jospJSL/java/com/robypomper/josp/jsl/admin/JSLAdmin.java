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


import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.params.jcp.*;

import java.util.List;

/**
 * Interface for JSL Service's info system.
 * <p>
 * This system collect all service's info and provide them to other JSL's systems.
 * <p>
 * JSLServiceInfo implementations can access to the JCP API and JSL settings file
 * to load and store values of Service's info.
 */
public interface JSLAdmin {

    // JCP APIs status

    APIsStatus getJCPAPIsStatus() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    APIsStatus.Objects getJCPAPIsObjects() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    APIsStatus.Services getJCPAPIsServices() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    APIsStatus.Users getJCPAPIsUsers() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    String getJCPAPIsExecOnline() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.CPU getJCPAPIsExecCPU() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.Disks getJCPAPIsExecDisks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.Java getJCPAPIsExecJava() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    List<JCPStatus.JavaThread> getJCPAPIsExecJavaThs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.Memory getJCPAPIsExecMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.Network getJCPAPIsExecNetwork() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    List<JCPStatus.NetworkIntf> getJCPAPIsExecNetworkIntfs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.Os getJCPAPIsExecOs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.Process getJCPAPIsExecProcess() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;


    // JCP GWs status

    List<GWsStatus> getJCPGWsStatus() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    List<GWsStatus.Server> getJCPGWsClients() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    List<String> getJCPGWsExecOnline() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    List<JCPStatus.CPU> getJCPGWsExecCPU() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    List<JCPStatus.Disks> getJCPGWsExecDisks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    List<JCPStatus.Java> getJCPGWsExecJava() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    List<List<JCPStatus.JavaThread>> getJCPGWsExecJavaThs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    List<JCPStatus.Memory> getJCPGWsExecMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    List<JCPStatus.Network> getJCPGWsExecNetwork() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    List<List<JCPStatus.NetworkIntf>> getJCPGWsExecNetworkIntfs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    List<JCPStatus.Os> getJCPGWsExecOs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    List<JCPStatus.Process> getJCPGWsExecProcess() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;


    // JCP JSL WebBridge status

    JSLWebBridgeStatus getJCPJSLWebBridgeStatus() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    String getJCPJSLWBExecOnline() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.CPU getJCPJSLWBExecCPU() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.Disks getJCPJSLWBExecDisks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.Java getJCPJSLWBExecJava() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    List<JCPStatus.JavaThread> getJCPJSLWBExecJavaThs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.Memory getJCPJSLWBExecMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.Network getJCPJSLWBExecNetwork() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    List<JCPStatus.NetworkIntf> getJCPJSLWBExecNetworkIntfs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.Os getJCPJSLWBExecOs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.Process getJCPJSLWBExecProcess() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;


    // JCP FE status

    FEStatus getJCPFEStatus() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    String getJCPFEExecOnline() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.CPU getJCPFEExecCPU() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.Disks getJCPFEExecDisks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.Java getJCPFEExecJava() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    List<JCPStatus.JavaThread> getJCPFEExecJavaThs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.Memory getJCPFEExecMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.Network getJCPFEExecNetwork() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    List<JCPStatus.NetworkIntf> getJCPFEExecNetworkIntfs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.Os getJCPFEExecOs() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    JCPStatus.Process getJCPFEExecProcess() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;


    // Exceptions

    class UserNotAuthException extends Throwable {

        // Class constants

        private static final String MSG = "User not authenticated can't access to '%s' resource";

        // Internal vars

        private final String resource;


        // Constructors

        public UserNotAuthException(String resource) {
            super(String.format(MSG, resource));
            this.resource = resource;
        }


        // Getters

        public String getResource() {
            return resource;
        }

    }

    class UserNotAdminException extends Throwable {

        // Class constants

        private static final String MSG = "User '%s' (%s) not authorized to access '%s' resource";

        // Internal vars

        private final String usrId;
        private final String usrName;
        private final String resource;


        // Constructors

        public UserNotAdminException(String usrId, String usrName, String resource) {
            super(String.format(MSG, usrId, usrName, resource));
            this.usrId = usrId;
            this.usrName = usrName;
            this.resource = resource;
        }


        // Getters

        public String getUsrId() {
            return usrId;
        }

        public String getUsrName() {
            return usrName;
        }

        public String getResource() {
            return resource;
        }

    }

}
