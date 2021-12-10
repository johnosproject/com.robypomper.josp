package com.robypomper.josp.jsl.logging;

import com.robypomper.josp.logging.JOSPLoggerConfigurator;

public class JSLShellLoggerConfigurator extends JOSPLoggerConfigurator {

    private static final String FILE_NAME_DEFAULT = "logs/jospJSL.log";

    public JSLShellLoggerConfigurator() {
        super(new JSLLoggers(), FILE_NAME_DEFAULT);
    }

}
