package com.robypomper.build.spring;

import com.robypomper.build.commons.Naming;
import org.gradle.api.Project;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.SourceSet;
import org.springframework.boot.gradle.tasks.bundling.BootJar;
import org.springframework.boot.gradle.tasks.run.BootRun;

import java.util.Collections;


/**
 * Add support to Spring Boot applications.
 *
 * This class register 2 tasks to build and run Spring Boot applications.
 */
public class SpringBuildUtils {

    /**
     * Create Spring Boot application build and run tasks for given source set.
     *
     * @param project
     * @param ss
     */
    static public void makeSpringBootFromSourceSet(Project project, SourceSet ss) {
        SpringBuildUtils.configureBootJarTask(project,ss);
        SpringBuildUtils.configureBootRunTask(project,ss);
    }

    static private BootJar configureBootJarTask(Project project, SourceSet ss) {
        String taskName = String.format("boot%sJar", Naming.capitalize(ss.getName()));
        BootJar bootJar = project.getTasks().create(taskName, BootJar.class);
        bootJar.setDescription(String.format("Assembles an executable jar archive containing the %s classes and their dependencies.",ss.getName()));
        bootJar.setGroup(BasePlugin.BUILD_GROUP);
        bootJar.classpath(ss.getRuntimeClasspath());
        //bootJar.conventionMapping(String.format("%sClassName", ss.getName()), new MainClassConvention2(project, bootJar::getClasspath));
        bootJar.conventionMapping(String.format("mainClassName"), new MainClassConvention2(project, bootJar::getClasspath));
        return bootJar;
    }

    static private BootRun configureBootRunTask(Project project, SourceSet ss) {
        String taskName = String.format("boot%sRun", Naming.capitalize(ss.getName()));
        String buildTaskName = String.format("boot%sJar", Naming.capitalize(ss.getName()));
        JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
        BootRun run = project.getTasks().create(taskName, BootRun.class);
        run.setDescription(String.format("Runs this project %s sources as a Spring Boot application.",ss.getName()));
        run.setGroup(ApplicationPlugin.APPLICATION_GROUP);
        run.classpath(ss.getRuntimeClasspath());
        run.getConventionMapping().map("jvmArgs", () -> {
            if (project.hasProperty("applicationDefaultJvmArgs")) {
                return project.property("applicationDefaultJvmArgs");
            }
            return Collections.emptyList();
        });
        //run.conventionMapping(ss.getName(), new MainClassConvention2(project, run::getClasspath));
        run.conventionMapping("main", new MainClassConvention2(project, run::getClasspath));
        return run;
    }

}