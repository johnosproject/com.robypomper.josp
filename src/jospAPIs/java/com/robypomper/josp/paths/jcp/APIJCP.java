package com.robypomper.josp.paths.jcp;

import com.robypomper.josp.info.JCPAPIsVersions;

public class APIJCP {
//@formatter:off

    // API info

    public static final String API_NAME = "JCP";
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JCPAPIsVersions.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupStatus {
        public final static String NAME = "JCP Service Status APIs";
        public final static String DESCR = "";
    }

    public static class SubGroupAPIsStatus {
        public final static String NAME = "JCP APIs Status APIs";
        public final static String DESCR = "";
    }

    public static class SubGroupGWsStatus {
        public final static String NAME = "JCP GWs Status APIs";
        public final static String DESCR = "";
    }

    public static class SubGroupJSLWebBridgeStatus {
        public final static String NAME = "JCP JSL Web Bridge Status APIs";
        public final static String DESCR = "";
    }

    public static class SubGroupFEStatus {
        public final static String NAME = "JCP FE Status APIs";
        public final static String DESCR = "";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_STATUS                  = "status";
    private static final String MTHD_STATUS_PROCESS          = "status/process";
    private static final String MTHD_STATUS_JAVA             = "status/java";
    private static final String MTHD_STATUS_JAVA_THREADS     = "status/java/threads";
    private static final String MTHD_STATUS_OS               = "status/os";
    private static final String MTHD_STATUS_CPU              = "status/cpu";
    private static final String MTHD_STATUS_MEMORY           = "status/memory";
    private static final String MTHD_STATUS_DISKS            = "status/disks";
    private static final String MTHD_STATUS_NETWORK          = "status/network";
    private static final String MTHD_STATUS_NETWORK_INTFS    = "status/network/intfs";

    private static final String MTHD_APIS_STATUS        = "apis/status";
    private static final String MTHD_APIS_STATUS_OBJS   = "apis/status/objs";
    private static final String MTHD_APIS_STATUS_SRVS   = "apis/status/srvs";
    private static final String MTHD_APIS_STATUS_GWS    = "apis/status/gws";
    private static final String MTHD_APIS_STATUS_GWS_CLI= "apis/status/gws/clients";
    private static final String MTHD_APIS_STATUS_USRS   = "apis/status/usrs";
    private static final String MTHD_GWS_STATUS         = "gws/status";
    private static final String MTHD_GWS_STATUS_CLI     = "gws/status/clients";
    private static final String MTHD_JSLWB_STATUS       = "jslwb/status";
    private static final String MTHD_FE_STATUS          = "fe/status";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_STATUS                  = API_PATH + "/" + API_VER + "/" + MTHD_STATUS;
    public static final String FULL_PATH_STATUS_PROCESS          = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_PROCESS;
    public static final String FULL_PATH_STATUS_JAVA             = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_JAVA;
    public static final String FULL_PATH_STATUS_JAVA_THREADS     = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_JAVA_THREADS;
    public static final String FULL_PATH_STATUS_OS               = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_OS;
    public static final String FULL_PATH_STATUS_CPU              = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_CPU;
    public static final String FULL_PATH_STATUS_MEMORY           = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_MEMORY;
    public static final String FULL_PATH_STATUS_DISKS            = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_DISKS;
    public static final String FULL_PATH_STATUS_NETWORK          = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_NETWORK;
    public static final String FULL_PATH_STATUS_NETWORK_INTFS    = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_NETWORK_INTFS;

    public static final String FULL_PATH_APIS_STATUS        = API_PATH + "/" + API_VER + "/" + MTHD_APIS_STATUS;
    public static final String FULL_PATH_APIS_STATUS_OBJS   = API_PATH + "/" + API_VER + "/" + MTHD_APIS_STATUS_OBJS;
    public static final String FULL_PATH_APIS_STATUS_SRVS   = API_PATH + "/" + API_VER + "/" + MTHD_APIS_STATUS_SRVS;
    public static final String FULL_PATH_APIS_STATUS_GWS    = API_PATH + "/" + API_VER + "/" + MTHD_APIS_STATUS_GWS;
    public static final String FULL_PATH_APIS_STATUS_GWS_CLI= API_PATH + "/" + API_VER + "/" + MTHD_APIS_STATUS_GWS_CLI;
    public static final String FULL_PATH_APIS_STATUS_USRS   = API_PATH + "/" + API_VER + "/" + MTHD_APIS_STATUS_USRS;
    public static final String FULL_PATH_GWS_STATUS         = API_PATH + "/" + API_VER + "/" + MTHD_GWS_STATUS;
    public static final String FULL_PATH_GWS_STATUS_CLI     = API_PATH + "/" + API_VER + "/" + MTHD_GWS_STATUS_CLI;
    public static final String FULL_PATH_JSLWB_STATUS       = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS;
    public static final String FULL_PATH_FE_STATUS          = API_PATH + "/" + API_VER + "/" + MTHD_FE_STATUS;

//@formatter:on
}
