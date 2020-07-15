# JOSP Project Documentation index

## JOSP Usage

The JOSP project allow setup different kind of [IoT eco-systems](josp_ecosystems.md).
Depending on your needs, required JOSP components, and their configurations may vary.

* **[JOSP JOD - John Object Daemon](jospJOD/README.md)**
  * [How to install on different HW platforms](jospJOD/jod_install.md)
  * [Configure the JOD agent](jospJOD/jod_config.md)
  * [Execute via Gradle's tasks](jospJOD/README.md#Gradle-tasks)
  * [JOD Interactive Shell cmds](jospJOD/jod_shellcmds.md)
* **[JOSP JSP - John Service Library](jospJSL/README.md)**
  * [Include the JSL Library in 3rd party SW](jsl_include.md)
  * [List available objects](jospJSL/jsl_listobjs.md)
  * [Search available objects](jospJSL/jsl_ose.md)
  * [Listen for new/removed objects](jospJSL/jsl_listeners.md)
  * [Listen for objects state updates](jospJSL/jsl_objinteractions.md)
  * [Send objects action commands](jospJSL/jsl_objinteractions.md)
  * Virtual JOSP Objects (Gradle "com.josp" plugin)
  * [Execute via Gradle's tasks](jospJSL/README.md#Gradle-tasks)
  * [JSL Interactive Shell cmds](jospJSL/jsl_shellcmds.md)
* **JCP - JOSP Cloud Platform**
  * [Execute JCP APIs](jcpAPIs/README.md#Gradle-tasks)
  * [Execute JOSP GWs](jospGWs/README.md#Gradle-tasks)
  * [Execute DBMS](dockers/dbms.md#Gradle-tasks)
  * [Execute Auth](dockers/auth.md#Gradle-tasks)
  * [Manage Users and Clients to Auth service](dockers/auth_addusersandclients.md)


## JOSP Security

* Services 2 Objects communication
  * [Basic SSL Communication](jospCommons/communication_ssl.md)
  * JOD > Communication > Local, Direct > SSL
  * JSL > Communication > Local, Direct > SSL
  * [SSL handshaking between JOSP GWs and JOD/JSL](jospGWs/ssl.md)
* Access Control
  * JOD > Permissions > Permission checks
  * JOSP GW > Broker#Permission checks
* User and clients authentication (aka OAuth)
  * [OAuth settings](jcpAPIs/oauth2.md)
  * [OAuth2](jospCommons/jcpclient_oauth2.md)
* Others
  * [HTTPs](jcpAPIs/https.md)


## JOSP Sub-Components

Here how JOSP Components are parted in Sub-Components and how they work.

* **[JOSP Commons](jospCommons/README.md)**
  * Sub-Components
    * [Discovery](jospCommons/discovery.md)
    * [Communication](jospCommons/communication.md)
      * [SSL Communication](jospCommons/communication_ssl.md)
    * [Settings](jospCommons/settings.md)
    * [Java](jospCommons/java.md)
    * [Logs Markers](jospCommons/logs.md)
    * [JCP Client](jospCommons/jcpclient.md)
      * [OAuth2](jospCommons/jcpclient_oauth2.md)
    * [JOSP Protocol](jospCommons/jospprotocol.md)
* **[JOSP JOD - John Object Daemon](jospJOD/README.md)**
  * Sub-Components
    * [Object Info](jospJOD/object_info.md)
    * [Structure](jospJOD/structure.md)
      \* Listeners, Puller, Executors
    * [Communication](jospJOD/communication.md)
      \* Local, Direct
        \* SSL
      \* JOSP Protocol (Tx <- callers, Rx -> processors)
    * [Permissions](jospJOD/permissions.md)
      \* Generate, Load, CRUD...
      \* Permission checks
    * [JCP Client](jospJOD/jcpclient.md)
* **[JOSP JSL - John Service Library](jospJSL/README.md)**
  * Sub-Components
    * [Service Info](jospJSL/service_info.md)
    * [Objects Manager](jospJSL/objects_manager.md)
    * [JSL Remote Object](jospJSL/remote_object.md)
    * [Communication](jospJSL/communication.md)
      \* Local, Direct
        \* SSL
      \* JOSP Protocol (Tx <- callers, Rx -> processors)
    * [JCP Client](jospJSL/jcpclient.md)
* **[JCP APIs](jcpAPIs/README.md)**
  * Sub-Components (APIs docs)
    * Login
    * [Object](jcpAPIs/object.md)
    * [Service](jcpAPIs/service.md)
    * [User](jcpAPIs/user.md)
    * [Permissions](jcpAPIs/object_permissions.md)
    * Configs
    * Updates
  * [Swagger configs](jcpAPIs/swagger.md)
  * [OAuth settings](jcpAPIs/oauth2.md)
  * [HTTPs](jcpAPIs/https.md)
  * [DB "jcp_api"](jcpAPIs/db.md)
* **[JOSP GWs](jospGWs/README.md)**
  * Sub-Components
    * [Object 2 Service](jospGWs/o2s.md)
    * [Service 2 Object](jospGWs/s2o.md)
    * [Broker](jospGWs/broker.md)
  * [SSL handshaking](jospGWs/ssl.md)
  * [DB "jcp_api"](jospGWs/db.md)
* **[Dockers](dockers/README.md)**
  * [DBMS](dockers/dbms.md)
    * [Preset users and data](dockers/dbms_presetdata.md)
    * [Manage preset users and data](dockers/dbms_presetdata_howto.md)
  * [Auth](dockers/auth.md)
    * [Default Realm, Users and Clients](dockers/auth_presetdata.md) (Preset Data)
* **[BuildSrc](buildSrc/README.md)**
  * Sub-Components
    * [Java](buildSrc/java.md)
    * [Spring](buildSrc/spring.md)
    * [Docker](buildSrc/docker.md)
