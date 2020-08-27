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
    private static final String PROP_FREQ = "freq";
    public static final int UNIX_SHELL_POLLING_TIME = 30000;


    // Internal vars

    private final String cmd;
    private final int freq_ms;


    // Constructor

    /**
     * Default PullerTest constructor.
     *
     * @param name       name of the puller.
     * @param proto      proto of the puller.
     * @param configsStr configs string, can be an empty string.
     */
    public PullerUnixShell(String name, String proto, String configsStr, JODComponent component) {
        super(name, proto, component);
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerUnixShell for component '%s' init with config string '%s://%s'.", getName(), proto, configsStr));

        Map<String, String> properties = splitConfigsStrings(configsStr);
        cmd = genericSubstitution(properties.get(PROP_CMD), getComponent());
        int freq_msTMP;
        try {
            freq_msTMP = parseConfigInt(properties, PROP_FREQ, UNIX_SHELL_POLLING_TIME / 1000) * 1000;

        } catch (MissingPropertyException | ParsingPropertyException e) {
            log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("Error on parsing %s property on PullerUnixShell for component '%s' init with config string '%s://%s', use default value '%d' seconds", PROP_FREQ, getName(), proto, configsStr, UNIX_SHELL_POLLING_TIME / 1000));
            freq_msTMP = UNIX_SHELL_POLLING_TIME;
        }
        freq_ms = freq_msTMP;
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
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerTest '%s' of proto '%s' pulling (cmd='%s')", getName(), getProto(), cmd));

        // CmdPartitioning
        String state;
        try {
            state = JavaExecProcess.execCmd(cmd);

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorUnixShell error on executing partial cmd '%s' for component '%s'", cmd, getName()));
            return;
        }

        if (!convertAndSetStatus(state))
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerUnixShell for component '%s' can't update his component because not supported (%s)", getName(), getComponent().getClass().getSimpleName()));
    }

}