# Dockers - Auth

The **Auth** docker provide an AAA service based on
[KeyCloak_9.0.0@DockerHub](https://hub.docker.com/r/jboss/keycloak/) image to use
as user, object and service register and authentication service for the JOSP
platform. Even the Auth component use the ```auth_usr``` DB with 
[preset data](auth_presetdata.md), it's always allowed to
[add/remove users and clients](auth_addusersandclients.md).

Once started, the Auth server is accessible via Web UI at
[https://localhost:8998/auth](https://localhost:8998/auth) url and default
administrator user credentials are ```admin/password```.

**Versions:**

* v. 2.0.0:
  initial version based on ```keycloak:9.0.0```

[changelog](auth_CHANGELOG.md) - [todos](auth_TODOS.md)


## Gradle tasks

### Runner tasks
When execute, the Auth component start a docker container running a KeyCloak 9.0.0
server and connect to ```jcp_auth``` DB on [DBMS](dbms.md) component.

---
```./gradlew auth_Up```

startup the docker container based on auth's Dockerfile and expone the Auth
UI and APIs on ```8998``` port.
this tasks generate the Docker's image, container and network required by
auth's service.

---
```./gradlew auth_Down```

halt the docker container based on auth's docker file.
this tasks remove the Docker's container generated with ```auth_Up``` task
but not the generated image and networks.


### Cleaner tasks

```./gradlew auth_Clean```

delete all docker container's volumes.
this tasks remove all the Docker's image, container and network generated
with ```auth_Up``` task


## Properties

**KeyCloak DB connection:**

KeyCloak is set to connect to ```auth_user``` DB on [DBMS](dbms.md) component.
Because both component are execute in two separate Docker's container, the Auth
component connects to DBMS via his container's ip (```172.17.0.1```) on port
```8999```.
To grant all privileges on ```auth_user``` DB, the Auth component use the
```auth_user/user_auth``` credentials (from [preset data/user](auth_presetdata.md).

**KeyCloak roles:**

To work properly for [JCP APIs](../jcpAPIs/README.md) component, the KeyCloak
is configured with 3 roles: ```obj``` and ```srv``` defined at client-level; and
the ```mng``` role defined to user-level.
First two roles can be asigned to clients to define if they are JOSP objects
([JOSP JOD](../jospJOD/README.md)) or JOSP services ([JOSP JSL](../jospJSL/README.md)).
The third role ```mng``` can be asigned to users to grant JOSP Platform
administrator's permissions.


## Connections

### From clients

Clients: JCP APIs


### To DBMS

[DBMS - Connections](dbms.md#Connections)
