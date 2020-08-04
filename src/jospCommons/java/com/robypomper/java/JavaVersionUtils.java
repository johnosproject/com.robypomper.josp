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

import java.io.File;

public class JavaVersionUtils {

    public static String buildJavaVersionStr(String mainAppName, String mainAppVersion) {
        return mainAppName + " versions:   " + mainAppVersion + "\n" +
                "+-----------------\n" +
                "| Current dir:     " + new File("").getAbsolutePath() + "\n" +
                "+-----------------\n" +
                "| Java Version:    " + System.getProperty("java.specification.version") + "(" + System.getProperty("java.version") + ")" + "\n" +
                "| Java VM Version: " + System.getProperty("java.vm.specification.version") + "\n" +
                "| Java Runtime:    " + System.getProperty("java.runtime.name") + "\n" +
                "| Java Home:       " + System.getProperty("java.home") + "\n" +
                "| Java Home (ENV): " + System.getenv("JAVA_HOME") + "\n" +
                "| Java Vendor:     " + System.getProperty("java.vm.vendor") + "\n" +
                "| Java Libs Path:  " + System.getProperty("java.library.path") + "\n" +
                "| Java ClassPath:  " + System.getProperty("java.class.path") + "\n" +
                "+-----------------\n" +
                "| OS Name:         " + System.getProperty("os.name") + "\n" +
                "| OS Version:      " + System.getProperty("os.version") + "\n" +
                "| Current user:    " + System.getProperty("user.name") + "\n" +
                "| Dflt language:   " + System.getProperty("user.language") + "\n" +
                "| Dflt encoding:   " + System.getProperty("sun.jnu.encoding") + "\n" +
                "| File encoding:   " + System.getProperty("file.encoding") + "\n" +
                "+-----------------\n" +
                "| Working dir:     " + System.getProperty("user.dir") + "\n" +
                "+-----------------\n";
    }

}
