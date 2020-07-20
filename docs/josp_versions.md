# JOSP Components Versions

TODO

Versions:
- JOSP Protocol
- JCP APIs (Methods)
- JOSP JOD
- JOSP JSL
- JCP APIs (SpringApp)
- JOSP Gateways (SpringApp)



### JOSP Protocol:
  defined in
  - src/jospCommons/java/com/robypomper/josp/protocol/JOSPProtocol.java
    public static final String JOSP_PROTO_VERSION = JOSP_PROTO_VERSION_2_0;
  based on
  - src/jospCommons/java/com/robypomper/josp/protocol/JOSPProtocol.java
    protected static final String JOSP_PROTO = "JOSP/" + JOSP_PROTO_VERSION;
  internally used by
  - JOSPProtocol internal (protocol's string constant)
  - JOD_002 and JSL_002 as JOSP Protocol supported versions
  version compatibility used by
  - JOSP JSL when connect as client to JOSP GWs
  - JOSP JOD when connect as client to JOSP GWs
  - JOSP JSL when connect as client to JOSP JOD
  increasing strategy
    ???
  note
    used by himself (JOSPProtocol) and by JOD_002 and JSL_002
  
### JCP APIs (Method's paths):
  defined in
  - src/jcpAPIsPublic/java/com/robypomper/josp/jcp/info/JCPAPIsVersions.java  definition class
    public static final String VER_JCP_APIs_2_0 = "2.0";
  based on
  - src/jcpAPIsPublic/java/com/robypomper/josp/jcp/apis/paths/**  definition classes
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
  internally used by
  - src/jcpAPIs/java/com/robypomper/josp/jcp/apis/jcp     controllers
  - src/jcpAPIs/java/com/robypomper/josp/jcp/docs/SwaggerConfigurer   configurer
  version compatibility used by
  - JOSP JSL when send requests as client to JCP APIs
  - JOSP JOD when send requests as client to JCP APIs
  increasing strategy
    ???

### JOSP JOD:
  defined in
  - src/jospJOD/java/com/robypomper/josp/jod/FactoryJOD.java
    public static final String JOD_VER_2_0_0 = "2.0.0";
  based on
  - src/jospJOD/java/com/robypomper/josp/jod/JOD_002.java
    private static final String VERSION = FactoryJOD.JOD_VER_2_0_0;   // Upgraded to 2.0.0
  internally used by
  - JOD_002 internal (startup log printer)
  - JODObjectInfo => communication/info msg
  version compatibility used by
  - JOSP JSL when connect as local client to JOD
  increase strategy
    ???
  notes
    Read/Set during JOD initialization by FactoryJOD class, influence the AbsJOD implementation choosed to instantiate the JOD object.

### JOSP JSL:
  defined in
  - src/jospJSL/java/com/robypomper/josp/jsl/FactoryJSL.java
    public static final String JSL_VER_2_0_0 = "2.0.0";
  based on
  - src/jospJSL/java/com/robypomper/josp/jsl/JSL_002.java
    private static final String VERSION = FactoryJSL.JSL_VER_2_0_0;   // Upgraded to 2.0.0
  internally used by
  - JSL_002 internal (startup log printer)
  - JSLObjectInfo => communication/info msg
  version compatibility used by
  - N/A
  increase strategy
    ???
  notes
    Read/Set during JSL initialization by FactoryJSL class, influence the AbsJSL implementation choosed to instantiate the JSL object.


### JCP APIs (SpringApp)
  defined in
  - .
    .
  based on
  - .
    .
  internally used by
  - .
  - .
  version compatibility used by
  - .
  increasing strategy
    ???
  note
    ...
  
  

### JOSP Gateways (SpringApp)
  defined in
  - .
    .
  based on
  - .
    .
  internally used by
  - .
  - .
  version compatibility used by
  - .
  increasing strategy
    ???
  note
    ...