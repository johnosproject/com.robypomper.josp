package com.robypomper.josp.jod.shell;

import asg.cliche.Command;
import com.robypomper.josp.core.jcpclient.JCPClient2;

public class CmdsJCPClient {

    private final JCPClient2 jcpClient;

    public CmdsJCPClient(JCPClient2 jcpClient) {
        this.jcpClient = jcpClient;
    }


    @Command(description = "Print JCP Client status.")
    public String jcpClientStatus() {
        return jcpClient.isConnected() ? "JCP Client is connect." : "JCP Client is NOT connect.";
    }

    @Command(description = "Connect JCP Client.")
    public String jcpClientConnect() {
        if (jcpClient.isConnected())
            return "JCP Client already connected.";

        try {
            jcpClient.connect();

        } catch (JCPClient2.ConnectionException e) {
            return String.format("Error on JCP Client connection: %s.", e.getMessage());

        } catch (JCPClient2.AuthenticationException e) {
            return String.format("Error on JCP Client authentication: %s.", e.getMessage());

        } catch (JCPClient2.JCPNotReachableException e) {
            return String.format("Error JCP Client not reachable: %s.", e.getMessage());
        }

        return "JCP Client connected successfully.";
    }

    @Command(description = "Disconnect JCP Client.")
    public String jcpClientDisconnect() {
        if (!jcpClient.isConnected())
            return "JCP Client already disconnected.";

        jcpClient.disconnect();
        return "JCP Client disconnected successfully.";
    }

}
