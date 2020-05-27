package com.robypomper.josp.jsl.shell;

import asg.cliche.Command;
import com.robypomper.josp.jsl.JSL;

public class CmdsJSL {

    private final JSL jsl;

    public CmdsJSL(JSL jsl) {
        this.jsl = jsl;
    }

    /**
     * Print current JSL Service status.
     *
     * @return the JSL Service status.
     */
    @Command(description = "Print current JSL Object status.")
    public JSL.Status jslStatus() {
        return jsl.status();
    }

    /**
     * Connect current JSL Service.
     *
     * @return success or error message.
     */
    @Command(description = "Connect current JSL library.")
    public String jslConnect() {
        try {
            jsl.connect();
        } catch (JSL.ConnectException e) {
            return "Error on connecting JSL service because " + e.getMessage();
        }

        if (jsl.status() != JSL.Status.CONNECTED)
            return "JSL service NOT connected.";

        return "JSL service connected successfully.";
    }

    /**
     * Disconnect current JSL Service.
     *
     * @return success or error message.
     */
    @Command(description = "Disconnect current JSL library.")
    public String jslDisconnect() {
        try {
            jsl.disconnect();
        } catch (JSL.ConnectException e) {
            return "Error on disconnecting JSL service because " + e.getMessage();
        }

        if (jsl.status() != JSL.Status.DISCONNECTED)
            return "JSL Service NOT disconnected.";

        return "JSL Service disconnected successfully.";
    }
}
