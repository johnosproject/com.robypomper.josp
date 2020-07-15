# Dockers - DBMS

The **DBMS** docker provide a DBMS based on [MySQL 5@DockerHub](https://hub.docker.com/_/mysql)
image and was customized as test DBMS for the JCP development. For this purpose the
service can be initialized with [preset data](dbms_presetdata.md) from the
```sql``` dir, [here](dbms_presetdata_howto.md) more info on how to manage preset
data.

The MySQL protocol is reachable at [mysql://localhost:8999]() and default ```root```
user's password is ```root```.

**Versions:**<br>
  * v. 2.0.0:
    initial version based on ```mysql:5```

[changelog](dbms_CHANGELOG.md) - [todos](dbms_TODOS.md)


## Gradle tasks

**Runner tasks:**<br>
  When execute, the DBMS component start a docker container running a MySQL 5.x.y
  server and load all [preset data](dbms_presetdata.md) (db schemas, tables, data
  and users).

  * ```./gradlew dbms_Up```<br>
    startup the docker container based on dbms's Dockerfile and expone the DBMS
    service on ```8999``` port.
    this tasks generate the Docker's image, container and network required by
    dbms's service.
    
  * ```./gradlew dbms_Down```<br>
    halt the docker container based on dbms's docker file.
    this tasks remove the Docker's container generated with ```dbms_Up``` task
    but not the generated image and networks.

**Cleaner tasks:**<br>
  * ```./gradlew dbms_Clean```<br>
    delete all docker container's volumes.
    this tasks remove all the Docker's image, container and network generated
    with ```dbms_Up``` task<br>
    **NB:** not yet implemented.


## Properties

**MySQL properties:**<br>
  No custom properties used in the default DBMS component. Personalized configs
  can be added in the ```/src/dockers/dbms/docker/configs``` dir.

**Connections from clients:**<br>
  Clients can connect to DBMS using the ```8999``` port of the working machine
  (preferred method) or via the ```3306``` container's port.<br>
  Normally clients must connect using dedicated user (with minimal permissions).
  That allow monitor witch client is communication with the DBMS and list executed
  queries.<br>
  Dedicated users are set within the [preset data](dbms_presetdata.md).

**Admin access:**<br>
  Default root password is ```root```.<br>
  DBMS clients like MySQLWorkbench can be used to access and checks DBMS's status.


## Connections

**From clients:**<br>
  Clients: Auth, JCP APIs, JOSP GWs
