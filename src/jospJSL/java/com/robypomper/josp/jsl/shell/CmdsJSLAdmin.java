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

package com.robypomper.josp.jsl.shell;

import asg.cliche.Command;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jsl.admin.JSLAdmin;
import com.robypomper.josp.params.jcp.*;

import java.time.Duration;
import java.util.List;

public class CmdsJSLAdmin {

    private final JSLAdmin admin;

    public CmdsJSLAdmin(JSLAdmin admin) {
        this.admin = admin;
    }

    // JCP Services Status

    @Command(name = "Admin-JCP-APIs-Status",
            abbrev = "admJCPAPIsStatus",
            description = "Print JCP APIs status",
            header = "JCP APIs STATUS")
    public String adminJCPAPIsStatus() {
        APIsStatus status;
        try {
            status = admin.getJCPAPIsStatus();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP APIs status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP APIs status' because '%s'", e.getMessage());
        }

        return printStatus(status);
    }

    @Command(name = "Admin-JCP-GWs-Status",
            abbrev = "admJCPGWsStatus",
            description = "Print JCP GWs status",
            header = "JCP GWs STATUS")
    public String adminJCPGWsStatus() {
        List<GWsStatus> statusList;
        try {
            statusList = admin.getJCPGWsStatus();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP GWs status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP GWs status' because '%s'", e.getMessage());
        }

        StringBuilder s = new StringBuilder();
        for (GWsStatus status : statusList) {
            s.append("  GW STATUS: \n");
            s.append(printStatus(status));
        }
        return s.toString();
    }

    @Command(name = "Admin-JCP-JSL-WebBridge-Status",
            abbrev = "admJCPJSLWBStatus",
            description = "Print JCP JSL WebBridge status",
            header = "JCP JSL WEB BRIDGE STATUS")
    public String adminJCPJSLWebBridgeStatus() {
        JSLWebBridgeStatus status;
        try {
            status = admin.getJCPJSLWebBridgeStatus();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP JSL WebBridge status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP JSL WebBridge status' because '%s'", e.getMessage());
        }

        return printStatus(status);
    }

    @Command(name = "Admin-JCP-FE-Status",
            abbrev = "admJCPFEStatus",
            description = "Print JCP FrontEnd status",
            header = "JCP FRONT END STATUS")
    public String adminJCPFEStatus() {
        FEStatus status;
        try {
            status = admin.getJCPFEStatus();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP FrontEnd status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP FrontEnd status' because '%s'", e.getMessage());
        }

        return printStatus(status);
    }


    // Utils

    private static String printStatus(ServiceStatus status) {
        String s = "";
        s += String.format("  Started at  . . . . . %s\n", status.timeStart);
        s += String.format("  Running since . . . . %s\n", humanReadableFormat(status.timeRunning));
        s += String.format("  CPU Used  . . . . . . %.2f %%\n", status.cpuUsed);
        s += String.format("  Memory Used . . . . . %.0f MB\n", status.memoryUsed);
        s += "\n";
        s += String.format("  Version . . . . . . . %s\n", status.buildInfo.Version);
        s += String.format("  BuildVersion  . . . . %s\n", status.buildInfo.VersionBuild);
        s += String.format("  BuildTime . . . . . . %s\n", status.buildInfo.BuildTime);
        s += String.format("  GitBranch . . . . . . %s\n", status.buildInfo.GitBranch);
        s += String.format("  GitCommit . . . . . . %s\n", status.buildInfo.GitCommitShort);
        return s;
    }

    private static String humanReadableFormat(long millis) {
        Duration duration = Duration.ofMillis(millis);
        return duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }

}
