/* *****************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.josp.jod.executor;


import com.robypomper.java.JavaExecProcess;
import com.robypomper.java.JavaFiles;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.pillars.JODBooleanAction;
import com.robypomper.josp.jod.structure.pillars.JODRangeAction;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.log.Mrk_Commons;
import com.robypomper.log.Mrk_JOD;

import java.io.IOException;
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
    public ExecutorUnixShell(String name, String proto, String configsStr, JODComponent component) throws MissingPropertyException {
        super(name, proto, component);
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorUnixShell for component '%s' init with config string '%s://%s'", getName(), proto, configsStr));

        Map<String, String> configs = splitConfigsStrings(configsStr);
        cmd = parseConfigString(configs, PROP_CMD);
        redirect = parseConfigString(configs, PROP_REDIRECT, "");
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


        String cmdUpd = new Substitutions(cmd)
                .substituteAction(commandAction)
                .toString();
        String redirectUpd = redirect.isEmpty() ? null :
                new Substitutions(redirect)
                        .substituteAction(commandAction)
                        .toString();

        log.trace(Mrk_Commons.DISC_PUB_IMPL, String.format("Exec ExecutorUnixShell cmd '%s'", cmdUpd));

        // Split, redirect*redirectUpd!=null, CmdPartitioning
        try {
            String output = JavaExecProcess.execCmdConcat(cmdUpd, 0);
            if (redirectUpd != null)
                JavaFiles.writeString(redirectUpd, output);

        } catch (JavaExecProcess.ExecConcatException e) {
            log.warn(Mrk_JOD.JOD_EXEC, String.format("ExecutorUnixShell error on executing partial cmd '%s' for component '%s' because %s", cmdUpd, getName(), e.getMessage()), e);
            return false;

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_EXEC, String.format("ExecutorUnixShell error on writing output to '%s' file of partial cmd '%s' for component '%s' because %s", redirectUpd, cmdUpd, getName(), e.getMessage()), e);
            return false;
        }

        return true;
    }

    @Override
    public boolean exec(JOSPProtocol.ActionCmd commandAction, JODRangeAction.JOSPRange cmdAction) {
        System.out.printf("\n\nReceived action command from %s::%s (srv::usr) for %s::%s (obj::component)%n", commandAction.getServiceId(), commandAction.getUserId(), commandAction.getObjectId(), commandAction.getComponentPath());
        System.out.printf("\tnewState %f%n", cmdAction.newState);
        System.out.printf("\toldState %f%n", cmdAction.oldState);

        String cmdUpd = new Substitutions(cmd)
                .substituteAction(commandAction)
                .toString();
        String redirectUpd = redirect.isEmpty() ? null :
                new Substitutions(redirect)
                        .substituteAction(commandAction)
                        .toString();

        log.trace(Mrk_Commons.DISC_PUB_IMPL, String.format("Exec ExecutorUnixShell cmd '%s'", cmdUpd));

        // Split, redirect*redirectUpd!=null, CmdPartitioning
        try {
            String output = JavaExecProcess.execCmdConcat(cmdUpd, 0);
            if (redirectUpd != null)
                JavaFiles.writeString(redirectUpd, output);

        } catch (JavaExecProcess.ExecConcatException e) {
            log.warn(Mrk_JOD.JOD_EXEC, String.format("ExecutorUnixShell error on executing partial cmd '%s' for component '%s' because %s", cmdUpd, getName(), e.getMessage()), e);
            return false;

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_EXEC, String.format("ExecutorUnixShell error on writing output to '%s' file of partial cmd '%s' for component '%s' because %s", redirectUpd, cmdUpd, getName(), e.getMessage()), e);
            return false;
        }

        return true;
    }

}
