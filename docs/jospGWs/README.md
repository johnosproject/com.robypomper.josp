# JOSP Gateways

The **JOSP GWs** is a Spring Boot application that provide JOSP Cloud communication
Gateways between [JOD objects](../jospJOD/README.md) and [JSL services](../jospJSL/README.md).

JOSP GWs are composed by 3 main components:
* [Object 2 Service GW](o2s.md)
* [Service 2 Object GW](s2o.md)
* [GWs Broker](broker.md)

- JOSP GWs protect all communication encrypting them with the [SSL](ssl.md) protocol.
- Finally, to store his data, the JOSP GWs connects to ["jcp_apis"](db.md) DB.

**NB:** actually the JOSP GWs are included in [JCP APIs](../jcpAPIs/README.md)'s
application.

**Versions:**<br>
  * v. 2.0.0:
    initial version that provide communication gateways used by JOSP JOD v. 2.0.0
    and JOSP JSL v. 2.0.0. It uses JOSP Protocol v. 2.0.

[changelog](CHANGELOG.md) - [todos](TODOS.md)


## Gradle tasks

Because the JOSP GWs are included in the JCP APIs application, tasks are the same
as [JCP APIs - Gradle tasks](../jcpAPIs/README.md#Gradle-tasks).


## Properties

*SpringBoot's properties:*
db
auth
...
    shared with JCP APIs


## Connections

**From clients:**<br>
  Clients: JCP APIs, JOD, JSL
  
**To DBMS:**<br>
  [DBMS - Connections](../dockers/dbms.md#Connections)



