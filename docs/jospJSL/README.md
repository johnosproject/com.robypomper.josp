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

**Versions:**

* v. 2.0.0:
  initial version that require JCP APIs v. 2.0, JOSP GWs v. 2.0.0 and use
  JOSP Protocol v. 2.0.

[changelog](CHANGELOG.md) - [todos](TODOS.md) - [versions](../josp_versions.md#JOSP-JSL)


## Gradle tasks

### Runner tasks

The JOSP JSL's running tasks execute the JSL library with the interactive shell.
This JSL implementation is called JSL Shell and can be executed with following tasks:

---
```./gradlew javaJSLRun```

this task start a JSL library from a persistent working dir. This means that,
each JSL execution preserve the latest execution state (p.e. user login).
After the first git commit, this task's working dir is added to ```.gitignore```
so any modification will not commit to the repository.

---
```./gradlew javaJSLVanillaRun```
    
at every execution, the task's working dir (and then his configs and data)
are cleaned. So with this task is always possible execute a JSL service
with default configs.

---
```./gradlew javaJSL{UC}Run```
    
like the ```javaJSLVanillaRun``` task but with pre-set configs:

| UC | User | Cloud auto-connect |
|----|------|--------------------|
| un | Unset | Yes |
| uf | Unset | No  |
| so | Set (Pinco) | Yes |
| sf | Set (Pinco) | No  |

---
```./gradlew javaJSL{Disc}Run```

like the ```javaJSL{UC}Run``` tasks, but use different configs that sets
local discovery sub-systems. This tasks can be used to test different
ZeroConf implementations compatibilities and/or JSL services tolerance to
test Discovery/Network errors.

| Disc   | Discovery sub-system |
|--------|----------------------|
| Avahi  | Use the avahi damon installed on hosting Operating System |
| JmDNS  | Use the JmDNS implementation of the ZeroConfig protocol   |
| JmmDNS | Use the JmDNS implementation of the ZeroConfig protocol   |

---
For most used tasks ```javaJSLRun``` and ```javaJSLVanillaRun``` task modifiers
are available adding strings ```OnlyCloud```, ```OnlyLocal``` and ```NoComm```
to the tasks name ```javaJSL{Mod}Run``` and ```javaJSLVanilla{Mod}Run```.
Each modifier start a JSL Shell instance with configurations that disable the
cloud communication, or disable the local communication or disable both
communications.


### Cleaner tasks

```./gradlew javaJSL_Clean```

delete working dir of ```javaJSLRun``` task.
**NB:** not yet implemented ####.


### Publish tasks

The JOSP JOD publication package and publish following files:

| File | Content |
|------|---------|
| ```jsl-{VERSION}-java.jar``` | Jar file to include to JSL services |
| ```jsl-{VERSION}-src.jar```  | Src package containing all sources used to compile the JOSP JSL library |
| ```jsl-{VERSION}-doc.jar```  | Docs archive contains all Java docs from JSL's source code |
| ```jsl-{VERSION}-deps.jar``` | Deps jar archive provide all dependencies required by the JOSP JSL library |

---
```./gradlew jospJSL_PublishToLocal```

generate the publication artifacts and publish them to local maven repo.

---   
```./gradlew jospJSL_PublishToSonatype```

generate the publication artifacts and publish them to public Sonatype repo.


## Properties

*??jsl.yaml??:*
...

*??log4j.xml??:*
...


## Connections

### To JOSP JOD

[JOSP JOD - Connections](../jospJOD/README.md#Connections)


### To JCP APIs

[JCP APIs - Connections](../jcpAPIs/README.md#Connections)


### To JOSP GWs

[JOSP GWs - Connections](../jospGWs/README.md#Connections)
