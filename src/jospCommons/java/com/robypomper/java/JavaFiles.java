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
