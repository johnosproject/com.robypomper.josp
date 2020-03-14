package com.robypomper.josp.jod.shell;

import asg.cliche.Command;
import com.robypomper.josp.core.jcpclient.JCPClient;

public class CmdsJCPClient {

    private final JCPClient jcpClient;

    public CmdsJCPClient(JCPClient jcpClient) {
        this.jcpClient = jcpClient;
    }


    @Command(description = "Print JCP Client status.")
    public String jcpStatus() {
        return jcpClient.isConnected() ? "JCP Client is connect." : "JCP Client is disconnect.";
    }

    @Command(description = "Connect JCP Client.")
    public String jcpConnect() {
        try {
            jcpClient.connect();
        } catch (JCPClient.ConnectionException e) {
            return String.format("Error on JCP Client connection: %s.", e.getMessage());
        }
        return "JCP Client connected successfully.";
    }

    @Command(description = "Disconnect JCP Client.")
    public String jcpDisconnect() {
        jcpClient.disconnect();
        return "JCP Client connected successfully.";
    }

}
