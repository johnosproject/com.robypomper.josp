**This is the main repository of the Open Source John O.S. Project.**

![John O.S. Project Logo](docs/media/logo_text_full_500.png "John O.S. Project")

The **John O.S. Project simplify the design and build process of modern Internet of Things solutions**:

* reduces smart object's creation a 5 minutes activity, even without coding skills
* it's possible integrate any kind of connected object, regardless of communication protocol used by the object
* develop custom software that easily interact with objects
* startups local and cloud connections between service and objects out-of-the-box
* it provides communication security and object's access control by design
* it's completely Open Source

The John O.S. Project (JOSP for friends) provide a suite of softwares and tools to create **any kind of [IoT Solutions](https://www.johnosproject.org/docs/What/IoT%20Solutions)**
in minutes. The main software supplied includes:

* **[John Object Daemon](https://www.johnosproject.org/docs/References/JOSP_Components/John%20Object%20Daemon/Home)** the agent to connect objects
* **[John Service Library](https://www.johnosproject.org/docs/References/JOSP_Components/John%20Service%20Library/Home)** the library to include in your software
* **[John Cloud Platform](https://www.johnosproject.org/docs/References/JOSP_Components/John%20Cloud%20Platform/Home)** the cloud platform to provide internet connectivity

This project also provides Tools to help makers and developers to speed up IoT objects and services development. Moreover, we provide a public/shared instance of the JOSP Cloud Platform that anyone can use as his personal IoT EcoSystem.

**The Public John Cloud Platform instance is reachable
[here](https://www.johnosproject.org/frontend/index.html).**

**Note:** These docs are intended for JOSP developers. Makers, Developers and End Users can look at the **[JOSP Project's Documentation](https://www.johnosproject.org/docs/)**.

JOSP's developers can found more info about the [JOSP project](docs/overview.md)
and his [development](docs/development.md) in this documentation. For a rapid IoT EcoSystem startup, read next chapter.

----

## Getting started with source code

To start working with JOSP project you must clone this repository via [Git](https://git-scm.com)
then with included [Gradle](https://gradle.org) Wrapper you can execute project's tasks to run your own IoT Eco-System. That require Git and Java JDK installed on your pc. If you would also execute the JCP Cloud platform, then Doker and docker-compose are also required.

**Note:** This getting started is intended for JOSP developers. Makers, Developers and End Users can look at the **JOSP Project's Documentation
[getting started](https://www.johnosproject.org/docs/#getting-started)**.

1. [Install Git](https://www.johnosproject.org/docs/external/git/install)
1. [Install Java JDK](https://www.johnosproject.org/docs/external/java/install-jdk)
1. [Install Docker and docker-compose](https://www.johnosproject.org/docs/external/docker/install) (Opt. to run JCP Cloud)
1. Clone com.robypomper.josp repository
    ```shellscript
    git clone https://bitbucket.org/johnosproject_shared/com.robypomper.josp.git
    ```
1. Enter cloned directory and start executing Gradle tasks via wrapper
    ```shellscript
    cd com.robypomper.josp
    ./gradlew --console=plain {TASK_NAME}
    ```

### Local JOSP Eco-System

The first, **basic IoT Eco-System** is composed only by a connected object, and by a service to interact with. To run a basic IoT Eco-System it's required to run the
[JOD object agent](https://www.johnosproject.org/docs/References/JOSP_Components/John%20Object%20Daemon/Home), and the shell version of the [JSL library](https://www.johnosproject.org/docs/References/JOSP_Components/John%20Service%20Library/Home). The only requirement is that both instance have to be connected to the same local network.

1. Run the John Object Daemon (JOD)
   ```shellscript
   ./gradlew javaJODRun
   ```
1. Run the John Service Library Shell
   ```shellscript
   ./gradlew javaJSLRun
   ```

That's it! **Your IoT EcoSystem is running!**

The JOD agent, when executed, starts and publish his local server. At the same time the JSL library, when initialized, starts his local object's discovery. When the JSL library detect an object, it starts the direct communication (Srv2Obj); after the SSL handshaking, the JOD agent (depending on object's permission) send his presentation messages to the service. At this point, if the JSL service has right permissions, can send action command and will receive all statuses updates to/from JOD agent.
**Object's presentation are send only to JSL service according to object's access permissions.**

Now, JSL Shell can interact with objects. Both softwares, the JOD and JSL Shell, provide a shell interface to interact with them. Type ```?list``` to print shell's command list or check out the [JOD Shell](https://www.johnosproject.org/docs/References/JOSP_Components/John%20Object%20Daemon/Shell_Commands)
and [JSL Shell](https://www.johnosproject.org/docs/References/JOSP_Components/John%20Service%20Library/Shell_Commands)
commands list.

Gradle's **```javaJODRun``` task run a full working instance of the JOD agent** that represent a development object, you can [customize this object's structure](docs/jospJOD/configure_object_structure.md)
using example structures like [struct_LINUX.jod](src/jospJOD/configs/struct_LINUX.jod)
or [struct_MAC.jod](src/jospJOD/configs/struct_MAC.jod) files. This task once runned first time, run always the same JOD Object from ```envs/runnubles/jod/JOD```
dir. For other execution options see the [JOD Runners](docs/jospJOD/tasks_groups.md#runner-tasks)
tasks group.

Gradle's **```javaJSLRun``` task run a shell application that implement a basic JSL service**. This JSL service translate all JSL features in [interactive commands](https://www.johnosproject.org/docs/References/JOSP_Components/John%20Service%20Library/Shell_Commands). Like for ```javaJODRun``` task, the ```javaJSLRun``` task generate his configs on first execution and then stored them in ```envs/runnables/jsl/JSL```
dir. Other execution options are available at [JSL Runners](docs/jospJSL/tasks_groups.md#runner-tasks)
tasks group.

To **stop the JOD agent or the JSL Shell**, type ```exit``` to the corresponding shell or kill them via their PID.

You can run first the JOD agent and second the JSL Shell or vice versa. Both JOSP components print log messages on the console and save them on files in their ```logs``` sub-dir. So to run multiple instances of JOD and/or JSL you must run them in different terminals.

### Cloud JOSP Eco-System

JOD objects and JSL services can communicate directly when on the same local network, or remotely via the JOSP Cloud Platform. The JCP is composed by 4 micro-services:

* [JCP DB](https://www.johnosproject.org/docs/References/JOSP_Components/JCP%20MicroServices/JCP%20Database/Home): manage the DBs (jcp_auth, jcp_apis...) for other micro-services
* [JCP Auth](https://www.johnosproject.org/docs/References/JOSP_Components/JCP%20MicroServices/JCP%20Auth/Home): provide the authentication and authorization service
* [JCP APIs](https://www.johnosproject.org/docs/References/JOSP_Components/JCP%20MicroServices/JCP%20APIs/Home): basic JCP APIs required by JOSP objects and services
* [JOSP Gateways](https://www.johnosproject.org/docs/References/JOSP_Components/JCP%20MicroServices/JCP%20Gateways/Home): the cloud gateways that acts as bridge between JOSP objects and services
* [JOSP JSL Web Bridge](https://www.johnosproject.org/docs/References/JOSP_Components/JCP%20MicroServices/JCP%20JSL%20Web%20Bridge/Home): the JSL's HTTP APIs manager for JSL services as web clients
* [JOSP Front End](https://www.johnosproject.org/docs/References/JOSP_Components/JCP%20MicroServices/JCP%20Front%20End/Home): the JCP front end

When the IoT Eco-System require remote connectivity, objects and services with the Cloud Communication enabled can communicate via the John Cloud Platform that acts as a bridge. When a JSL service connect to the JCP Gateways, it responds with object's presentations messages so the JSL service know all available objects (via cloud). At the same time, when it's a JOD agent that connects to JCP Gateways, it sends his presentation to the JCP Gateways, that's forwarded to the connected JSL services. **Object's presentation are send only to JSL service according to object's access permissions.**

To **startup and shutdown all JCP micro-services** once, run one of following Gradle's tasks: ```jospCloud_Start``` or ```jospCloud_Stop```. When are running you can print JCP micro-service's logs with following command:

```shellscript
$ ./gradlew jospCloud_Start
$ tail -f tail -f envs/runnables/jcp/{JCP_SERVICE}_StartAsync/jcp.log_{START_DATE_TIME}
Ctrl+C
```

To reduce time wast during micro-service restart, micro-services are parted in two groups: [Docker](docs/jcp/tasks_groups.md#jcp-docker) and
[Soft](docs/jcp/tasks_groups.md#jcp-soft). Each group can be started and stopped independently to the other one. *Be careful to startup the Soft's micro-services only when all Docker's micro-services are running.*. There are also tasks for startup and shutdown each [micro-service individually](docs/jcp/tasks_groups.md#jcp-services).

When the John Cloud Platform is running, you can start JOD and JSL instances with their ```javaJODRun``` and ```javaJSLRun``` tasks. Previous tasks start JOD and JSL instances using their default configs
([jod.yml](src/jospJOD/configs/jod_default.yml) and
[jsl.yml](src/jospJSL/configs/jsl_default.yml)) that **enable by default Local and Cloud communications** on both components.

Once started, both JOD and JSL can be connected/disconnected to the JCP Cloud and local communication started/stopped with corresponding shell commands
([JOD Shell's cmds](docs/jospJOD/jod_shellcmds.md) and
[JSL Shell's cmds](docs/jospJSL/jsl_shellcmds.md)).

To run the JOD and JSL components with different communication configs you can use the tasks from [JOD Runners Alternative](docs/jospJOD/tasks_groups.md#alternative)
and [JSL Runners Alternative](docs/jospJSL/tasks_groups.md#alternative).

Combining this tasks, allow you to **simulate different
[IoT Solutions](https://www.johnosproject.org/docs/What/IoT%20Solutions) architectures**.

----

## Collaborate

**Any kind of collaboration is welcome!** This is an Open Source project, so we are happy to share our experience with other developers, makers and users. Bug reporting, extension development, documentation and guides etc... are activities where anybody can help to improve this project.

One of the John O.S. Project’s goals is to release more John Objects Utils & Apps to allow connecting even more connected objects from other standards and protocols. Checkout the Utils & Apps extensions list and start collaborating with a development team or create your own extension.

At the same time we are always looking for new use cases and demos. So, whether you have just an idea or are already implementing your IoT solution, don't hesitate to contact us. We will be happy to discuss with you about tech decisions and help build your solution with John’s component.

Please email to [tech@johnosproject.com](mailto:tech@johnosproject.com).

----

## Versions

John Operating System Project provided softwares must communicate togheter and with 3rd party softwares. To do that they use different protocols or interfaces. Depending on the protocol or interface supported versions a software can/cannot communicate with others. Here the protocols and interfaces versions supported by this release. That means each software build from current source code's release can communicate with software that also support one of following versions.

| Protocol / Interface | Supported Versions |
|----------------------|--------------------|
| [JOSP Protocol](docs/josp/internal_versions.md#JOSP-Protocol)   | 2.0    |     1st JOSP version for JOSP>2
| [JCP APIs Ver](docs/josp/internal_versions.md#JCP-APIs)         | 2.0    |     1st JCP APIs API version for JOSP>2
| [JSL APIs Ver](docs/josp/internal_versions.md#JSL-APIs)         | 1.0    |     1st JCP JSL Web Bridge's API version
| [JOD Executors](docs/josp/internal_versions.md#JOD-Executor)    | 2a     |     JOSP>2 Executors>a
| [JOD Structure](docs/josp/internal_versions.md#JOD-Structure)   | 2a     |     JOSP>2 Structure>a
| [JSL Interface](docs/josp/internal_versions.md#JSL-Interface)   | 2.2.0  |     Same as current release

All components from current source code's release are build with **2.2.0** version.

**Older version of JOSP source code:**

* v [2.2.0](https://bitbucket.org/johnosproject_shared/com.robypomper.josp/src/2.2.0/)
* v [2.1.0](https://bitbucket.org/johnosproject_shared/com.robypomper.josp/src/2.1.0/)
* v [2.0.0](https://bitbucket.org/johnosproject_shared/com.robypomper.josp/src/2.0.0/)

----

## Licences

JOSP Project's provide different software (JOSP Components) and each one of them
have the corresponding licence. In the following table the list of JOSP Components
and their licences.

| Component | Licence |
|-----------|---------|
| JOSP JOD              | [GPLv3](LICENSES/John Object Daemon) |
| JOSP JSL              | [Apache Licence 2.0](LICENSES/John Service Library) |
| JCP APIs              | [AGPLv3](LICENSES/John Cloud Platform) |
| JCP Gateways          | [AGPLv3](LICENSES/John Cloud Platform) |
| JCP JSL Web Bridge    | [AGPLv3](LICENSES/John Cloud Platform) |
| JCP Gateways          | [AGPLv3](LICENSES/John Cloud Platform) |
| JCP Front End         | [AGPLv3](LICENSES/John Cloud Platform) |

This project use different other Open Source software, click here for the
[JOSP dependencies list](docs/josp/external_dependencies.md) and their licences. 
