package com.robypomper.josp.jod.logging;

import com.robypomper.josp.logging.JOSPLoggerConfigurator;

/**
 * Utils class to expose JOD loggers groups: Main, Internal and External libraries.
 */
public class JODLoggers implements JOSPLoggerConfigurator.LoggerGroupsIntf {

    // Logger list methods

    /**
     * @return the list of Main loggers.
     */
    public String[] getAllLoggers() {
        return new String[]{
                "com.robypomper.josp.jod"
        };
    }

    /**
     * @return the list of Internal libraries loggers.
     */
    public String[] getAllInternalLibsLoggers() {
        return new String[]{
                "com.robypomper",
                "com.robypomper.josp"
        };
    }

    /**
     * @return the list of External libraries loggers.
     */
    public String[] getAllExternalLibsLoggers() {
        return new String[]{
        };
    }

}
