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

package com.robypomper.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class JavaExecProcess {

    // Exec process

    public static String execCmd(String cmd) throws IOException {
        ProcessBuilder pBuild = new ProcessBuilder(splitCmd(cmd));

        StringBuilder sb = new StringBuilder();
        Process process = pBuild.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) sb.append(line);

        return sb.toString();
    }

    public static void execCmdConcat(String cmd, Path redirectFile, long timeout) throws IOException {
        for (String singleCmd : cmd.split("&&")) {
            ProcessBuilder pBuild = new ProcessBuilder(splitCmd(singleCmd));
            if (redirectFile == null) {
                pBuild.inheritIO();
            } else {
                File f = redirectFile.toFile();
                if (!f.getParentFile().exists())
                    //noinspection ResultOfMethodCallIgnored
                    f.getParentFile().mkdirs();
                pBuild.redirectOutput(f);
                pBuild.redirectError(f);
            }
            Process process;
            process = pBuild.start();
            if (timeout != 0)
                try {
                    process.waitFor(timeout, TimeUnit.SECONDS);
                    process.getOutputStream().flush();

                } catch (InterruptedException e) {
                    process.destroy();
                }
        }
    }


    // Conversion methods used by exec process

    public static String[] splitCmd(String cmd) {
        String[] cmdParts = cmd.trim().split(" ");
        List<String> cmdSplitted = new ArrayList<>();
        for (int i = 0; i < cmdParts.length; i++) {
            String part = cmdParts[i];

            if (part.startsWith("'")) {
                if (part.endsWith("'")) {
                    cmdSplitted.add(part.substring(1, part.length() - 1));
                    continue;
                }

                StringBuilder partComplete = new StringBuilder(part);
                for (int k = i + 1; k < cmdParts.length; k++) {
                    partComplete.append(" ").append(cmdParts[k]);
                    if (cmdParts[k].endsWith("'")) {
                        cmdSplitted.add(partComplete.substring(1, partComplete.length() - 1));
                        i = k + 1;
                        break;
                    }
                }
                continue;
            }

            cmdSplitted.add(part);
        }
        return cmdSplitted.toArray(new String[0]);
    }

}
