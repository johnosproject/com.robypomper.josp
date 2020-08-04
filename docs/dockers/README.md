# Dockers

The **Dockers** provided by JOSP Project allow to test the JOSP Project's
components in a development (local) environment. Provided dockers are:

* [DBMS](dbms.md): DBMS based on [MySQL 5@DockerHub](https://hub.docker.com/_/mysql)
* [Auth](auth.md): AAA service based on [KeyCloak_9.0.0@DockerHub](https://hub.docker.com/r/jboss/keycloak/)


## Docker's components

When a JOSP's component is a docker container, then it's configured with both
a Dockerfile and a docker-compose.yml. Then the Gradle's plugin
[com.avast.gradle.docker-compose](https://github.com/avast/gradle-docker-compose-plugin)
use provided docker-compose files to execute corresponding JOSP component.

**Gradle's configs:**<br>
  By default the com.avast.gradle.docker-compose plugin add Gradle's tasks
  in the ```docker``` group, but for a better task organization the "up" and "down"
  tasks are duplicated as ```{DOCKER_COMP}_Up``` and ```{DOCKER_COMP}_Down```
  in the [build_dockers.gradle](../../build_dockers.gradle) file.<br>
  In the same file, JOSP docker components are configured via the
  ```dockerCompose``` extension as in the following example:
  ```
  dockerCompose {
      {DOCKER_COMP} {                                                                  // on localhost:8999
          useComposeFiles = ['src/dockers/{DOCKER_COMP}/docker/docker-compose.yml']
      }
  }
  ```
  Here an example of douplicated "Up" and "Down" tasks:
  ```
  task {DOCKER_COMP}_Up {
      description = 'Runs...'
      group = 'JOSP runners'
      dependsOn {DOCKER_COMP}ComposeUp
  }

  task {DOCKER_COMP}Down {
      description = 'Runs...'
      group = 'JOSP runners'
      dependsOn {DOCKER_COMP}ComposeDown
  }
  ```
  The "Clean" task for docker components remove component's image, container and
  volumes generated from the corresponding "Up" task. Moreover, it deletes the
  content of local folder associated to container's volume.


**Volumes and ports:**<br>
  Docker's components can persist their files used in the container volumes.
  By default, the volumes with persistent files are mapped to dirs under the
  ```envs/dockers/{DOCKER_COMP}``` folder. This folders are deleted by the "Clean"
  task of corresponding JOSP component.<br>
  To improve the JOSP platform management, each JOSP docker component that expose
  a network port is mapped to localhost port starting from 8999 going down. For
  example the DBMS is mapped to 8999, the Auth is mapped to 8998...