package com.robypomper.josp.jsl.shell;

import asg.cliche.Command;
import asg.cliche.Shell;
import com.robypomper.java.JavaVersionUtils;
import com.robypomper.josp.jsl.JSL_002;

public class CmdsShell {

    private final Shell shell;

    public CmdsShell(Shell shell) {
        this.shell = shell;
    }

    @Command(description = "Print current Java versions.")
    public String printJavaVersions() {
        return JavaVersionUtils.buildJavaVersionStr("ohn Service Library", JSL_002.VERSION);
    }

}
