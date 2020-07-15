# John O.S. Project

This is the main repository of the Open Source John O.S. Project.

The John O.S. Project (JOSP for friends) simplify the design and build process
of modern [Internet of Things solutions](docs/josp_ecosystems.md):
* reduces smart object's creation a 5 minutes activity, even without coding skills
* it's possible integrate any kind of connected object, regardless of communication protocol used by the object
* develop custom software that easily interact with objects
* startups local and cloud connections between service and objects out-of-the-box
* it provides communication security and object's access control by design
* it's completely Open Source


## Project goal

The JOSP Project aim is to provide all necessary component to
create a secure, out-of-the-box Internet of Things eco-system, including the
object's agent, the services library and their cloud communication platform.<br>
The object's agent ([JOSP JOD](docs/jospJOD/README.md)) can be installed on a
smart object and allow to expose all object's status and actions to services.
Services are all software that include the ([JOSP JSL](docs/jospJSL/README.md))
library. This library allows any kind of software to interact with JOD objects:
listing and filter available objects, getting status updates and send action
commands.<br>
Even if object's and services can communicate directly (when on the same network)
the [JCP APIs](docs/jcpAPIs/README.md) and the [JOSP GWs](docs/jospGWs/README.md)
components allow objects and services to communicate remotely via Cloud.

Moreover, the JOSP platform allow managing users/services access to the objects.
For example an object can be available to any user/service or another one can be
accessible only by specific user/service pair. Only the object's owners can grant
or revoke user/service permissions.

[Here](docs/INDEX.md) the full JOSP documentation.


## Getting started

...understand which kind of IoT eco-system do you need

Startup a minimal IoT eco-system (only object and service):
1. Setup and start the JOSP JOD Agent
1. Setup and start the testing JSL service

Startup a complete IoT eco-system (locally):
1. [OPT.] Start the JOSP Cloud Platform
  1. Start the DBMS and Auth micro-services
  1. Start the JCP APIs and JOSP GWs micro-service


## JOSP Overview

The JOSP project split an IoT eco-system in 3 different components:
* Objects: any instances of [JOSP JOD](docs/jospJOD/README.md) agent
* Services: any software that include the [JOSP JSL](docs/jospJSL/README.md) library
* JOSP Cloud Platform: the cloud infrastructure used for remote object-service
  communication

**Objects:**<br>
  Depending on the object to integrate there are different ways to integrate it:
  * JOSP Native objects
  * JOSP Hub objects
  * JOSP Web Objects
  Once started, the [Jhon Object Daemon (JOD)](docs/jospJOD/README.md) agent
  try to connect to the JOSP Cloud Platform and publish his self to
  [ZeroConf](https://it.wikipedia.org/wiki/Zeroconf) that allow JSL service
  connect to object directly (when on the same local network) and via JOSP
  Cloud Platform (trough classic internet connections).<br>
  For testing purposes this project provide gradle's tasks. With these tasks it's
  possible execute the JOD agent with different configurations on the local machine.

**Services:**<br>
  Any kind of software (mobile/desktop/web apps, cloud service, daemon, cmdLine, etc..)
  can include the JSL library.<br>
  The [John Service Library (JSL)](docs/jospJSL/README.md) provides to 3rd party
  software the ability to authenticate users (or keep them anonymous), to list
  all available objects (for authenticated user) and to interact with that objects.<br>
  From the JSL library point of view an object is a collection of status and actions.
  So the 3rd party software can listen for status's updates or send action commands
  to the objects, without to worry about connection, communication or security
  configurations.

**Cloud Platform:**<br>
  The JOSP Cloud Platform (JCP) allow objects and service to communicate when are not
  on the same network. That platform is composed by different micro-services,
  all provided within this project:
  * a DBMS service
  * an AAA service
  * the JCP APIs
  * the JOSP GWs
  ...
  Public JCP...


## Collaborate

...


## Licences

JOSP Project's provide different software (JOSP Components) and each one of them
have the corresponding licence. In the following table the list of JOSP Components
and their licences.

| Component | Licence |
|-----------|---------|
| JOSP Commons | [GPLv3](src/jospCommons/resources/LICENCE) |
| JOSP JOD     | [GPLv3](src/jospJOD/resources/LICENCE) |
| JOSP JSL     | [Apache Licence 2.0](src/jospJSL/resources/LICENCE) |
| JCP APIs     | [AGPLv3](src/jcpAPIs/resources/LICENCE) |
| JOSP GWs     | [AGPLv3](src/jcpAPIs/resources/LICENCE) |
| JCP FrontEnd | [AGPLv3](src/jcpFrontEnd/resources/LICENCE) |
| BuildSrc     | [GPLv3](buildSrc/LICENCE) |
