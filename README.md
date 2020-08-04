# John O.S. Project

This is the main repository of the Open Source John O.S. Project.

The John O.S. Project (JOSP for friends) simplify the design and build process
of modern [Internet of Things solutions](docs/josp_ecosystems.md):

* reduces **smart object's creation a 5 minutes activity**, even without coding skills
* it's possible integrate any kind of connected object, regardless of communication protocol used by the object
* develop custom **software that easily interact with objects**
* startups local and cloud connections between service and objects out-of-the-box
* it provides communication **security and object's access control by design**
* it's completely Open Source


## Project goal

The JOSP Project aim is to provide all necessary component to
create an out-of-the-box Internet of Things eco-system, including the
object's agent, the services library and their cloud communication platform.

    Image: Obj-Srv-Cloud diagram

* The **Object's agent [JOSP JOD](docs/jospJOD/README.md)** can be installed on a
smart object and allow to expose all object's status and actions to services
like the environment temperature or ths light status.
* To the other side, Services are **all software that include the 
[JOSP JSL](docs/jospJSL/README.md) library**. This library allows software 
easily interact with JOD objects:
  1. listing and filter available objects,
  1. getting status updates and
  1. send action commands.
* The **cloud components [JCP APIs](docs/jcpAPIs/README.md) and the
[JOSP GWs](docs/jospGWs/README.md)** components allow objects and services to
communicate also remotely via Cloud.

Moreover, the JOSP platform allow managing users/services access to the objects.
For example an object can be available to any user/service or another one can be
accessible only by specific user/service pair. Only the object's owners can grant
or revoke user/service permissions.

To start experiment with your own IoT Eco-System follow instructions in the
next "Getting started" chapter or see the [JOSP Project documentation](docs/INDEX.md).
----


## Getting started

