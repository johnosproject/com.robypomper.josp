package com.robypomper.josp.jcp.paths.jslwb;

import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.paths.APIAdmin;

public class APIJSLWBAdmin {
//@formatter:off

    // API info

    public static final String API_NAME = "JSL/Admin";
    public static final String API_VER = JCPJSLWBVersions.VER_JCPJSLWB_APIs_1_0;
    public static final String API_PATH = JCPJSLWBVersions.PATH_JSLWB_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupAdmin {
        public final static String NAME = "JSL Admin";
        public final static String DESCR = "Methods to manage JCP as Admin";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_JSLWB_STATUS_APIS          = APIAdmin.MTHD_JCP_APIS_STATUS_INSTANCE;
    private static final String MTHD_JSLWB_STATUS_APIS_OBJS     = APIAdmin.MTHD_JCP_APIS_STATUS_OBJS;
    private static final String MTHD_JSLWB_STATUS_APIS_SRVS     = APIAdmin.MTHD_JCP_APIS_STATUS_SRVS;
    private static final String MTHD_JSLWB_STATUS_APIS_USRS     = APIAdmin.MTHD_JCP_APIS_STATUS_USRS;
    private static final String MTHD_JSLWB_STATUS_APIS_EXEC_ONLINE          = APIAdmin.MTHD_JCP_APIS_STATUS_EXEC_ONLINE;
    private static final String MTHD_JSLWB_STATUS_APIS_EXEC_CPU             = APIAdmin.MTHD_JCP_APIS_STATUS_EXEC_CPU;
    private static final String MTHD_JSLWB_STATUS_APIS_EXEC_DISKS           = APIAdmin.MTHD_JCP_APIS_STATUS_EXEC_DISKS;
    private static final String MTHD_JSLWB_STATUS_APIS_EXEC_JAVA            = APIAdmin.MTHD_JCP_APIS_STATUS_EXEC_JAVA;
    private static final String MTHD_JSLWB_STATUS_APIS_EXEC_JAVA_THS        = APIAdmin.MTHD_JCP_APIS_STATUS_EXEC_JAVA_THS;
    private static final String MTHD_JSLWB_STATUS_APIS_EXEC_MEMORY          = APIAdmin.MTHD_JCP_APIS_STATUS_EXEC_MEMORY;
    private static final String MTHD_JSLWB_STATUS_APIS_EXEC_NETWORK         = APIAdmin.MTHD_JCP_APIS_STATUS_EXEC_NETWORK;
    private static final String MTHD_JSLWB_STATUS_APIS_EXEC_NETWORK_INTFS   = APIAdmin.MTHD_JCP_APIS_STATUS_EXEC_NETWORK_INTFS;
    private static final String MTHD_JSLWB_STATUS_APIS_EXEC_OS              = APIAdmin.MTHD_JCP_APIS_STATUS_EXEC_OS;
    private static final String MTHD_JSLWB_STATUS_APIS_EXEC_PROCESS         = APIAdmin.MTHD_JCP_APIS_STATUS_EXEC_PROCESS;
    private static final String MTHD_JSLWB_STATUS_GWS           = APIAdmin.MTHD_JCP_GWS_STATUS_INSTANCE;
    private static final String MTHD_JSLWB_STATUS_GWS_CLI       = APIAdmin.MTHD_JCP_GWS_STATUS_SERVERS;
    private static final String MTHD_JSLWB_STATUS_GWS_EXEC_ONLINE           = APIAdmin.MTHD_JCP_GWS_STATUS_EXEC_ONLINE;
    private static final String MTHD_JSLWB_STATUS_GWS_EXEC_CPU              = APIAdmin.MTHD_JCP_GWS_STATUS_EXEC_CPU;
    private static final String MTHD_JSLWB_STATUS_GWS_EXEC_DISKS            = APIAdmin.MTHD_JCP_GWS_STATUS_EXEC_DISKS;
    private static final String MTHD_JSLWB_STATUS_GWS_EXEC_JAVA             = APIAdmin.MTHD_JCP_GWS_STATUS_EXEC_JAVA;
    private static final String MTHD_JSLWB_STATUS_GWS_EXEC_JAVA_THS         = APIAdmin.MTHD_JCP_GWS_STATUS_EXEC_JAVA_THS;
    private static final String MTHD_JSLWB_STATUS_GWS_EXEC_MEMORY           = APIAdmin.MTHD_JCP_GWS_STATUS_EXEC_MEMORY;
    private static final String MTHD_JSLWB_STATUS_GWS_EXEC_NETWORK          = APIAdmin.MTHD_JCP_GWS_STATUS_EXEC_NETWORK;
    private static final String MTHD_JSLWB_STATUS_GWS_EXEC_NETWORK_INTFS    = APIAdmin.MTHD_JCP_GWS_STATUS_EXEC_NETWORK_INTFS;
    private static final String MTHD_JSLWB_STATUS_GWS_EXEC_OS               = APIAdmin.MTHD_JCP_GWS_STATUS_EXEC_OS;
    private static final String MTHD_JSLWB_STATUS_GWS_EXEC_PROCESS          = APIAdmin.MTHD_JCP_GWS_STATUS_EXEC_PROCESS;
    private static final String MTHD_JSLWB_STATUS_JSL_WB        = APIAdmin.MTHD_JCP_JSL_WB_STATUS_INSTANCE;
    private static final String MTHD_JSLWB_STATUS_JSL_WB_EXEC_ONLINE        = APIAdmin.MTHD_JCP_JSL_WB_STATUS_EXEC_ONLINE;
    private static final String MTHD_JSLWB_STATUS_JSL_WB_EXEC_CPU           = APIAdmin.MTHD_JCP_JSL_WB_STATUS_EXEC_CPU;
    private static final String MTHD_JSLWB_STATUS_JSL_WB_EXEC_DISKS         = APIAdmin.MTHD_JCP_JSL_WB_STATUS_EXEC_DISKS;
    private static final String MTHD_JSLWB_STATUS_JSL_WB_EXEC_JAVA          = APIAdmin.MTHD_JCP_JSL_WB_STATUS_EXEC_JAVA;
    private static final String MTHD_JSLWB_STATUS_JSL_WB_EXEC_JAVA_THS      = APIAdmin.MTHD_JCP_JSL_WB_STATUS_EXEC_JAVA_THS;
    private static final String MTHD_JSLWB_STATUS_JSL_WB_EXEC_MEMORY        = APIAdmin.MTHD_JCP_JSL_WB_STATUS_EXEC_MEMORY;
    private static final String MTHD_JSLWB_STATUS_JSL_WB_EXEC_NETWORK       = APIAdmin.MTHD_JCP_JSL_WB_STATUS_EXEC_NETWORK;
    private static final String MTHD_JSLWB_STATUS_JSL_WB_EXEC_NETWORK_INTFS = APIAdmin.MTHD_JCP_JSL_WB_STATUS_EXEC_NETWORK_INTFS;
    private static final String MTHD_JSLWB_STATUS_JSL_WB_EXEC_OS            = APIAdmin.MTHD_JCP_JSL_WB_STATUS_EXEC_OS;
    private static final String MTHD_JSLWB_STATUS_JSL_WB_EXEC_PROCESS       = APIAdmin.MTHD_JCP_JSL_WB_STATUS_EXEC_PROCESS;
    private static final String MTHD_JSLWB_STATUS_FE            = APIAdmin.MTHD_JCP_FE_STATUS_INSTANCE;
    private static final String MTHD_JSLWB_STATUS_FE_EXEC_ONLINE            = APIAdmin.MTHD_JCP_FE_STATUS_EXEC_ONLINE;
    private static final String MTHD_JSLWB_STATUS_FE_EXEC_CPU               = APIAdmin.MTHD_JCP_FE_STATUS_EXEC_CPU;
    private static final String MTHD_JSLWB_STATUS_FE_EXEC_DISKS             = APIAdmin.MTHD_JCP_FE_STATUS_EXEC_DISKS;
    private static final String MTHD_JSLWB_STATUS_FE_EXEC_JAVA              = APIAdmin.MTHD_JCP_FE_STATUS_EXEC_JAVA;
    private static final String MTHD_JSLWB_STATUS_FE_EXEC_JAVA_THS          = APIAdmin.MTHD_JCP_FE_STATUS_EXEC_JAVA_THS;
    private static final String MTHD_JSLWB_STATUS_FE_EXEC_MEMORY            = APIAdmin.MTHD_JCP_FE_STATUS_EXEC_MEMORY;
    private static final String MTHD_JSLWB_STATUS_FE_EXEC_NETWORK           = APIAdmin.MTHD_JCP_FE_STATUS_EXEC_NETWORK;
    private static final String MTHD_JSLWB_STATUS_FE_EXEC_NETWORK_INTFS     = APIAdmin.MTHD_JCP_FE_STATUS_EXEC_NETWORK_INTFS;
    private static final String MTHD_JSLWB_STATUS_FE_EXEC_OS                = APIAdmin.MTHD_JCP_FE_STATUS_EXEC_OS;
    private static final String MTHD_JSLWB_STATUS_FE_EXEC_PROCESS           = APIAdmin.MTHD_JCP_FE_STATUS_EXEC_PROCESS;

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_JSLWB_STATUS_APIS          = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_APIS;
    public static final String FULL_PATH_JSLWB_STATUS_APIS_OBJS     = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_APIS_OBJS;
    public static final String FULL_PATH_JSLWB_STATUS_APIS_SRVS     = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_APIS_SRVS;
    public static final String FULL_PATH_JSLWB_STATUS_APIS_USRS     = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_APIS_USRS;
    public static final String FULL_PATH_JSLWB_STATUS_APIS_EXEC_ONLINE          = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_APIS_EXEC_ONLINE;
    public static final String FULL_PATH_JSLWB_STATUS_APIS_EXEC_CPU             = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_APIS_EXEC_CPU;
    public static final String FULL_PATH_JSLWB_STATUS_APIS_EXEC_DISKS           = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_APIS_EXEC_DISKS;
    public static final String FULL_PATH_JSLWB_STATUS_APIS_EXEC_JAVA            = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_APIS_EXEC_JAVA;
    public static final String FULL_PATH_JSLWB_STATUS_APIS_EXEC_JAVA_THS        = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_APIS_EXEC_JAVA_THS;
    public static final String FULL_PATH_JSLWB_STATUS_APIS_EXEC_MEMORY          = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_APIS_EXEC_MEMORY;
    public static final String FULL_PATH_JSLWB_STATUS_APIS_EXEC_NETWORK         = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_APIS_EXEC_NETWORK;
    public static final String FULL_PATH_JSLWB_STATUS_APIS_EXEC_NETWORK_INTFS   = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_APIS_EXEC_NETWORK_INTFS;
    public static final String FULL_PATH_JSLWB_STATUS_APIS_EXEC_OS              = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_APIS_EXEC_OS;
    public static final String FULL_PATH_JSLWB_STATUS_APIS_EXEC_PROCESS         = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_APIS_EXEC_PROCESS;
    public static final String FULL_PATH_JSLWB_STATUS_GWS           = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_GWS;
    public static final String FULL_PATH_JSLWB_STATUS_GWS_CLI       = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_GWS_CLI;
    public static final String FULL_PATH_JSLWB_STATUS_GWS_EXEC_ONLINE           = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_GWS_EXEC_ONLINE;
    public static final String FULL_PATH_JSLWB_STATUS_GWS_EXEC_CPU              = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_GWS_EXEC_CPU;
    public static final String FULL_PATH_JSLWB_STATUS_GWS_EXEC_DISKS            = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_GWS_EXEC_DISKS;
    public static final String FULL_PATH_JSLWB_STATUS_GWS_EXEC_JAVA             = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_GWS_EXEC_JAVA;
    public static final String FULL_PATH_JSLWB_STATUS_GWS_EXEC_JAVA_THS         = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_GWS_EXEC_JAVA_THS;
    public static final String FULL_PATH_JSLWB_STATUS_GWS_EXEC_MEMORY           = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_GWS_EXEC_MEMORY;
    public static final String FULL_PATH_JSLWB_STATUS_GWS_EXEC_NETWORK          = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_GWS_EXEC_NETWORK;
    public static final String FULL_PATH_JSLWB_STATUS_GWS_EXEC_NETWORK_INTFS    = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_GWS_EXEC_NETWORK_INTFS;
    public static final String FULL_PATH_JSLWB_STATUS_GWS_EXEC_OS               = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_GWS_EXEC_OS;
    public static final String FULL_PATH_JSLWB_STATUS_GWS_EXEC_PROCESS          = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_GWS_EXEC_PROCESS;
    public static final String FULL_PATH_JSLWB_STATUS_JSL_WB        = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_JSL_WB;
    public static final String FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_ONLINE        = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_JSL_WB_EXEC_ONLINE;
    public static final String FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_CPU           = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_JSL_WB_EXEC_CPU;
    public static final String FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_DISKS         = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_JSL_WB_EXEC_DISKS;
    public static final String FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_JAVA          = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_JSL_WB_EXEC_JAVA;
    public static final String FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_JAVA_THS      = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_JSL_WB_EXEC_JAVA_THS;
    public static final String FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_MEMORY        = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_JSL_WB_EXEC_MEMORY;
    public static final String FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_NETWORK       = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_JSL_WB_EXEC_NETWORK;
    public static final String FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_NETWORK_INTFS = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_JSL_WB_EXEC_NETWORK_INTFS;
    public static final String FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_OS            = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_JSL_WB_EXEC_OS;
    public static final String FULL_PATH_JSLWB_STATUS_JSL_WB_EXEC_PROCESS       = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_JSL_WB_EXEC_PROCESS;
    public static final String FULL_PATH_JSLWB_STATUS_FE            = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_FE;
    public static final String FULL_PATH_JSLWB_STATUS_FE_EXEC_ONLINE            = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_FE_EXEC_ONLINE;
    public static final String FULL_PATH_JSLWB_STATUS_FE_EXEC_CPU               = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_FE_EXEC_CPU;
    public static final String FULL_PATH_JSLWB_STATUS_FE_EXEC_DISKS             = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_FE_EXEC_DISKS;
    public static final String FULL_PATH_JSLWB_STATUS_FE_EXEC_JAVA              = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_FE_EXEC_JAVA;
    public static final String FULL_PATH_JSLWB_STATUS_FE_EXEC_JAVA_THS          = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_FE_EXEC_JAVA_THS;
    public static final String FULL_PATH_JSLWB_STATUS_FE_EXEC_MEMORY            = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_FE_EXEC_MEMORY;
    public static final String FULL_PATH_JSLWB_STATUS_FE_EXEC_NETWORK           = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_FE_EXEC_NETWORK;
    public static final String FULL_PATH_JSLWB_STATUS_FE_EXEC_NETWORK_INTFS     = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_FE_EXEC_NETWORK_INTFS;
    public static final String FULL_PATH_JSLWB_STATUS_FE_EXEC_OS                = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_FE_EXEC_OS;
    public static final String FULL_PATH_JSLWB_STATUS_FE_EXEC_PROCESS           = API_PATH + "/" + API_VER + "/" + MTHD_JSLWB_STATUS_FE_EXEC_PROCESS;


    // API's descriptions

    public static final String DESCR_PATH_JSLWB_STATUS_APIS         = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_INSTANCE;
    public static final String DESCR_PATH_JSLWB_STATUS_APIS_OBJS    = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_OBJS;
    public static final String DESCR_PATH_JSLWB_STATUS_APIS_SRVS    = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_SRVS;
    public static final String DESCR_PATH_JSLWB_STATUS_APIS_USRS    = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_USRS;
    public static final String DESCR_PATH_JSLWB_STATUS_APIS_EXEC_ONLINE             = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_ONLINE;
    public static final String DESCR_PATH_JSLWB_STATUS_APIS_EXEC_CPU                = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_CPU;
    public static final String DESCR_PATH_JSLWB_STATUS_APIS_EXEC_DISKS              = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_DISKS;
    public static final String DESCR_PATH_JSLWB_STATUS_APIS_EXEC_JAVA               = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_JAVA;
    public static final String DESCR_PATH_JSLWB_STATUS_APIS_EXEC_JAVA_THS           = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_JAVA_THS;
    public static final String DESCR_PATH_JSLWB_STATUS_APIS_EXEC_MEMORY             = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_MEMORY;
    public static final String DESCR_PATH_JSLWB_STATUS_APIS_EXEC_NETWORK            = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_NETWORK;
    public static final String DESCR_PATH_JSLWB_STATUS_APIS_EXEC_NETWORK_INTFS      = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_NETWORK_INTFS;
    public static final String DESCR_PATH_JSLWB_STATUS_APIS_EXEC_OS                 = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_OS;
    public static final String DESCR_PATH_JSLWB_STATUS_APIS_EXEC_PROCESS            = APIAdmin.DESCR_PATH_JCP_APIS_STATUS_EXEC_PROCESS;
    public static final String DESCR_PATH_JSLWB_STATUS_GWS          = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_INSTANCE;
    public static final String DESCR_PATH_JSLWB_STATUS_GWS_CLI      = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_SERVERS;
    public static final String DESCR_PATH_JSLWB_STATUS_GWS_EXEC_ONLINE              = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_ONLINE;
    public static final String DESCR_PATH_JSLWB_STATUS_GWS_EXEC_CPU                 = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_CPU;
    public static final String DESCR_PATH_JSLWB_STATUS_GWS_EXEC_DISKS               = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_DISKS;
    public static final String DESCR_PATH_JSLWB_STATUS_GWS_EXEC_JAVA                = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_JAVA;
    public static final String DESCR_PATH_JSLWB_STATUS_GWS_EXEC_JAVA_THS            = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_JAVA_THS;
    public static final String DESCR_PATH_JSLWB_STATUS_GWS_EXEC_MEMORY              = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_MEMORY;
    public static final String DESCR_PATH_JSLWB_STATUS_GWS_EXEC_NETWORK             = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_NETWORK;
    public static final String DESCR_PATH_JSLWB_STATUS_GWS_EXEC_NETWORK_INTFS       = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_NETWORK_INTFS;
    public static final String DESCR_PATH_JSLWB_STATUS_GWS_EXEC_OS                  = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_OS;
    public static final String DESCR_PATH_JSLWB_STATUS_GWS_EXEC_PROCESS             = APIAdmin.DESCR_PATH_JCP_GWS_STATUS_EXEC_PROCESS;
    public static final String DESCR_PATH_JSLWB_STATUS_JSL_WB        = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_INSTANCE;
    public static final String DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_ONLINE           = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_ONLINE;
    public static final String DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_CPU              = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_CPU;
    public static final String DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_DISKS            = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_DISKS;
    public static final String DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_JAVA             = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_JAVA;
    public static final String DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_JAVA_THS         = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_JAVA_THS;
    public static final String DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_MEMORY           = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_MEMORY;
    public static final String DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_NETWORK          = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_NETWORK;
    public static final String DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_NETWORK_INTFS    = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_NETWORK_INTFS;
    public static final String DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_OS               = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_OS;
    public static final String DESCR_PATH_JSLWB_STATUS_JSL_WB_EXEC_PROCESS          = APIAdmin.DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_PROCESS;
    public static final String DESCR_PATH_JSLWB_STATUS_FE           = APIAdmin.DESCR_PATH_JCP_FE_STATUS_INSTANCE;
    public static final String DESCR_PATH_JSLWB_STATUS_FE_EXEC_ONLINE               = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_ONLINE;
    public static final String DESCR_PATH_JSLWB_STATUS_FE_EXEC_CPU                  = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_CPU;
    public static final String DESCR_PATH_JSLWB_STATUS_FE_EXEC_DISKS                = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_DISKS;
    public static final String DESCR_PATH_JSLWB_STATUS_FE_EXEC_JAVA                 = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_JAVA;
    public static final String DESCR_PATH_JSLWB_STATUS_FE_EXEC_JAVA_THS             = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_JAVA_THS;
    public static final String DESCR_PATH_JSLWB_STATUS_FE_EXEC_MEMORY               = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_MEMORY;
    public static final String DESCR_PATH_JSLWB_STATUS_FE_EXEC_NETWORK              = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_NETWORK;
    public static final String DESCR_PATH_JSLWB_STATUS_FE_EXEC_NETWORK_INTFS        = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_NETWORK_INTFS;
    public static final String DESCR_PATH_JSLWB_STATUS_FE_EXEC_OS                   = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_OS;
    public static final String DESCR_PATH_JSLWB_STATUS_FE_EXEC_PROCESS              = APIAdmin.DESCR_PATH_JCP_FE_STATUS_EXEC_PROCESS;

//@formatter:on
}
