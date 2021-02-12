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
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.log.Mrk_JOD;

import java.io.IOException;
import java.util.Map;


/**
 * JOD Puller for Unix Shell commands.
 * <p>
 * Each time the {@link #pull()} method is called, it execute set command and parse his command as component's status.
 */
public class PullerUnixShell extends AbsJODPuller {

    // Class constants

    private static final String PROP_CMD = "cmd";
    private static final String PROP_FREQ_SEC = "freq";                 // in seconds


    // Internal vars

    private final String cmd;
    private final int freq_ms;


    // Constructor

    /**
     * Default PullerUnixShell constructor.
     *
     * @param name       name of the puller.
     * @param proto      proto of the puller.
     * @param configsStr configs string, can be an empty string.
     */
    public PullerUnixShell(String name, String proto, String configsStr, JODComponent component) throws MissingPropertyException, ParsingPropertyException {
        super(name, proto, component);
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerUnixShell for component '%s' init with config string '%s://%s'.", getName(), proto, configsStr));

        Map<String, String> configs = splitConfigsStrings(configsStr);
        cmd = parseConfigString(configs, PROP_CMD);
        freq_ms = parseConfigInt(configs, PROP_FREQ_SEC, Integer.toString(AbsJODPuller.DEF_POLLING_TIME / 1000)) * 1000;
    }

    protected long getPollingTime() {
        return freq_ms;
    }

    // Mngm

    /**
     * Pull method: print a log message and call the {@link JODState} sub
     * class's <code>setUpdate(...)</code> method.
     */
    @Override
    public void pull() {
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerUnixShell '%s' of proto '%s' pulling (cmd='%s')", getName(), getProto(), cmd));

        // CmdPartitioning
        String state;
        try {
            state = JavaExecProcess.execCmd(cmd).trim();

        } catch (IOException | JavaExecProcess.ExecStillAliveException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerUnixShell error on executing cmd '%s' for component '%s' because '%s'", cmd, getName(), e.getMessage()), e);
            return;
        }
        log.info(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerUnixShell '%s' of proto '%s' read state '%s'", getName(), getProto(), state));

        if (!convertAndSetStatus(state))
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerUnixShell for component '%s' can't update his component because not supported (%s)", getName(), getComponent().getClass().getSimpleName()));
    }

}
