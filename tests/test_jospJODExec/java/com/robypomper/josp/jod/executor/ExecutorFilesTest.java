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

import com.robypomper.java.JavaFiles;
import com.robypomper.java.JavaFormatter;
import com.robypomper.josp.jod.structure.pillars.JODBooleanAction;
import com.robypomper.josp.jod.structure.pillars.JODRangeAction;
import com.robypomper.josp.protocol.JOSPMsgParams;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.test.mocks.jod.MockActionCmd;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class ExecutorFilesTest {

    private final String ITEMS_SEP = JOSPMsgParams.ITEMS_SEP;

    @Test
    public void executorTest() throws InterruptedException, IOException {
        String name = "executorTest";
        String proto = "files";
        String filePath = "listenerFilesTest.txt";
        String configs = "path=" + filePath;

        System.out.println("\nCREATE AND START EXECUTOR FOR FILES");
        ExecutorFiles e = new ExecutorFiles(name, proto, configs, null);
        JOSPProtocol.ActionCmd commandAction = new MockActionCmd();

        System.out.println("\nEXECUTE RANGE ACTION");
        String updStr = ExecutorUnixShellTest.formatUpdStr(true, false);
        JODBooleanAction.JOSPBoolean cmdActionBoolean = new JODBooleanAction.JOSPBoolean(updStr);
        e.exec(commandAction, cmdActionBoolean);
        Thread.sleep(1000);
        String readFile = JavaFiles.readString(Paths.get(filePath));
        Assertions.assertTrue(JavaFormatter.strToBoolean(readFile));

        System.out.println("\nEXECUTE RANGE ACTION");
        updStr = ExecutorUnixShellTest.formatUpdStr(5.33, 0.0);
        JODRangeAction.JOSPRange cmdActionRange = new JODRangeAction.JOSPRange(updStr);
        e.exec(commandAction, cmdActionRange);
        Thread.sleep(1000);
        String readFile2 = JavaFiles.readString(Paths.get(filePath));
        Assertions.assertEquals(new Double(5.33), JavaFormatter.strToDouble(readFile2));

        Paths.get(filePath).toFile().delete();
    }

}