To start working with JOSP project you must clone this repository via [Git](https://git-scm.com)
then with include [Gradle](https://gradle.org) Wrapper you can execute project's tasks to run
your IoT Eco-System. Gradle and JOSP project require Java JDK installed on your pc (Java 14
actually not supported).

Here a brief description on [installing JOSP's pre-requisite](docs/josp_prerequisites.md).

### Local JOSP Eco-System

The first, **basic IoT Eco-System** is composed only by a connected object, and by a
service to interact with. To run a basic IoT Eco-System it's required to run the
JOD object agent, and the shell version of the JSL library. The only requirement is
that both instance have to be connected to the same local network.

1. run the JOSP Object Daemon (JOD) ```./gradlew javaJODRun```

1. run the JOSP Service Library Shell ```./gradlew javaJSLRun```

That's it! **Your IoT EcoSystem is running!**

Now, from the JSL Shell you can [list objects](docs/jospJSL/jsl_shellcmds.md#List-objects)
and print their [structures and stauses](docs/jospJSL/jsl_shellcmds.md#Object-structure-and-status).
Depending on the object's structures you can also
[send action commands](docs/jospJSL/jsl_shellcmds.md#Send-action-commands)
from JSL Shell and receive status updates. Both softwares, the JOD and JSL Shell,
provide a shell interface to interact with them. Type ```?list``` to print shell's
command list.

```javaJODRun```  task run a full working instance of the JOD agent that represent a
[generic object](docs/jospJOD/jod_defaults.md#Structure-development-default). After his
first execution, when initialize JOD's IDs, that command run the same JOD objects
because it stores his configs on [```envs/runnables/jod/JOD```](envs/runnables/jod/JOD)
directory.
To execute a new (vanilla) JOD object, use the task ```javaJODVanilla```, this task
clean the JOD working dir at each execution. For more JOD execution tasks see
[JOD: Gradle tasks](docs/jospJOD/README.md#Runner-tasks).

```javaJSLRun``` task run a shell application that implement a basic JSL service. This JSL service
translate all JSL features in [interactive commands](docs/jospJSL/jsl_shellcmds.md).
His configs are generated on first execution and then stored in
[```envs/runnables/jsl/JSL```](envs/runnables/jsl/JSL) dir.
Like for the JOD agent, you can execute a new (vanilla) JSL Shell with task
```javaJSLVanilla```. For more JOD execution tasks see
[JSL: Gradle tasks](docs/jospJSL/README.md#Runner-tasks).

The JOD agent, when executed, starts and publish his local server. At the same time the JSL
library, when initialized, starts his local object's discovery. When the JSL library
detect an object, it starts the local communication (Srv2Obj); after the SSL
handshaking, the JOD agent (depending on object's permission) send his presentation
messages to the service. At this point the JSL service can send action command and will
receive all states updates to/from JOD agent.

To stop the JOD agent or the JSL Shell, type ```exit``` to the corresponding shell.

On disabling Local Communication, the JOD and JSL instances lost their
connection. The object is not more available to the service, but the JSL
library keep in memory all object's states until object's next reconnection
or JSL service shutdown.

You can run first the JOD agent and second the JSL Shell or vice versa. Both
JOSP components print log messages on the console and save them on files in
their ```logs``` sub-dir. So to run multiple instances of JOD and/or JSL you
must run them in different terminals.


### Cloud JOSP Eco-System

JOD objects and JSL services can communicate directly when on the same local network,
or remotely via the JOSP Cloud Platform. The JCP is composed by 4 micro-services:

* [DBMS](docs/dockers/dbms.md): manage the DBs (auth_josp and jcp_apis) for other micro-services
* [AAA](docs/dockers/auth.md): provide the authentication and authorization service
* [JCP APIs](docs/jcpAPIs/README.md): basic JCP APIs required by JOSP objects and services
* [JOSP GWs](docs/jospGWs/README.md): the cloud gateways that acts as bridge between JOSP objects and services

To startup all JCP micro-services run:

```
# To startup all JCP micro-services run:
$ ./gradlew jospCloud_Start```
$ tail -f tail -f envs/runnables/jcp/jcpAPI_StartAsync/jcp.log_{START_DATE_TIME}
Ctrl+C

# Then to stop all JCP micro-services run:
$ ./gradlew jospCloud_Stop
```

When the IoT Eco-System require remote connectivity, objects and services
with the Cloud Communication enabled can communicate via the JOSP Cloud
Platform that acts as a bridge ([JOSP GWs](docs/jospGWs/README.md)).
When a JSL service connect to the JOSP GWs, it responds with object's
presentations messages so the JSL service know all available objects (via cloud).
At the same time, when it's a JOD agent that connects to JOSP GWs, it sends his
presentation to the JOSP GWs, that's forwarded by to the connected JSL services.
*Always object's presentation are send only to JSL service according to
object's access permissions.* 

To reduce time wast during micro-service restart, micro-services are parted in
two groups: Docker and Soft. Each group can be started and stopped independently
to the other one. *Be careful to startup the Soft's micro-services only when all
Docker's micro-services are running.*


### Mixed JOSP Eco-System

Previous tasks start JOD and JSL instances using their default configs
([jod.yml](src/jospJOD/configs/jod_default.yml) and
[jsl.yml](src/jospJSL/configs/jsl_default.yml)) that **enable by default 
Local and Cloud communications** on both components.

Once started, both JOD and JSL can be connected/disconnected to the JCP Cloud
and local communication started/stopped with corresponding shell commands
([JOD Shell's cmds](docs/jospJOD/jod_shellcmds.md) and
[JSL Shell's cmds](docs/jospJSL/jsl_shellcmds.md)).

To run the JOD and JSL components with different communication configs you
can use the tasks as described in the bellow tables.

**JOD alternative runner tasks**

| Gradle Tasks                                                  | Configs used                                      | Description |
|---------------------------------------------------------------|---------------------------------------------------|-------------|
| ```javaJODOnlyLocalRun```<br>```javaJODVanillaOnlyLocalRun``` | [jod_only-local.yml](src/jospJOD/configs/jod_only-local.yml) | Start a JOD agent with Cloud Comm disabled |
| ```javaJODOnlyCloudRun```<br>```javaJODVanillaOnlyCloudRun``` | [jod_only-cloud.yml](src/jospJOD/configs/jod_only-cloud.yml) | Start a JOD agent with Local Comm disabled |
| ```javaJODNoCommRun```<br>```javaJODVanillaNoCommRun```       | [jod_no-comm.yml](src/jospJOD/configs/jod_no-comm.yml)    | Start a JOD agent with Cloud and Local Comm disabled |


**JSL alternative runner tasks**

| Gradle Tasks                                                  | Configs used                                      | Description |
|---------------------------------------------------------------|---------------------------------------------------|-------------|
| ```javaJSLOnlyLocalRun```<br>```javaJSLVanillaOnlyLocalRun``` | [jsl_only-local.yml](src/jospJSL/configs/jsl_only-local.yml) | Start a JSL Shell with Cloud Comm disabled |
| ```javaJSLOnlyCloudRun```<br>```javaJSLVanillaOnlyCloudRun``` | [jsl_only-cloud.yml](src/jospJSL/configs/jsl_only-cloud.yml) | Start a JSL Shell with Local Comm disabled |
| ```javaJSLNoCommRun```<br>```javaJSLVanillaNoCommRun```       | [jsl_no-comm.yml](src/jospJSL/configs/jsl_no-comm.yml)    | Start a JSL Shell with Cloud and Local Comm disabled |

To the other side, using the vanilla's runners for JOD agents and JSL service,
multiple instances can be executed: each JOD agents representing a different
object and each JSL Shell a different service.

Combining this tasks, allow you to **simulate different
[IoT Eco-Systems](docs/josp_ecosystems.md) architectures**.

All Eco-Systems generated with Gradle's tasks, can be used with different objects and
services configurations. Objects can be of different types with
[different obejct's structures](docs/jospJOD/jod_config.md) or the JOSP JOD agent can be
[installed on different HW platforms](docs/jospJOD/jod_install.md). All right the JSL services can be
implemented and executed almost independently to the JOSP Project, except for
their [dependency to the JSL library](docs/jospJSL/jsl_include.md) (p.e. via Maven
repository).

More info on customizing and use JOSP objects and services see the documentation
[INDEX](docs/INDEX.md).
----


## JOSP Overview

The JOSP project split an IoT eco-system in 3 different components:

* Objects: any instances of [JOSP JOD](docs/jospJOD/README.md) agent
* Services: any software that include the [JOSP JSL](docs/jospJSL/README.md) library
* JOSP Cloud Platform: the cloud infrastructure used for remote object-service
  communication


### Objects

Depending on the object to integrate there are different ways to integrate it:

* JOSP Native objects
* JOSP Hub Objects
* JOSP Web Objects

Once started, the [John Object Daemon (JOD)](docs/jospJOD/README.md) agent
try to connect to the JOSP Cloud Platform and publish his self to
[ZeroConf](https://it.wikipedia.org/wiki/Zeroconf) that allow JSL service
connect to object directly (when on the same local network) and via JOSP
Cloud Platform (trough classic internet connections).

For testing purposes this project provide gradle's tasks. With these tasks it's
possible execute the JOD agent with different configurations on the local machine.


### Services

Any kind of software (mobile/desktop/web apps, cloud service, daemon, cmdLine, etc..)
can include the JSL library.

The [John Service Library (JSL)](docs/jospJSL/README.md) provides to 3rd party
software the ability to authenticate users (or keep them anonymous), to list
all available objects (for authenticated user) and to interact with that objects.

From the JSL library point of view an object is a collection of status and actions.
So the 3rd party software can listen for status's updates or send action commands
to the objects, without to worry about connection, communication or security
configurations.


### Cloud Platform

The JOSP Cloud Platform (JCP) allow objects and service to communicate when are not
connected to the same network. That platform is composed by different micro-services,
all provided within this project:

* a [DBMS](docs/dockers/dbms.md) service to store the AAA and the JCP databases
* an [AAA](docs/dockers/auth.md) service to register and manage objects, services and users
* the [JCP APIs](docs/jcpAPIs/README.md) for objects and services
* the [JOSP GWs](docs/jospGWs/README.md) to forward messages from objects 2 services and vice versa
----


## Collaborate

**Any kind of collaboration is welcome!** This is an Open Source project,
so we are happy to share our experience with other developers, makers and users.
Bug reporting, extension development, documentation and guides etc... are activities
where anybody can help to improve this project.

Please email to [mailto:developers@johnosproject.com].


## Versions

John Operating System Project provide a set of software and protocols. Bellow the 
versions buildable with this **JOSP's release 2.0.0**:

| Component | Latest Version |
|-----------|----------------|
| [JOSP Protocol](docs/josp_versions.md#JOSP-Protocol) | 2.0 |
| [JCP APIs Ver](docs/josp_versions.md#JCP-APIs) | 2.0 |
| JOSP Commons  | unset |
| [JOSP JOD](docs/josp_versions.md#JOSP-JOD) | 2.0.0 |
| [JOSP JSL](docs/josp_versions.md#JOSP-JSL) | 2.0.0 |
| [JCP APIs](docs/josp_versions.md#JCP-APIs) | 2.0 |
| [JOSP Gateways](docs/josp_versions.md#JOSP-Gateways) | same as JCP APIs |
| BuildSrc      | unset |

More info on current JOSP project's version [here](docs/josp_versions.md).

Older version of JOSP projects:

* [2.0.0](https://bitbucket.org/johnosproject/com.robypomper.josp/src/2.0.0/)


## Licences

JOSP Project's provide different software (JOSP Components) and each one of them
have the corresponding licence. In the following table the list of JOSP Components
and their licences.

| Component | Licence |
|-----------|---------|
| JOSP Commons | [GPLv3](LICENSES/John Object Daemon) |
| JOSP JOD     | [GPLv3](LICENSES/John Object Daemon) |
| JOSP JSL     | [Apache Licence 2.0](LICENSES/John Service Library) |
| JCP APIs     | [AGPLv3](LICENSES/John Cloud Platform) |
| JOSP GWs     | [AGPLv3](LICENSES/John Cloud Platform) |
| JCP FrontEnd | [AGPLv3](LICENSES/John Cloud Platform) |
| BuildSrc     | [GPLv3](LICENSES/John Object Daemon) |

This project use different other Open Source software,
[here](docs/josp_dependencies.md#Licences) for the full list and their licences.