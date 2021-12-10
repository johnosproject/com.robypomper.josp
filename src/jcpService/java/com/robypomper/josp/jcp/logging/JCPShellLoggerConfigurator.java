package com.robypomper.josp.jcp.logging;

import com.robypomper.josp.logging.JOSPLoggerConfigurator;

public class JCPShellLoggerConfigurator extends JOSPLoggerConfigurator {

    private static final String FILE_NAME_DEFAULT_FORMAT = "logs/%s.log";

    public JCPShellLoggerConfigurator(String serviceName) {
        super(new JCPLoggers(), String.format(FILE_NAME_DEFAULT_FORMAT, serviceName));
    }

}
