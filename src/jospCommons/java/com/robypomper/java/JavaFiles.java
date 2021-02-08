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


/**
 * Class to read files as Strings or byte arrays.
 */
public class JavaFiles {

    // Create file

    /**
     * Check if parent directory of given path exist, if not create it.
     *
     * @param fileName file name relative or absolute.
     * @throws IOException if errors occurs on creating parent dir.
     */
    public static void createParentIfNotExist(String fileName) throws IOException {
        createParentIfNotExist(new File(fileName));
    }

    /**
     * Check if parent directory of given path exist, if not create it.
     *
     * @param filePath file path.
     * @throws IOException if errors occurs on creating parent dir.
     */
    public static void createParentIfNotExist(Path filePath) throws IOException {
        createParentIfNotExist(filePath.toFile());
    }

    /**
     * Check if parent directory of given path exist, if not create it.
     *
     * @param file file instance.
     * @throws IOException if errors occurs on creating parent dir.
     */
    public static void createParentIfNotExist(File file) throws IOException {
        if (!file.exists()) {
            if (!file.getParentFile().exists())
                //noinspection ResultOfMethodCallIgnored
                file.getParentFile().mkdirs();
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        }
    }


    // Read from file

    /**
     * Read given file and return content as a String.
     *
     * @param fileName file name relative or absolute.
     * @return the file content.
     * @throws IOException if errors occurs on reading file.
     */
    public static String readString(String fileName) throws IOException {
        File file = new File(fileName);
        return readString(file);
    }

    /**
     * Read given file and return content as a String.
     *
     * @param filePath file path.
     * @return the file content.
     * @throws IOException if errors occurs on reading file.
     */
    public static String readString(Path filePath) throws IOException {
        return readString(filePath.toFile());
    }

    /**
     * Read given file and return content as a String.
     *
     * @param file file instance.
     * @return the file content.
     * @throws IOException if errors occurs on reading file.
     */
    public static String readString(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));

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

    /**
     * Read given file and return content as a byte array.
     *
     * @param fileName file name relative or absolute.
     * @return the file content.
     * @throws IOException if errors occurs on reading file.
     */
    public static byte[] readFile(String fileName) throws IOException {
        File file = new File(fileName);
        return readFile(file);
    }

    /**
     * Read given file and return content as a byte array.
     *
     * @param filePath file path.
     * @return the file content.
     * @throws IOException if errors occurs on reading file.
     */
    public static byte[] readFile(Path filePath) throws IOException {
        return readFile(filePath.toFile());
    }

    /**
     * Read given file and return content as a byte array.
     *
     * @param file file instance.
     * @return the file content.
     * @throws IOException if errors occurs on reading file.
     */
    public static byte[] readFile(File file) throws IOException {
        if (!file.exists() || !file.isFile())
            throw new FileNotFoundException(file.getAbsolutePath());

        if (!file.isFile())
            throw new IOException(String.format("Error reading '%s' because is not a file.", file.getAbsolutePath()));

        FileInputStream in = new FileInputStream(file);
        byte[] dataRead = new byte[0];

        byte[] dataTmp = new byte[in.available()];
        while (in.read(dataTmp) != -1) {
            dataRead = JavaByteArrays.append(dataRead, dataTmp);
            dataTmp = new byte[in.available() > 0 ? in.available() : 1];
        }

        in.close();
        return dataRead;
    }


    // Write to file

    /**
     * Write given content to file.
     *
     * @param fileName file name relative or absolute.
     * @throws IOException if errors occurs on reading file.
     */
    public static void writeString(String fileName, String content) throws IOException {
        writeString(new File(fileName), content);
    }

    /**
     * Write given content to file.
     *
     * @param filePath file path.
     * @throws IOException if errors occurs on reading file.
     */
    public static void writeString(Path filePath, String content) throws IOException {
        writeString(filePath.toFile(), content);
    }

    /**
     * Write given content to file.
     *
     * @param file file instance.
     * @throws IOException if errors occurs on reading file.
     */
    public static void writeString(File file, String content) throws IOException {
        FileWriter myWriter = new FileWriter(file);
        myWriter.write(content);
        myWriter.close();
    }

}
