package com.robypomper.josp.defs.admin.gateways.executable;

import com.robypomper.josp.types.RESTItemList;

import java.util.List;


/**
 * JOSP Admin - Gateways / Executable 2.0
 */
public class Params20 extends com.robypomper.josp.jcp.defs.base.internal.status.executable.Params20 {

    // List

    public static class GatewaysServers {

        public List<RESTItemList> serverList;

    }


    // Index

    public static class Index {

        public final String urlOnline;
        public final String urlProcess;
        public final String urlJava;
        public final String urlOS;
        public final String urlCPU;
        public final String urlMemory;
        public final String urlDisks;
        public final String urlNetworks;

        public Index(String gwServerId) {
            this.urlOnline = Paths20.FULL_PATH_JCP_GWS_EXEC_ONLINE(gwServerId);
            this.urlProcess = Paths20.FULL_PATH_JCP_GWS_EXEC_PROCESS(gwServerId);
            this.urlJava = Paths20.FULL_PATH_JCP_GWS_EXEC_JAVA(gwServerId);
            this.urlOS = Paths20.FULL_PATH_JCP_GWS_EXEC_OS(gwServerId);
            this.urlCPU = Paths20.FULL_PATH_JCP_GWS_EXEC_CPU(gwServerId);
            this.urlMemory = Paths20.FULL_PATH_JCP_GWS_EXEC_MEMORY(gwServerId);
            this.urlDisks = Paths20.FULL_PATH_JCP_GWS_EXEC_DISKS(gwServerId);
            this.urlNetworks = Paths20.FULL_PATH_JCP_GWS_EXEC_NETWORKS(gwServerId);
        }

    }


    // Java

    public static class JavaIndex {

        public final String urlVM;
        public final String urlRuntime;
        public final String urlTimes;
        public final String urlClasses;
        public final String urlMemory;
        public final String urlThreads;

        public JavaIndex(String gwServerId) {
            this.urlVM = Paths20.FULL_PATH_JCP_GWS_EXEC_JAVA_VM(gwServerId);
            this.urlRuntime = Paths20.FULL_PATH_JCP_GWS_EXEC_JAVA_RUNTIME(gwServerId);
            this.urlTimes = Paths20.FULL_PATH_JCP_GWS_EXEC_JAVA_TIMES(gwServerId);
            this.urlClasses = Paths20.FULL_PATH_JCP_GWS_EXEC_JAVA_CLASSES(gwServerId);
            this.urlMemory = Paths20.FULL_PATH_JCP_GWS_EXEC_JAVA_MEMORY(gwServerId);
            this.urlThreads = Paths20.FULL_PATH_JCP_GWS_EXEC_JAVA_THREADS(gwServerId);
        }
    }

}
