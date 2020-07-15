# JOSP Commons

The **JOSP Commons** library includes the source code shared between JOSP's
components. The source code contained in this library provide different modules
such as:

* [Communication](communication.md), [SSL](communication_ssl.md)
* [Discovery](discovery.md)
* [Settings](settings.md)
* [Java](java.md)
* [Logs Markers](logs.md)
* [JCP Client](jcpclient.md), [OAuth2](jcpclient_oauth2.md)
* [JOSP Protocol](jospprotocol.md)

The JOSP Commons library is shared to other JOSP components inside this project
as Gradle's dependency. Actually it's required by the [JCP APIs](../jcpAPIs/README.md)
(Public) library, which in turn is required from [JOSP JOD](../jospJOD/README.md)
and [JOSP JSL](../jospJSL/README.md). Because the [JOSP GWs](../jospGWs/README.md)
are included in the JCP APIs, it includes the JOSP Commons library automatically.  

**Versions:**<br>
  * v. 2.0.0:
    initial version used by JOSP JOD v. 2.0.0, JOSP JSL v. 2.0.0, JCP APIs v. 2.0.0
    and JOSP GWs v. 2.0.0

[changelog](CHANGELOG.md) - [todos](TODOS.md)


## Gradle tasks

**Publish tasks:**<br>
  The JOSP Commons library can publish only "src" and "docs" packages. The binary
  package is not provided because the other JOSP components resolve the dependencies
  via JOSP Commons's sourceSet output.
   
  * ```./gradlew jospCommons_PublishToLocal```<br>
    generate the Java docs of the JOSP Commons source code, pack them in a jar
    archive, like the source code files; and publish them to local maven repo.
   
  * ```./gradlew jospCommons_PublishToSonatype```<br>
    generate the Java docs of the JOSP Commons source code, pack them in a jar
    archive, like the source code files; and publish them to public Sonatype repo.
