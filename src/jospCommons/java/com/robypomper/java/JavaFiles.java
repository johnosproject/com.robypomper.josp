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

import java.io.*;
import java.nio.file.Path;


public class JavaFiles {

    // Create file

    public static void createParentIfNotExist(String filePath) throws IOException {
        File file = new File(filePath).getAbsoluteFile();
        if (!file.exists()) {
            if (!file.getParentFile().exists())
                //noinspection ResultOfMethodCallIgnored
                file.getParentFile().mkdirs();
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        }
    }


    // Read from file

    public static String readString(Path filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()));

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        // delete the last new line separator
        if (stringBuilder.length() > 0)
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        reader.close();

        return stringBuilder.toString();
    }

    public static void writeString(String filePath, String stateStr) throws IOException {
        FileWriter myWriter = new FileWriter(filePath);
        myWriter.write(stateStr);
        myWriter.close();
    }
}
