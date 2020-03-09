# Auth/Keycloak

This project provide the **JCP Auth Server** for development environment.

This service is based on [jboss/keycloak@DockerHub](https://hub.docker.com/r/jboss/keycloak/)
image and was customized as test Auth server for the JCP development. For this
purpose the service is connected to [DBMS](../../dbms/docs/README.md)'s db ```josp_auth```.

The Auth server console is reachable at [https://localhost:8998/auth/admin/master/console/]()
and realm is ```jcp```.

## Service mngm

Like others Docker based project, it can be **started and stopped** with following cmds:
```shell script
./gradlew authComposeUp
./gradlew authComposeDown
```

To **manage the auth server resources** (clients/user/roles/scopes) please visit
the admin console at [https://localhost:8998/auth/admin/master/console/]().

To **reset the auth server's database**, you can reinitialize the DBMS
If you'd like to **reset the databases** to initial preset datasets, then
[DBMS](../../dbms/docs/README.md) service (losing all other data contined in the
dbms) or simply delete and recreate the ```auth_josp``` database with 
[auth_josp](../../dbms/docker/sql) sql scripts.

## Preset configurations

The auth server for development environment come with preconfigured settings
from the ```auth_josp``` database. This include:

**Clients:**
* test-client: support all flows
* test-client-obj: act as an object. Support Client Cred flow and ```obj``` role.
* test-client-srv: act as a service. Support Auth Code flow and ```srv``` role.
* test-client-swagger: act as swagger user because swagger support only Implicit flow. It supports also all scopes.

**Users:**
* roby/roby: user with manager privileges (mng role)
* pinco/pinco: user with normal privileges

**Roles:**
* obj: default role for objects (set @ client-level)
* srv: default role for services (set @ client-level)
* mng: default role for management services (set @ user-level)
* removed default assigned roles offline_access and uma_authorization

**Scopes:**
* roles: allow access to user's roles
* profile: allow access to user's profile
* email: allow access to user's email

**Keycloak:**
* general configs
* login:<br>
  Enabled following settings: user registration, edit username, forgot password, remember me, verify email,
  login with email, require SSL (true)
* email:<br>
  enabled SMTP server via [mailjet.com](https://app.mailjet.com/)

**Here a table that sum up access rules:**  

| User  | Client     | CliRoles | UsrRoles | Flows              | Task                  |
|-------|------------|----------|----------|--------------------|-----------------------|
| --    | Object     | obj      | --       | Client Credentials | javaClientTest_ObjRun |
| User  | Service    | srv      | --       | Auth Code          | javaClientTest_SrvRun |
| Mngr  | Service    | srv      | mng      | Auth Code          | javaClientTest_MngRun |
| ---   | Swagger    | ALL      | --       | Implicit           |                       |
