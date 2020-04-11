package com.robypomper.josp.jsl.shell;

import asg.cliche.Command;
import com.robypomper.josp.jsl.JSL;

public class CmdsJSL {

    private final JSL jsl;

    public CmdsJSL(JSL jsl) {
        this.jsl = jsl;
    }

    /**
     * Print current JOD Object status.
     *
     * @return the JOD Object status.
     */
    @Command(description = "Print current JSL Object status.")
    public JSL.Status jslStatus() {
        return jsl.status();
    }

    /**
     * Connect current JOD Object.
     *
     * @return success or error message.
     */
    @Command(description = "Connect current JSL library.")
    public String jslConnect() {
        jsl.connect();
        if (jsl.status() != JSL.Status.CONNECTED)
            return "JOD Object NOT connected.";

        return "JOD Object connected successfully.";
    }

    /**
     * Disconnect current JOD Object.
     *
     * @return success or error message.
     */
    @Command(description = "Disconnect current JSL library.")
    public String jslDisconnect() {
        jsl.disconnect();
        if (jsl.status() != JSL.Status.DISCONNECTED)
            return "JOD Object NOT disconnected.";

        return "JOD Object disconnected successfully.";
    }
}
