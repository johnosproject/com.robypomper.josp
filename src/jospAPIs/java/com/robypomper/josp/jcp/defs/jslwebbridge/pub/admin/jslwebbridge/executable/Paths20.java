package com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.jslwebbridge.executable;


/**
 * JCP JSL Web Bridge - Admin / JSL Web Bridge / Executable 2.0
 */
public class Paths20 extends com.robypomper.josp.defs.admin.jslwebbridge.executable.Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to manage JCP as Admin";


    // API Paths

    //@formatter:off
    // JCP JSL Web Bridge Executable methods
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC                  = API_PATH + "/" + API_VER + "/" + MTHD_EXEC;
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_ONLINE           = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_ONLINE;
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_PROCESS          = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_PROCESS;
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_JAVA             = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_JAVA;
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_JAVA_VM          = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_JAVA_VM;
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_JAVA_RUNTIME     = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_JAVA_RUNTIME;
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_JAVA_TIMES       = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_JAVA_TIMES;
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_JAVA_CLASSES     = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_JAVA_CLASSES;
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_JAVA_MEMORY      = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_JAVA_MEMORY;
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_JAVA_THREADS     = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_JAVA_THREADS;
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_JAVA_THREAD      = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_JAVA_THREAD;
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_OS               = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_OS;
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_CPU              = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_CPU;
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_MEMORY           = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_MEMORY;
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_DISKS            = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_DISKS;
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_DISK             = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_DISK;
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_NETWORKS         = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_NETWORKS;
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_NETWORK          = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_NETWORK;
    //@formatter:on


    // API Paths composers

    //@formatter:off
    // JCP JSL Web Bridge Executable methods
    public static String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_JAVA_THREAD         (long threadId){ return FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_JAVA_THREAD      .replace(PARAM_URL_THREAD,Long.toString(threadId)); }
    public static String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_DISK                (String diskId){ return FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_DISK             .replace(PARAM_URL_DISK,diskId); }
    public static String FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_NETWORK             (int networkId){ return FULL_PATH_JSLWB_ADMIN_JSLWB_EXEC_NETWORK          .replace(PARAM_URL_NTWK,Integer.toString(networkId)); }
    //@formatter:off


    // API Descriptions

    //@formatter:off
    // JCP JSL Web Bridge Executable methods
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_EXEC                  = DESCR_PATH_JCP_JSLWB_EXEC;
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_EXEC_ONLINE           = DESCR_PATH_JCP_JSLWB_EXEC_ONLINE;
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_EXEC_PROCESS          = DESCR_PATH_JCP_JSLWB_EXEC_PROCESS;
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_EXEC_JAVA             = DESCR_PATH_JCP_JSLWB_EXEC_JAVA;
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_EXEC_JAVA_VM          = DESCR_PATH_JCP_JSLWB_EXEC_JAVA_VM;
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_EXEC_JAVA_RUNTIME     = DESCR_PATH_JCP_JSLWB_EXEC_JAVA_RUNTIME;
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_EXEC_JAVA_TIMES       = DESCR_PATH_JCP_JSLWB_EXEC_JAVA_TIMES;
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_EXEC_JAVA_CLASSES     = DESCR_PATH_JCP_JSLWB_EXEC_JAVA_CLASSES;
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_EXEC_JAVA_MEMORY      = DESCR_PATH_JCP_JSLWB_EXEC_JAVA_MEMORY;
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_EXEC_JAVA_THREADS     = DESCR_PATH_JCP_JSLWB_EXEC_JAVA_THREAD;
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_EXEC_JAVA_THREAD      = DESCR_PATH_JCP_JSLWB_EXEC_JAVA_THREADS;
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_EXEC_OS               = DESCR_PATH_JCP_JSLWB_EXEC_OS;
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_EXEC_CPU              = DESCR_PATH_JCP_JSLWB_EXEC_CPU;
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_EXEC_MEMORY           = DESCR_PATH_JCP_JSLWB_EXEC_MEMORY;
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_EXEC_DISKS            = DESCR_PATH_JCP_JSLWB_EXEC_DISKS;
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_EXEC_DISK             = DESCR_PATH_JCP_JSLWB_EXEC_DISK;
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_EXEC_NETWORKS         = DESCR_PATH_JCP_JSLWB_EXEC_NETWORKS;
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_EXEC_NETWORK          = DESCR_PATH_JCP_JSLWB_EXEC_NETWORK;
    //@formatter:on

}
