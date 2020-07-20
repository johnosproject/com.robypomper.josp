package com.robypomper.build.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

public class GradleUtils {

    public static class PrintGradleVersion extends DefaultTask {

        @TaskAction
        void print() {
            String str = "Gradle versions:" +
                    "+-----------------\n" +
                    "| Gradle:          " + getProject().getGradle().getGradleVersion() + "\n" +
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
            System.out.println(str);
        }
    }

}
