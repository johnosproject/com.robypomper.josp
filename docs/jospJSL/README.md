# JOSP JSL

The **JOSP JSL** is the software library used by 3rd party software (JOSP Services)
to interact with connected Objects (represented by the JOSP JOD agent).
Developers can easily [include the JSL library](jsl_include.md) into their own
software and start interacting with the JOSP eco-system.

After initialize the JSL library, developers can list all [available objects](jsl_listobjs.md),
both connected locally and remotely. Depending on whether the user is logged in
or not, the available objects may vary. If the user was not logged in, then
he can only list objects accessible by anonymous users. Otherwise, after the
[user logged in](jsl_userlogin.md), the JSL library provide the list of all
objects accessible by the logged user.

Because available objects will grow really fast, it's very important for developers
identify right objects to interact with. For this purpose, the JSL library provide
a complete [object search engine](jsl_ose.md) that allow developers filter
objects by their features: brand, model, states and actions among others.

Once identified right objects, developers can browse [object's structures](../jospJOD/structure.md)
(the collection of states and actions exposed by each object); and then register
their own listener to states changes or send action commands (see
"[interact with objects](jsl_objinteractions.md)").

Like for object's states updates, developers can register listeners also for object's
updates (p.e. on name update or object's connection/disconnection). The JSL library
provide listeners support even for local and cloud communication availability.
More info on [JSL's listeners](jsl_listeners.md).

Because the JSL is a library (then not executable as application), to improve the
development and testing process, a JSL Library within an interactive shell is
provided with different [shell commands](jsl_shellcmds.md) to manage the JOSP
service and interact with connected JOSP Objects. All JSL execution tasks
configured in Gradle's files, run the JSL library in interactive shell mode.

It's a software lib composed by:
* [Service Info](service_info.md)
* [Objects Manager](objects_manager.md)
* [Remote Object](remote_object.md)
* [Communication](communication.md)
* [JCP Client](jcpclient.md)

**Versions:**<br>
  * v. 2.0.0:
    initial version that require JCP APIs v. 2.0, JOSP GWs v. 2.0.0 and use
    JOSP Protocol v. 2.0.

[changelog](CHANGELOG.md) - [todos](TODOS.md)


## Gradle tasks

**Runner tasks:**<br>
  The JOSP JSL's running tasks execute the JSL library with the interactive shell:
  
  * ```./gradlew javaJSLRun```<br>
    this task start a JSL agent from a persistent working dir. This means that,
    each JSL execution preserve the latest execution state (p.e. user login).
    After the first git commit, this task's working dir is added to ```.gitignore```
    so any modification will not commit to the repository.<br>
    **NB:** not yet implemented.
  
  * ```./gradlew javaJSLVanillaRun```<br>
    at every execution, the task's working dir (and then his configs and data)
    are cleaned. So with this task is always possible execute a JSL service
    with default configs.<br>
    **NB:** not yet implemented.
  
  * ```./gradlew javaJSL{UC}Run```<br>
    like the ```javaJSLVanillaRun``` task but with pre-set configs:<br>

    | UC | User | Cloud auto-connect |
    |----|------|--------------------|
    | un | Unset | Yes |
    | uf | Unset | No  |
    | so | Set (Pinco) | Yes |
    | sf | Set (Pinco) | No  |
    
**Cleaner tasks:**<br>
  * ```./gradlew javaJSL_Clean```<br>
    delete working dir of ```javaJSLRun``` task.<br>
    **NB:** not yet implemented.

**Publish tasks:**<br>
  The JOSP JOD publication package and publish following files:
  
  | File | Content |
  |------|---------|
  | ```jsl-{VERSION}-java.jar``` | Jar file to include to JSL services |
  | ```jsl-{VERSION}-src.jar```  | Src package containing all sources used to compile the JOSP JSL library |
  | ```jsl-{VERSION}-doc.jar```  | Docs archive contains all Java docs from JSL's source code |
  | ```jsl-{VERSION}-deps.jar``` | Deps jar archive provide all dependencies required by the JOSP JSL library |
   
  * ```./gradlew jospJSL_PublishToLocal```<br>
    generate the publication artifacts and publish them to local maven repo.
   
  * ```./gradlew jospJSL_PublishToSonatype```<br>
    generate the publication artifacts and publish them to public Sonatype repo.


## Properties

*??jsl.yaml??:*
...

*??log4j.xml??:*
...


## Connections

**To JOSP JOD:**<br>
  [JOSP JOD - Connections](../jospJOD/README.md#Connections)

**To JCP APIs:**<br>
  [JCP APIs - Connections](../jcpAPIs/README.md#Connections)

**To JOSP GWs:**<br>
  [JOSP GWs - Connections](../jospGWs/README.md#Connections)