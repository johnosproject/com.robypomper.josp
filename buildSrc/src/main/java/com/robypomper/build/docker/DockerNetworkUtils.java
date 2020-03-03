package com.robypomper.build.docker;

import com.avast.gradle.dockercompose.ComposeSettings;
import com.robypomper.build.commons.Naming;
import org.gradle.api.Project;
import org.gradle.api.tasks.Exec;

import java.io.ByteArrayOutputStream;
import java.util.Objects;


/**
 * Add support to docker's networks.
 *
 * This class register 3 {@link Exec} tasks that test, create and remove docker's
 * networks.
 */
public class DockerNetworkUtils {

    public static final String DOCKER_MAINTENANCE_GROUP = "docker maintenance";

    /**
     * Create docker's network management tasks and add (create network task) as
     * dependencies to given ComposeSettings.up tasks.
     *
     * @param project
     * @param docker
     * @param networkName
     */
    static public void setDockerNetworkDependency(Project project, ComposeSettings docker, String networkName) {
        Exec testNetwork = DockerNetworkUtils.tryConfigureTestNetworkTask(project,networkName);
        Exec createNetwork = DockerNetworkUtils.tryConfigureCreateNetworkTask(project,networkName,testNetwork);
        DockerNetworkUtils.tryConfigureRemoveNetworkTask(project,networkName);
        DockerNetworkUtils.configureDockerUpTaskDependency(docker,createNetwork);
    }

    /**
     *  </pre>
     *  task testTestNetwork(type: Exec) {
     *      group 'docker maintenance'
     *      commandLine('docker', 'network', 'inspect', 'rp-test' )
     *      ignoreExitValue = true
     *  }
     *  </pre>
     */
    static private Exec tryConfigureTestNetworkTask(Project project, String networkName) {
        String taskName = String.format("test%sNetwork", Naming.capitalize(networkName));
        Exec exec = project.getTasks().maybeCreate(taskName, Exec.class);
        exec.setGroup(DockerNetworkUtils.DOCKER_MAINTENANCE_GROUP);
        exec.setCommandLine("docker", "network", "inspect", networkName);
        exec.setIgnoreExitValue(true);
        exec.setStandardOutput(new ByteArrayOutputStream());
        return exec;
    }

    /**
     *  </pre>
     *  task createTestNetwork(type: Exec) {
     *      group 'docker maintenance'
     *      commandLine('docker', 'network', 'create', 'rp-test' )
     *      dependsOn testTestNetwork
     *      onlyIf {
     *          testTestNetwork.getExecResult().getExitValue() != 0
     *      }
     *  }
     *  </pre>
     */
    static private Exec tryConfigureCreateNetworkTask(Project project, String networkName, Exec testNetworkTask) {
        String taskName = String.format("create%sNetwork", Naming.capitalize(networkName));
        Exec exec = project.getTasks().maybeCreate(taskName, Exec.class);
        exec.setGroup(DockerNetworkUtils.DOCKER_MAINTENANCE_GROUP);
        exec.setCommandLine("docker", "network", "create", networkName);
        exec.dependsOn(testNetworkTask);
        exec.onlyIf(task -> Objects.requireNonNull(testNetworkTask.getExecResult()).getExitValue() != 0);
        return exec;
    }

    /**
     *  </pre>
     *  task removeTestNetworkSilent(type: Exec) {
     *      group 'docker maintenance'
     *      commandLine('docker', 'network', 'rm', 'rp-test' )
     *      ignoreExitValue = true
     *  }
     *  </pre>
     */
    static private Exec tryConfigureRemoveNetworkTask(Project project, String networkName) {
        String taskName = String.format("remove%sNetwork", Naming.capitalize(networkName));
        Exec exec = project.getTasks().maybeCreate(taskName, Exec.class);
        exec.setGroup(DockerNetworkUtils.DOCKER_MAINTENANCE_GROUP);
        exec.setCommandLine("docker", "network", "rm", networkName);
        exec.setIgnoreExitValue(true);
        return exec;
    }

    /**
     *  </pre>
     *  tasks.dockerTestComposeUp.dependsOn createTestNetwork
     *  </pre>
     */
    static private void configureDockerUpTaskDependency(ComposeSettings docker, Exec createNetworkTask) {
        docker.getUpTask().configure(composeUp -> composeUp.dependsOn(createNetworkTask));
    }

}
