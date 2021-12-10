package com.robypomper.josp.jcp.logging;

import com.robypomper.josp.logging.JOSPLoggerConfigurator;

/**
 * Utils class to expose JCP loggers groups valid for all JCP Services: Main,
 * Internal and External libraries.
 */
public class JCPLoggers implements JOSPLoggerConfigurator.LoggerGroupsIntf {

    /**
     * @return the list of Main loggers.
     */
    public String[] getAllLoggers() {
        return new String[]{
                "com.robypomper.josp.jcp"
        };
    }

    /**
     * @return the list of Internal libraries loggers.
     */
    public String[] getAllInternalLibsLoggers() {
        return new String[]{
                "com.robypomper",
                "com.robypomper.josp",
                "com.robypomper.josp.jod",
                "com.robypomper.josp.jsl"
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
