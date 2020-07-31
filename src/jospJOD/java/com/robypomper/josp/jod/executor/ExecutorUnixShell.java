package com.robypomper.josp.jod.executor;


import com.robypomper.java.JavaExecProcess;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.pillars.JODBooleanAction;
import com.robypomper.josp.jod.structure.pillars.JODRangeAction;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.log.Mrk_Commons;
import com.robypomper.log.Mrk_JOD;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;


/**
 * JOD Puller for Unix Shell commands.
 * <p>
 * Each time this executor must execute an action command, it execute a shell command.
 */
public class ExecutorUnixShell extends AbsJODExecutor implements JODBooleanAction.JOSPBoolean.Executor, JODRangeAction.JOSPRange.Executor {

    // Class constants

    private static final String PROP_CMD = "cmd";
    private static final String PROP_REDIRECT = "redirect";


    // Internal vars

    private final String cmd;
    private final String redirect;


    // Constructor

    /**
     * Default ExecutorUnixShell constructor.
     *
     * @param name       name of the executor.
     * @param proto      proto of the executor.
     * @param configsStr configs string, can be an empty string.
     */
    public ExecutorUnixShell(String name, String proto, String configsStr, JODComponent component) {
        super(name, proto, component);
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorUnixShell for component '%s' init with config string '%s://%s'", getName(), proto, configsStr));

        Map<String, String> properties = splitConfigsStrings(configsStr);
        cmd = properties.get(PROP_CMD);
        redirect = properties.get(PROP_REDIRECT);
    }


    // Mngm

    /**
     * Exec action method: print a log messages and return <code>true</code>.
     */
    @Deprecated
    @Override
    protected boolean subExec() {
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorTest for component '%s' of proto '%s' exec", getName(), getProto()));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exec(JOSPProtocol.ActionCmd commandAction, JODBooleanAction.JOSPBoolean cmdAction) {
        System.out.printf("\n\nReceived action command from %s::%s (srv::usr) for %s::%s (obj::component)%n", commandAction.getServiceId(), commandAction.getUserId(), commandAction.getObjectId(), commandAction.getComponentPath());
        System.out.printf("\tnewState %b%n", cmdAction.newState);
        System.out.printf("\toldState %b%n", cmdAction.oldState);


        String cmdUpd = actionSubstitution(cmd, getComponent(), commandAction, cmdAction);
        String redirectUpd = actionSubstitution(redirect, getComponent(), commandAction, cmdAction);

        log.trace(Mrk_Commons.DISC_PUB_IMPL, String.format("Exec ExecutorUnixShell cmd '%s'", cmdUpd));

        // Split, redirect*redirectUpd!=null, CmdPartitioning
        try {
            JavaExecProcess.execCmdConcat(cmdUpd, redirectUpd != null ? Paths.get(redirectUpd) : null, 0);

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorUnixShell error on executing partial cmd '%s' for component '%s'", cmd, getName()));
            return false;
        }

        return true;
    }

    @Override
    public boolean exec(JOSPProtocol.ActionCmd commandAction, JODRangeAction.JOSPRange cmdAction) {
        System.out.printf("\n\nReceived action command from %s::%s (srv::usr) for %s::%s (obj::component)%n", commandAction.getServiceId(), commandAction.getUserId(), commandAction.getObjectId(), commandAction.getComponentPath());
        System.out.printf("\tnewState %f%n", cmdAction.newState);
        System.out.printf("\toldState %f%n", cmdAction.oldState);


        String cmdUpd = actionSubstitution(cmd, getComponent(), commandAction, cmdAction);
        String redirectUpd = actionSubstitution(redirect, getComponent(), commandAction, cmdAction);

        log.trace(Mrk_Commons.DISC_PUB_IMPL, String.format("Exec ExecutorUnixShell cmd '%s'", cmdUpd));

        // Split, redirect*redirectUpd!=null, CmdPartitioning
        try {
            JavaExecProcess.execCmdConcat(cmdUpd, redirectUpd != null ? Paths.get(redirectUpd) : null, 0);

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorUnixShell error on executing partial cmd '%s' for component '%s'", cmd, getName()));
            return false;
        }

        return true;
    }

}
