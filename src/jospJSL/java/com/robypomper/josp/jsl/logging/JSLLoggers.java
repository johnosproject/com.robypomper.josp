package com.robypomper.josp.jsl.logging;

import com.robypomper.josp.logging.JOSPLoggerConfigurator;

/**
 * Utils class to expose JSL loggers groups: Main, Internal and External libraries.
 */
public class JSLLoggers implements JOSPLoggerConfigurator.LoggerGroupsIntf {

    /**
     * @return the list of Main loggers.
     */
    public String[] getAllLoggers() {
        return new String[]{
                "com.robypomper.josp.jsl"
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
