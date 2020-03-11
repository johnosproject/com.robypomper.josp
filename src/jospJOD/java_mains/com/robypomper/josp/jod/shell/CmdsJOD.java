package com.robypomper.josp.jod.shell;

import asg.cliche.Command;
import asg.cliche.Param;
import com.robypomper.josp.jod.JOD;

public class CmdsJOD {

    private final JOD jod;

    public CmdsJOD(com.robypomper.josp.jod.JOD jod) {
        this.jod = jod;
    }

    /**
     * Print current JOD Object status.
     *
     * @return the JOD Object status.
     */
    @Command(description="Print current JOD Object status.")
    public com.robypomper.josp.jod.JOD.Status jodStatus() {
        return jod.status();
    }

    /**
     * Start current JOD Object.
     *
     * @return success or error message.
     */
    @Command(description="Start current JOD Object.")
    public String jodStart() {
        try {
            jod.start();
        } catch (com.robypomper.josp.jod.JOD.RunException e) {
            return String.format("Error on starting JOD Object:\n\t'%s'", e.getMessage());
        }
        return "JOD Object started successfully.";
    }

    /**
     * Stop current JOD Object.
     *
     * @return success or error message.
     */
    @Command(description="Stop current JOD Object.")
    public String jodStop() {
        try {
            jod.stop();
        } catch (com.robypomper.josp.jod.JOD.RunException e) {
            return String.format("Error on stopping JOD Object:\n\t'%s'", e.getMessage());
        }
        return "JOD Object stopped successfully.";
    }
}
