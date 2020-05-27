package com.robypomper.josp.jod.shell;

import asg.cliche.Command;
import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.comm.JODLocalClientInfo;

public class CmdsJODCommunication {

    // Internal vars

    private final JODCommunication comm;


    // Constructor

    public CmdsJODCommunication(JODCommunication comm) {
        this.comm = comm;
    }


    // Local communication control

    @Command(description = "Print local communication status.")
    public String commLocalStatus() {
        return String.format("Local communication server is %s", comm.isLocalRunning() ? "running" : "NOT running");
    }

    @Command(description = "Start the local communication server.")
    public String commLocalStart() {
        if (comm.isLocalRunning())
            return "Local communication server is already started, do noting";

        try {
            comm.startLocal();
        } catch (JODCommunication.LocalCommunicationException e) {
            return String.format("Error on starting local communication server because %s.", e.getMessage());
        }

        if (comm.isLocalRunning())
            return "Local communication server started successfully.";

        return "Error on starting local communication server.";
    }

    @Command(description = "Stop the local communication server.")
    public String commLocalStop() {
        if (!comm.isLocalRunning())
            return "Local communication server is already stopped, do noting";

        try {
            comm.stopLocal();
        } catch (JODCommunication.LocalCommunicationException e) {
            return String.format("Error on stopping local communication server because %s.", e.getMessage());
        }

        if (!comm.isLocalRunning())
            return "Local communication server stopped successfully.";

        return "Error on stopping local communication server.";
    }

    @Command(description = "Print all local connections.")
    public String commPrintAllLocalConnections() {
        StringBuilder s = new StringBuilder("LOCAL CONNECTIONS LIST\n");
        for (JODLocalClientInfo conn : comm.getAllLocalClientsInfo()) {
            String fullAddr = String.format("%s:%d", conn.getClientAddress(), conn.getClientPort());
            s.append(String.format("- %-30s (srv: %s; usr: %s; status: %s; local: %s)\n", fullAddr, conn.getSrvId(), conn.getUsrId(), conn.isConnected() ? "connected" : "NOT conn.", conn.getLocalFullAddress()));
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
        } catch (JODCommunication.CloudCommunicationException e) {
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

}
