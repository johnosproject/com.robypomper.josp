package com.robypomper.josp.paths.jcp;

import com.robypomper.josp.info.JCPAPIsVersions;

public class JCPStatusAbs {
//@formatter:off

    // API info

    public static final String API_NAME = "JCP/Status";
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JCPAPIsVersions.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups


    // API's methods

    // '{mthdName}
    private static final String MTHD_STATUS_INSTANCE            = "instance";
    private static final String MTHD_STATUS_ONLINE              = "online";
    private static final String MTHD_STATUS_PROCESS             = "process";
    private static final String MTHD_STATUS_JAVA                = "java";
    private static final String MTHD_STATUS_JAVA_THS            = "java/threads";
    private static final String MTHD_STATUS_OS                  = "os";
    private static final String MTHD_STATUS_CPU                 = "cpu";
    private static final String MTHD_STATUS_MEMORY              = "memory";
    private static final String MTHD_STATUS_DISKS               = "disks";
    private static final String MTHD_STATUS_NETWORK             = "network";
    private static final String MTHD_STATUS_NETWORK_INTFS       = "network/intfs";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_STATUS_INSTANCE            = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_INSTANCE;
    public static final String FULL_PATH_STATUS_ONLINE              = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_ONLINE;
    public static final String FULL_PATH_STATUS_PROCESS             = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_PROCESS;
    public static final String FULL_PATH_STATUS_JAVA                = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_JAVA;
    public static final String FULL_PATH_STATUS_JAVA_THS            = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_JAVA_THS;
    public static final String FULL_PATH_STATUS_OS                  = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_OS;
    public static final String FULL_PATH_STATUS_CPU                 = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_CPU;
    public static final String FULL_PATH_STATUS_MEMORY              = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_MEMORY;
    public static final String FULL_PATH_STATUS_DISKS               = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_DISKS;
    public static final String FULL_PATH_STATUS_NETWORK             = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_NETWORK;
    public static final String FULL_PATH_STATUS_NETWORK_INTFS       = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_NETWORK_INTFS;


    // API's descriptions

    public static final String DESCR_PATH_STATUS_INSTANCE       = "Return current JCP Service instance's status";
    public static final String DESCR_PATH_STATUS_ONLINE         = "Return current JCP Service executable's ONLINE status";
    public static final String DESCR_PATH_STATUS_PROCESS        = "Return current JCP Service executable's PROCESS status";
    public static final String DESCR_PATH_STATUS_JAVA           = "Return current JCP Service executable's JAVA status";
    public static final String DESCR_PATH_STATUS_JAVA_THS       = "Return current JCP Service executable's JAVA_THS status";
    public static final String DESCR_PATH_STATUS_OS             = "Return current JCP Service executable's OS status";
    public static final String DESCR_PATH_STATUS_CPU            = "Return current JCP Service executable's CPU status";
    public static final String DESCR_PATH_STATUS_MEMORY         = "Return current JCP Service executable's MEMORY status";
    public static final String DESCR_PATH_STATUS_DISKS          = "Return current JCP Service executable's DISKS status";
    public static final String DESCR_PATH_STATUS_NETWORK        = "Return current JCP Service executable's NETWORK status";
    public static final String DESCR_PATH_STATUS_NETWORK_INTFS  = "Return current JCP Service executable's NETWORK_INTFS status";

//@formatter:on
}
