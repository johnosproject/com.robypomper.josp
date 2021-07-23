# JOSP Overview

The JOSP project split an IoT eco-system in 3 different components corresponding to the 3 main JOSP components:

* **Objects:** any instances of [JOSP JOD](jospJOD/README.md) agent
* **Services:** any software that include the [JOSP JSL](jospJSL/README.md) library
* **JOSP Cloud Platform:** the [cloud services](jcp/README.md) used for remote object-service communication

Bellow you can find a description for each component and how they collaborate in an IoT solution.

----

## Objects

Depending on the object to integrate there are different ways to integrate it:

* JOSP Native objects
* JOSP Hub Objects
* JOSP Web Objects

Once started, the John Object Daemon (JOD) agent try to connect to the JOSP Cloud Platform and publish his self to [ZeroConf](https://it.wikipedia.org/wiki/Zeroconf)
that allow JSL service connect to object directly (when on the same local network)
and via JOSP Cloud Platform (trough classic internet connections).

For testing purposes this project provide gradle's tasks. With these tasks it's possible execute the JOD agent with different configurations on the local machine.

----

## Services

Any kind of software (mobile/desktop/web apps, cloud service, daemon, cmdLine, etc..)
can include the JSL library.

The John Service Library (JSL) provides to 3rd party software the ability to authenticate users (or keep them anonymous), to list all available objects
(for authenticated user) and to interact with that objects.

From the JSL library point of view an object is a collection of status and actions. So the 3rd party software can listen for status's updates or send action commands to the objects, without to worry about connection, communication or security configurations.

----

## Cloud Platform

The JOSP Cloud Platform (JCP) allow objects and service to communicate when are not connected to the same network. That platform is composed by different micro-services, all provided within this project:

* [JCP DBMS](jcpDBMS/README.md): service to store the AAA and the JCP databases
* [JCP Auth](jcpAuth/README.md): service to register and manage objects, services and users
* [JCP APIs](jcpAPIs/README.md): for objects, services and jcp micro-services coordination
* [JCP Gateways](jcpGWs/README.md): to forward messages from objects 2 services and vice versa
* [JCP JSL Web Bridge](jcpJSLWB/README.md):  to allow JSL services as web client implementation
* [JCP Front End](jcpFE/README.md): to display JCP objects and info in a web portal
* [JCP All](jcpAll/README.md): join all jcp services in one (not working)

        * a [JCP DB](http://www.johnosproject.org/docs/References/JOSP_Components/JCP%20MicroServices/JCP%20Database/Home) service to store the AAA and the JCP databases
        * an [JCP Auth](http://www.johnosproject.org/docs/References/JOSP_Components/JCP%20MicroServices/JCP%20Auth/Home) service to register and manage objects, services and users
        * the [JCP APIs](http://www.johnosproject.org/docs/References/JOSP_Components/JCP%20MicroServices/JCP%20APIs/Home) for objects and services
        * the [JOSP Gateways](http://www.johnosproject.org/docs/References/JOSP_Components/JCP%20MicroServices/JCP%20Gateways/Home) to forward messages from objects 2 services and vice versa
        * the [JOSP JSL Web Bridge](http://www.johnosproject.org/docs/References/JOSP_Components/JCP%20MicroServices/JCP%20JSL%20Web%20Bridge/Home) to allow JSL services as web client implementation
        * the [JOSP Front End](http://www.johnosproject.org/docs/References/JOSP_Components/JCP%20MicroServices/JCP%20Front%20End/Home) to display JCP objects and info in a web portal

----

## JOSP Components list

* [<div style='opacity: 60%'>JOSP</div>](josp/README.md)
    * [<div style='opacity: 60%'>JOSP APIs</div>](jospAPIs/README.md)
    * [<div style='opacity: 60%'>JOSP Commons</div>](jospCommons/README.md)
    * **[<div style='opacity: 100%'>JOSP JOD](jospJOD/README.md)**
    * **[<div style='opacity: 100%'>JOSP JSL</div>](jospJSL/README.md)**
* **[<div style='opacity: 100%'>JCP</div>](jcp/README.md)**
    * [<div style='opacity: 60%'>JCP Commons</div>](jcpCommons/README.md)
    * [<div style='opacity: 60%'>JCP DB</div>](jcpDB/README.md)
        * [<div style='opacity: 60%'>JCP DB APIs</div>](jcpDB/apis/README.md)
        * [<div style='opacity: 60%'>JCP DB FE</div>](jcpDB/fe/README.md)
    * [<div style='opacity: 60%'>JCP Service</div>](jcpService/README.md)
        * [<div style='opacity: 100%'>JCP DBMS</div>](jcpDBMS/README.md)
        * [<div style='opacity: 100%'>JCP Auth</div>](jcpAuth/README.md)
        * [<div style='opacity: 100%'>JCP APIs</div>](jcpAPIs/README.md)
        * [<div style='opacity: 100%'>JCP Gateways</div>](jcpGWs/README.md)
        * [<div style='opacity: 100%'>JCP JSL Web Bridge</div>](jcpJSLWB/README.md)
        * [<div style='opacity: 100%'>JCP Front End</div>](jcpFE/README.md)
        * [<div style='opacity: 100%'>JCP All</div>](jcpAll/README.md)