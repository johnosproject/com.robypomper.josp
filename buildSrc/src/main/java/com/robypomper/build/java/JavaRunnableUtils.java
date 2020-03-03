package com.robypomper.build.java;

import com.robypomper.build.commons.Naming;
import org.gradle.api.Project;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.jvm.tasks.Jar;


/**
 * Add support pure java applications.
 *
 * This class register 2 tasks: jar and run tasks.
 *
 * First one, the jar, task assemble in a single jar all source set java sources.
 * Second one, the run task, allow to execute the main class specified as a java
 * application.
 */
public class JavaRunnableUtils {

    /**
     * Create java application build and run tasks for given source set.
     *
     * @param project
     * @param ss
     * @param mainClass
     */
    static public void makeJavaFromSourceSet(Project project, SourceSet ss, String mainClass) {
        JavaRunnableUtils.configureJavaJarTask(project,ss,"");
        JavaRunnableUtils.configureJavaRunTask(project,ss,mainClass,"");
    }

    /**
     * Create java application build and run tasks for given source set.
     *
     * @param project
     * @param ss
     * @param mainClass
     * @param taskPostName
     */
    static public void makeJavaFromSourceSet(Project project, SourceSet ss, String mainClass, String taskPostName) {
        JavaRunnableUtils.configureJavaJarTask(project,ss,taskPostName);
        JavaRunnableUtils.configureJavaRunTask(project,ss,mainClass,taskPostName);
    }

    static private Jar configureJavaJarTask(Project project, SourceSet ss, String taskPostName) {
        if (!taskPostName.isEmpty() && !taskPostName.startsWith("_"))
            taskPostName = "_" + taskPostName;

        String taskName = String.format("java%sJar", Naming.capitalize(ss.getName() + taskPostName));
        Jar javaJar = project.getTasks().create(taskName, Jar.class);
        javaJar.setDescription(String.format("Assembles an executable jar archive containing the %s classes and their dependencies.",ss.getName()));
        javaJar.setGroup(BasePlugin.BUILD_GROUP);
        javaJar.from(ss.getOutput());
        return javaJar;
    }

    static private JavaExec configureJavaRunTask(Project project, SourceSet ss, String mainClass, String taskPostName) {
        if (!taskPostName.isEmpty() && !taskPostName.startsWith("_"))
            taskPostName = "_" + taskPostName;

        String taskName = String.format("java%sRun", Naming.capitalize(ss.getName() + taskPostName));
        String buildTaskName = String.format("java%sJar", Naming.capitalize(ss.getName() + taskPostName));
        JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
        JavaExec run = project.getTasks().create(taskName, JavaExec.class);
        run.setDescription(String.format("Runs this project %s sources as a Java application.",ss.getName()));
        run.setGroup(ApplicationPlugin.APPLICATION_GROUP);
        run.classpath(ss.getRuntimeClasspath());
        run.setMain(mainClass);
        return run;
    }

}
