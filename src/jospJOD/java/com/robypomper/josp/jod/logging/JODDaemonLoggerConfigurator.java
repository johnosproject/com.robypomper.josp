package com.robypomper.josp.jod.logging;

import com.robypomper.java.JavaLogging;
import com.robypomper.josp.logging.JOSPLoggerConfigurator;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Handler;

public class JODDaemonLoggerConfigurator extends JOSPLoggerConfigurator {

    private static final String FILE_NAME_DEFAULT = "logs/jospJOD.log";

    public JODDaemonLoggerConfigurator() {
        super(new JODLoggers(), FILE_NAME_DEFAULT);
    }

    protected Handler[] setupRootLoggersRelease(boolean isOnlyConsole, boolean isOnlyFile, String logFileName) {
        Handler[] handlers;
        try {
            handlers = JavaLogging.setupRootLoggerReleaseFile(logFileName);
            if (isOnlyConsole) logInfo(String.format("JOD Daemon's logs are forced to be printed on the '%s' file", Paths.get(logFileName).toAbsolutePath()));
            else if (isOnlyFile) logInfo(String.format("JOD Daemon's logs are printed on the '%s' file", Paths.get(logFileName).toAbsolutePath()));
            else logInfo(String.format("JOD Daemon's logs are printed on the '%s' file", Paths.get(logFileName).toAbsolutePath()));
        } catch (IOException e) {
            logError(String.format("Error setting up the file appender for release logger (%s),%nfallback to console appender.", e));
            handlers = JavaLogging.setupRootLoggerReleaseConsole(false);
        }
        return handlers;
    }

}
