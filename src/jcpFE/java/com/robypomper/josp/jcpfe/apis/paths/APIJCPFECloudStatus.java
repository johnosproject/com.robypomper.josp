package com.robypomper.josp.jcpfe.apis.paths;

import com.robypomper.josp.jcpfe.info.JCPFEAPIsVersions;

public class APIJCPFECloudStatus {
//@formatter:off

    // API info

    public static final String API_NAME = "CloudStatus";
    public static final String API_VER = JCPFEAPIsVersions.VER_JCPFE_APIs_1_0;
    public static final String API_PATH = JcpFEAPI.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupState {
        public final static String NAME = "JCP FrontEnd's Cloud Status APIs";
        public final static String DESCR = "";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_STATE              = "";
    private static final String MTHD_STATE_JCPFE        = "state/jcpfe";
    private static final String MTHD_STATE_JCPFE_INST   = "state/jcpfe/instances";
    private static final String MTHD_STATE_PROCESS      = "state/process";
    private static final String MTHD_STATE_JAVA             = "state/java";
    private static final String MTHD_STATE_JAVA_THREADS     = "state/java/threads";
    private static final String MTHD_STATE_OS               = "state/os";
    private static final String MTHD_STATE_CPU              = "state/cpu";
    private static final String MTHD_STATE_MEMORY           = "state/memory";
    private static final String MTHD_STATE_DISKS            = "state/disks";
    private static final String MTHD_STATE_NETWORK          = "state/network";
    private static final String MTHD_STATE_NETWORK_INTFS    = "state/network/intfs";
    private static final String MTHD_STATE_JCPAPIS                  = "state/jcpapis";
    private static final String MTHD_STATE_JCPAPIS_PROCESS          = "state/jcpapis/process";
    private static final String MTHD_STATE_JCPAPIS_JAVA             = "state/jcpapis/java";
    private static final String MTHD_STATE_JCPAPIS_JAVA_THREADS     = "state/jcpapis/java/threads";
    private static final String MTHD_STATE_JCPAPIS_OS               = "state/jcpapis/os";
    private static final String MTHD_STATE_JCPAPIS_CPU              = "state/jcpapis/cpu";
    private static final String MTHD_STATE_JCPAPIS_MEMORY           = "state/jcpapis/memory";
    private static final String MTHD_STATE_JCPAPIS_DISKS            = "state/jcpapis/disks";
    private static final String MTHD_STATE_JCPAPIS_NETWORK          = "state/jcpapis/network";
    private static final String MTHD_STATE_JCPAPIS_NETWORK_INTFS    = "state/jcpapis/network/intfs";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_STATE                  = API_PATH + "/" + API_VER + "/" + MTHD_STATE;
    public static final String FULL_PATH_STATE_JCPFE            = API_PATH + "/" + API_VER + "/" + MTHD_STATE_JCPFE;
    public static final String FULL_PATH_STATE_JCPFE_INST       = API_PATH + "/" + API_VER + "/" + MTHD_STATE_JCPFE_INST;
    public static final String FULL_PATH_STATE_PROCESS          = API_PATH + "/" + API_VER + "/" + MTHD_STATE_PROCESS;
    public static final String FULL_PATH_STATE_JAVA             = API_PATH + "/" + API_VER + "/" + MTHD_STATE_JAVA;
    public static final String FULL_PATH_STATE_JAVA_THREADS     = API_PATH + "/" + API_VER + "/" + MTHD_STATE_JAVA_THREADS;
    public static final String FULL_PATH_STATE_OS               = API_PATH + "/" + API_VER + "/" + MTHD_STATE_OS;
    public static final String FULL_PATH_STATE_CPU              = API_PATH + "/" + API_VER + "/" + MTHD_STATE_CPU;
    public static final String FULL_PATH_STATE_MEMORY           = API_PATH + "/" + API_VER + "/" + MTHD_STATE_MEMORY;
    public static final String FULL_PATH_STATE_DISKS            = API_PATH + "/" + API_VER + "/" + MTHD_STATE_DISKS;
    public static final String FULL_PATH_STATE_NETWORK          = API_PATH + "/" + API_VER + "/" + MTHD_STATE_NETWORK;
    public static final String FULL_PATH_STATE_NETWORK_INTFS    = API_PATH + "/" + API_VER + "/" + MTHD_STATE_NETWORK_INTFS;
    public static final String FULL_PATH_STATE_JCPAPIS                  = API_PATH + "/" + API_VER + "/" + MTHD_STATE_JCPAPIS;
    public static final String FULL_PATH_STATE_JCPAPIS_PROCESS          = API_PATH + "/" + API_VER + "/" + MTHD_STATE_JCPAPIS_PROCESS;
    public static final String FULL_PATH_STATE_JCPAPIS_JAVA             = API_PATH + "/" + API_VER + "/" + MTHD_STATE_JCPAPIS_JAVA;
    public static final String FULL_PATH_STATE_JCPAPIS_JAVA_THREADS     = API_PATH + "/" + API_VER + "/" + MTHD_STATE_JCPAPIS_JAVA_THREADS;
    public static final String FULL_PATH_STATE_JCPAPIS_OS               = API_PATH + "/" + API_VER + "/" + MTHD_STATE_JCPAPIS_OS;
    public static final String FULL_PATH_STATE_JCPAPIS_CPU              = API_PATH + "/" + API_VER + "/" + MTHD_STATE_JCPAPIS_CPU;
    public static final String FULL_PATH_STATE_JCPAPIS_MEMORY           = API_PATH + "/" + API_VER + "/" + MTHD_STATE_JCPAPIS_MEMORY;
    public static final String FULL_PATH_STATE_JCPAPIS_DISKS            = API_PATH + "/" + API_VER + "/" + MTHD_STATE_JCPAPIS_DISKS;
    public static final String FULL_PATH_STATE_JCPAPIS_NETWORK          = API_PATH + "/" + API_VER + "/" + MTHD_STATE_JCPAPIS_NETWORK;
    public static final String FULL_PATH_STATE_JCPAPIS_NETWORK_INTFS    = API_PATH + "/" + API_VER + "/" + MTHD_STATE_JCPAPIS_NETWORK_INTFS;

//@formatter:on
}
