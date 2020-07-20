# JOSP JOD

The **JOSP JOD** is the software agent that represent a JOSP connected Object.

Makers can turn their objects into connected objects simply by [installing](jod_install.md)
and [configuring](jod_config.md) the JOSP JOD agent. The JOD agent can be
executed on a large variety of hardware platforms, and it allows makers to expose
object's states and actions to [JOSP services](../jospJSL/README.md) without
writing code.

Each object's state or action can be defined in the **object's structure**. The
object's structure is a hierarchy collection of basic blocks that represent
precisely states and actions that the object's expose to JOSP services.<br>
The state blocks configurations tell to JOD how the states have to be read from
underling hardware peripherals and how to update connected JOSP services. In the
same way, the action blocks configurations tell to JOD witch (firmware's) command
must be executed when receive an action command from a JOSP service.

Before send a state update or execute an action command, the JOD agent check the
service's **access permission** to represented object to prevent unauthorized access
to the object.

The JOD can **manage connections (direct and cloud)** with the JOSP services.
When started the JOD agent, if an internet connection is available, try connect
to the [JCP APIs](../jcpAPIs/README.md) and to the [JOSP GWs](../jospGWs/README.md)
so it can communicate with any allowed JOSP service via Cloud Gateways. Moreover,
the JOD agent start a local server and publish it via ZeroConf on the local network.
That allow local services to connect directly to the represented object even
without an internet connection. Both kind of communication (Cloud and Direct) are
setup with double-key SSL encryption.

To simplify the JOD development and testing, it can be executed with two different
[execution modes](jod_execmode.md): interactive shell or as a daemon. When
executed in **interactive shell mode** the execution user can type commands to monitor
or alter the object's state. For example enabling/disabling the cloud connection,
or updating object's name, etc... A list of available JOD interactive shell
commands can be found [here](jod_shellcmds.md)<br>
The **daemon execution mode**, to the other side is the default mode on real objects.
The JOD agent can be installed on hosting operating system like a service/daemon
so, it will executed on each host's operating system bootup.

It's a software java app composed by:
* [Object Info](object_info.md)
* [Structure](structure.md)
* [Communication](communication.md)
* [Permissions](permissions.md)
* [JCP Client](jcpclient.md)

**Versions:**<br>
  * v. 2.0.0:
    initial version that require JCP APIs v. 2.0, JOSP GWs v. 2.0.0 and use
    JOSP Protocol v. 2.0.

[changelog](CHANGELOG.md) - [todos](TODOS.md)


## Gradle tasks

**Runner tasks:**<br>
  To run the JOSP JOD agent this project provide different tasks for different 
  purposes. With Gradle's task, the JOD agent is always executed as interactive
  shell:
  
  * ```./gradlew javaJODRun```<br>
    this task start a JOD agent from a persistent working dir. This means that,
    after the first execution (when the object initialize his ids, name, etc...),
    all other execution keep the same configs. In other words when executed with
    this task, the JOD agent always represent the same object.
    After the first git commit, this task's working dir is added to ```.gitignore```
    so any modification will not commit to the repository.
  
  * ```./gradlew javaJODVanillaRun```<br>
    at every execution, the task's working dir (and then his configs and data)
    are cleaned. So with this task is always possible execute a new JOD object
    with default configs.
  
  * ```./gradlew javaJOD{IOC}Run```<br>
    like the ```javaJODVanillaRun``` task but with pre-set configs:<br>

    | IOC | Obj's IDs | Obj's Owner | Cloud auto-connect |
    |-----|-----------|-------------|--------------------|
    | uun | Unset | Unset | Yes |
    | uuf | Unset | Unset | No  |
    | usn | Unset | Set (Pinco) | Yes |
    | usf | Unset | Set (Pinco) | No  |
    | cun | Set (Cloud) | Unset | Yes |
    | cuf | Set (Cloud) | Unset | No  |
    | csn | Set (Cloud) | Set (Pinco) | Yes |
    | csf | Set (Cloud) | Set (Pinco) | No  |
    | lun | Set (Local) | Unset | Yes |
    | luf | Set (Local) | Unset | No  |
    | lsn | Set (Local) | Set (Pinco) | Yes |
    | lsf | Set (Local) | Set (Pinco) | No  |
    
  * ```./gradlew javaJOD{Disc}Run```<br>
    like the ```javaJOD{UC}Run``` tasks, but use different configs that sets
    local discovery sub-systems. This tasks can be used to test different
    ZeroConf implementations compatibilities and/or JOD object tolerance to
    test Discovery/Network errors.<br>

    | Disc   | Discovery sub-system |
    |--------|----------------------|
    | Avahi  | Use the avahi damon installed on hosting Operating System |
    | JmDNS  | Use the JmDNS implementation of the ZeroConfig protocol   |
    | JmmDNS | Use the JmDNS implementation of the ZeroConfig protocol   |
    
  For most used tasks ```javaJODRun``` and ```javaJODVanillaRun``` task modifiers
  are available adding strings ```OnlyCloud```, ```OnlyLocal``` and ```NoComm```
  to the tasks name ```javaJOD{Mod}Run``` and ```javaJODVanilla{Mod}Run```.<br>
  Each modifier start a JOD agent instance with configurations that disable the
  cloud communication, or disable the local communication or disable both
  communications.
    
**Cleaner tasks:**<br>
  * ```./gradlew javaJOD_Clean```<br>
    delete working dir of ```javaJODRun``` task.<br>
    **NB:** not yet implemented ####.

**Publish tasks:**<br>
  The JOSP JOD publication package and publish following files:
  
  | File | Content |
  |------|---------|
  | ```jod-{VERSION}-java.jar``` | Jar file used to run the JOD Agent as a interactive shell or a daemon |
  | ```jod-{VERSION}-src.jar```  | Src package containing all sources used to compile the JOSP JOD agent |
  | ```jod-{VERSION}-doc.jar```  | Docs archive contains all Java docs from JOD's source code |
  | ```jod-{VERSION}-deps.jar``` | Deps jar archive provide all dependencies required by the JOSP JOD agent |
   
  * ```./gradlew jospJOD_PublishToLocal```<br>
    generate the publication artifacts and publish them to local maven repo.
   
  * ```./gradlew jospJOD_PublishToSonatype```<br>
    generate the publication artifacts and publish them to public Sonatype repo.


## Properties

*??jod.yaml??:*
...

*??struct.jod??:*
...

*??log4j.xml??:*
...


## Connections

**From clients:**<br>
  Clients: JSL

**To JCP APIs:**<br>
  [JCP APIs - Connections](../jcpAPIs/README.md#Connections)

**To JOSP GWs:**<br>
  [JOSP GWs - Connections](../jospGWs/README.md#Connections)
