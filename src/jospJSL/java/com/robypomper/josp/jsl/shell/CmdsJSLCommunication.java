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
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLLocalClient;

public class CmdsJSLCommunication {

    // Internal vars

    private final JSLCommunication comm;


    // Constructor

    public CmdsJSLCommunication(JSLCommunication comm) {
        this.comm = comm;
    }


    // Local communication control

    @Command(description = "Print local communication status.")
    public String commLocalStatus() {
        return String.format("Local communication discovery system is %s", comm.isLocalRunning() ? "running" : "NOT running");
    }

    @Command(description = "Start the local communication discovery system.")
    public String commLocalStart() {
        if (comm.isLocalRunning())
            return "Local communication discovery system is already started, do noting";

        try {
            comm.startLocal();
        } catch (JSLCommunication.LocalCommunicationException e) {
            return String.format("Error on starting local communication discovery system because %s.", e.getMessage());
        }

        if (comm.isLocalRunning())
            return "Local communication discovery system started successfully.";

        return "Error on starting local communication discovery system.";
    }

    @Command(description = "Stop the local communication discovery system.")
    public String commLocalStop() {
        if (!comm.isLocalRunning())
            return "Local communication discovery system is already stopped, do noting";

        try {
            comm.stopLocal();
        } catch (JSLCommunication.LocalCommunicationException e) {
            return String.format("Error on stopping local communication discovery system because %s.", e.getMessage());
        }

        if (!comm.isLocalRunning())
            return "Local communication discovery system stopped successfully.";

        return "Error on stopping local communication discovery system.";
    }

    @Command(description = "Print all local connections.")
    public String commPrintAllLocalConnections() {
        StringBuilder s = new StringBuilder("LOCAL CONNECTIONS LIST\n");
        for (JSLLocalClient client : comm.getAllLocalServers()) {
            String fullAddr = String.format("%s:%d", client.getServerAddr(), client.getServerPort());
            s.append(String.format("- %-30s (obj: %s; status: %s; local: %s)\n", fullAddr, client.getObjId(), client.isConnected() ? "connected" : "NOT conn.", client.getServerInfo().getLocalFullAddress()));
        }

        return s.toString();
    }


    // Cloud communication mngm

    @Command(description = "Print cloud communication status.")
    public String commCloudStatus() {
        return String.format("Cloud communication client is %s", comm.isCloudConnected() ? "connected" : "NOT connected");
    }

    @Command(description = "Connect the cloud communication client.")
    public String commCloudConnect() {
        if (comm.isCloudConnected())
            return "Cloud communication client is already connected, do noting";

        try {
            comm.connectCloud();
        } catch (JSLCommunication.CloudCommunicationException e) {
            return String.format("Error on connecting cloud communication client because %s.", e.getMessage());
        }

        if (comm.isCloudConnected())
            return "Cloud communication client connected successfully.";

        return "Error on connecting cloud communication client.";
    }

    @Command(description = "Connect the cloud communication client.")
    public String commCloudDisconnect() {
        if (!comm.isCloudConnected())
            return "Cloud communication client is already disconnected, do noting";

        comm.disconnectCloud();
        if (!comm.isCloudConnected())
            return "Cloud communication client disconnected successfully.";

        return "Error on disconnecting cloud communication client.";
    }


    // Communication's listeners

    @Command(description = "Add logger listener to objects manager's events.")
    public String objsCommAddListeners() {
        comm.addListener(new JSLCommunication.CommunicationListener() {

            @Override
            public void onCloudConnected(JSLCommunication comm) {
                System.out.println(CmdsJSLObjsMngr.PRE + "Communication cloud connected" + CmdsJSLObjsMngr.POST);
            }

            @Override
            public void onCloudDisconnected(JSLCommunication comm) {
                System.out.println(CmdsJSLObjsMngr.PRE + "Communication cloud disconnected" + CmdsJSLObjsMngr.POST);
            }

            @Override
            public void onLocalStarted(JSLCommunication comm) {
                System.out.println(CmdsJSLObjsMngr.PRE + "Communication local started" + CmdsJSLObjsMngr.POST);
            }

            @Override
            public void onLocalStopped(JSLCommunication comm) {
                System.out.println(CmdsJSLObjsMngr.PRE + "Communication local stopped" + CmdsJSLObjsMngr.POST);
            }
        });

        return "ok";
    }

}
