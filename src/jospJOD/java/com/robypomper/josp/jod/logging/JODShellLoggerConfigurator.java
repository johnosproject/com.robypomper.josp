package com.robypomper.josp.jod.logging;

import com.robypomper.josp.logging.JOSPLoggerConfigurator;

public class JODShellLoggerConfigurator extends JOSPLoggerConfigurator {

    private static final String FILE_NAME_DEFAULT = "logs/jospJOD.log";

    public JODShellLoggerConfigurator() {
        super(new JODLoggers(), FILE_NAME_DEFAULT);
    }

}
