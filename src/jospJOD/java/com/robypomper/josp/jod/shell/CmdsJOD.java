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

package com.robypomper.josp.jod.shell;

import asg.cliche.Command;
import com.robypomper.josp.jod.JOD;

public class CmdsJOD {

    private final JOD jod;

    public CmdsJOD(com.robypomper.josp.jod.JOD jod) {
        this.jod = jod;
    }

    /**
     * Print current JOD Object status.
     *
     * @return the JOD Object status.
     */
    @Command(description = "Print current JOD Object status.")
    public JOD.Status jodStatus() {
        return jod.status();
    }

    /**
     * Start current JOD Object.
     *
     * @return success or error message.
     */
    @Command(description="Start current JOD Object.")
    public String jodStart() {
        try {
            jod.start();
        } catch (com.robypomper.josp.jod.JOD.RunException e) {
            return String.format("Error on starting JOD Object:\n\t'%s'", e.getMessage());
        }
        return "JOD Object started successfully.";
    }

    /**
     * Stop current JOD Object.
     *
     * @return success or error message.
     */
    @Command(description="Stop current JOD Object.")
    public String jodStop() {
        try {
            jod.stop();
        } catch (com.robypomper.josp.jod.JOD.RunException e) {
            return String.format("Error on stopping JOD Object:\n\t'%s'", e.getMessage());
        }
        return "JOD Object stopped successfully.";
    }
}
