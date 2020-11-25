package com.robypomper.josp.paths;

import com.robypomper.josp.info.JCPAPIsVersions;

public class APICloudStatus {
//@formatter:off

    // API info

    public static final String API_NAME = "CloudStatus";
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JcpAPI.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupStatus {
        public final static String NAME = "JCP APIs's Cloud Status APIs";
        public final static String DESCR = "";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_STATE                  = "";
    private static final String MTHD_STATE_JCPAPIS          = "state/jcpapi";
    private static final String MTHD_STATE_PROCESS          = "state/process";
    private static final String MTHD_STATE_JAVA             = "state/java";
    private static final String MTHD_STATE_JAVA_THREADS     = "state/java/threads";
    private static final String MTHD_STATE_OS               = "state/os";
    private static final String MTHD_STATE_CPU              = "state/cpu";
    private static final String MTHD_STATE_MEMORY           = "state/memory";
    private static final String MTHD_STATE_DISKS            = "state/disks";
    private static final String MTHD_STATE_NETWORK          = "state/network";
    private static final String MTHD_STATE_NETWORK_INTFS    = "state/network/intfs";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_STATE                  = API_PATH + "/" + API_VER + "/" + MTHD_STATE;
    public static final String FULL_PATH_STATE_JCPAPIS          = API_PATH + "/" + API_VER + "/" + MTHD_STATE_JCPAPIS;
    public static final String FULL_PATH_STATE_PROCESS          = API_PATH + "/" + API_VER + "/" + MTHD_STATE_PROCESS;
    public static final String FULL_PATH_STATE_JAVA             = API_PATH + "/" + API_VER + "/" + MTHD_STATE_JAVA;
    public static final String FULL_PATH_STATE_JAVA_THREADS     = API_PATH + "/" + API_VER + "/" + MTHD_STATE_JAVA_THREADS;
    public static final String FULL_PATH_STATE_OS               = API_PATH + "/" + API_VER + "/" + MTHD_STATE_OS;
    public static final String FULL_PATH_STATE_CPU              = API_PATH + "/" + API_VER + "/" + MTHD_STATE_CPU;
    public static final String FULL_PATH_STATE_MEMORY           = API_PATH + "/" + API_VER + "/" + MTHD_STATE_MEMORY;
    public static final String FULL_PATH_STATE_DISKS            = API_PATH + "/" + API_VER + "/" + MTHD_STATE_DISKS;
    public static final String FULL_PATH_STATE_NETWORK          = API_PATH + "/" + API_VER + "/" + MTHD_STATE_NETWORK;
    public static final String FULL_PATH_STATE_NETWORK_INTFS    = API_PATH + "/" + API_VER + "/" + MTHD_STATE_NETWORK_INTFS;

//@formatter:on
}
