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

package com.robypomper.build.node;

import com.github.gradle.node.npm.task.NpmTask;
import com.robypomper.build.commons.Naming;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.BasePlugin;

import java.io.File;


/**
 * Add support to Node projects.
 * <p>
 * This class register 4 tasks to install modules (one task for dev modules and
 * another for project modules), build and run given Node.js project.
 */
public class NodeBuildUtils {

    /**
     * Create Node.js project install, build and run tasks for given source set.
     *
     * @param project
     * @param sourceSetName
     */
    static public void makeNodeFromSourceSet(Project project, String sourceSetName, File sourceSetDir) {
        NpmTask npmInstallDev = NodeBuildUtils.configureNpmDevInstallTask(project, sourceSetName, sourceSetDir);
        NpmTask npmInstall = NodeBuildUtils.configureNpmInstallTask(project, sourceSetName, sourceSetDir, npmInstallDev);
        NodeBuildUtils.configureNpmBuildTask(project, sourceSetName, sourceSetDir, npmInstall);
        NodeBuildUtils.configureNodeRunTask(project, sourceSetName, sourceSetDir, npmInstall);
    }

    /*
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

        String hostnameAndDomain;
        String hostname;
        try {
            hostnameAndDomain = InetAddress.getLocalHost().getHostName().toLowerCase();
            hostname = hostnameAndDomain.substring(0,hostnameAndDomain.indexOf('.'));
            try {
                InetAddress.getAllByName(hostname);

            } catch (UnknownHostException ignore1) {
                InetAddress.getAllByName(hostnameAndDomain);
            }
        } catch (UnknownHostException ignore) {
            hostname = "localhost";
        }
        run.environment("HOSTNAME", hostname);
        run.doFirst(new Action<Object>(){
            @Override
            public void execute(Object task) {
                System.out.println("HOSTNAME: " + ((BootRun)task).getEnvironment().get("HOSTNAME"));
            }
        });
        return run;
    }
    */


    // node{SourceSetName}InstallDev
    static private NpmTask configureNpmDevInstallTask(Project project, String sourceSetName, File sourceSetDir) {
        String taskName = String.format("node%sInstallDev", Naming.capitalize(sourceSetName));
        NpmTask npmInstallDev = project.getTasks().create(taskName, NpmTask.class);

        npmInstallDev.setDescription(String.format("Install Node.js DEV modules for '%s' project.", sourceSetName));
        npmInstallDev.setGroup(BasePlugin.BUILD_GROUP);
        npmInstallDev.getWorkingDir().set(sourceSetDir);
        String[] args = {"install",
                "--save-dev",
                //"@babel/cli",
                //"@babel/preset-env",
                //"@babel/preset-react",
                //"babel-loader",
                //"webpack",
                //"webpack-cli"
        };
        npmInstallDev.getArgs().addAll(args);
        return npmInstallDev;
    }

    // node{SourceSetName}Install
    static private NpmTask configureNpmInstallTask(Project project, String sourceSetName, File sourceSetDir, Task npmInstallDev) {
        String taskName = String.format("node%sInstall", Naming.capitalize(sourceSetName));
        NpmTask npmInstall = project.getTasks().create(taskName, NpmTask.class);

        npmInstall.setDescription(String.format("Install Node.js modules for '%s' project.", sourceSetName));
        npmInstall.setGroup(BasePlugin.BUILD_GROUP);
        npmInstall.getWorkingDir().set(sourceSetDir);
        String[] args = {"install"};
        npmInstall.getArgs().addAll(args);

        npmInstall.dependsOn(npmInstallDev);
        return npmInstall;
    }

    // node{SourceSetName}Build       ex: processJcpFEResourcesNpmBuild
    static private NpmTask configureNpmBuildTask(Project project, String sourceSetName, File sourceSetDir, Task npmInstall) {
        String taskName = String.format("node%sBuild", Naming.capitalize(sourceSetName));
        NpmTask npmBuild = project.getTasks().create(taskName, NpmTask.class);

        npmBuild.setDescription(String.format("Build Node.js '%s' project.", sourceSetName));
        npmBuild.setGroup(BasePlugin.BUILD_GROUP);
        npmBuild.getWorkingDir().set(sourceSetDir);
        String[] cmds = {"run", "build"};
        npmBuild.getNpmCommand().addAll(cmds);

        npmBuild.dependsOn(npmInstall);
        return npmBuild;
    }

    // node{SourceSetName}Run
    static private NpmTask configureNodeRunTask(Project project, String sourceSetName, File sourceSetDir, Task npmInstall) {
        String taskName = String.format("node%sRun", Naming.capitalize(sourceSetName));
        NpmTask npmRun = project.getTasks().create(taskName, NpmTask.class);

        npmRun.setDescription(String.format("Run Node.js '%s' project.", sourceSetName));
        npmRun.setGroup("NPM Run");
        npmRun.getWorkingDir().set(sourceSetDir);
        // npm start

        String[] cmds = {"start"};
        npmRun.getNpmCommand().addAll(cmds);

        npmRun.dependsOn(npmInstall);
        return npmRun;
    }

}