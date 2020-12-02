/* *****************************************************************************
 * The John Operating System Project is the collection of software and configurations
 * to generate IoT EcoSystem, like the John Operating System Platform one.
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

package com.robypomper.build.spring;

import com.robypomper.build.commons.Naming;
import org.gradle.api.Project;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.springframework.boot.gradle.tasks.bundling.BootJar;
import org.springframework.boot.gradle.tasks.run.BootRun;

import java.io.File;
import java.util.Collections;


/**
 * Add support to Spring Boot applications.
 * <p>
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
        makeSpringBootFromSourceSet(project, ss, null);
    }

    static public void makeSpringBootFromSourceSet(Project project, SourceSet ss, String profiles) {
        SpringBuildUtils.configureBootJarTask(project, ss);
        SpringBuildUtils.configureBootRunTask(project, ss, profiles);
    }

    static public void makeSpringBootFromSourceSet(Project project, SourceSet ss, File logConfigs, String profiles) {
        SpringBuildUtils.configureBootJarTask(project, ss);
        BootRun br = SpringBuildUtils.configureBootRunTask(project, ss, profiles);
        br.setJvmArgs(Collections.singletonList("-Dlog4j.configurationFile=" + logConfigs.getAbsolutePath()));
    }

    static private BootJar configureBootJarTask(Project project, SourceSet ss) {
        String taskName = String.format("boot%sJar", Naming.capitalize(ss.getName()));
        BootJar bootJar = project.getTasks().create(taskName, BootJar.class);
        bootJar.setDescription(String.format("Assembles an executable jar archive containing the %s classes and their dependencies.", ss.getName()));
        bootJar.setGroup(BasePlugin.BUILD_GROUP);
        bootJar.classpath(ss.getRuntimeClasspath());
        //bootJar.conventionMapping(String.format("%sClassName", ss.getName()), new MainClassConvention2(project, bootJar::getClasspath));
        bootJar.conventionMapping(String.format("mainClassName"), new MainClassConvention2(project, bootJar::getClasspath));
        return bootJar;
    }

    static private BootRun configureBootRunTask(Project project, SourceSet ss, String profiles) {
        String taskName = String.format("boot%sRun", Naming.capitalize(ss.getName()));
        String buildTaskName = String.format("boot%sJar", Naming.capitalize(ss.getName()));
        JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
        BootRun run = project.getTasks().create(taskName, BootRun.class);
        run.setDescription(String.format("Runs this project %s sources as a Spring Boot application.", ss.getName()));
        run.setGroup(ApplicationPlugin.APPLICATION_GROUP);
        run.classpath(ss.getRuntimeClasspath());
        if (profiles != null)
            run.setArgs(Collections.singletonList("--spring.profiles.active=" + profiles));
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