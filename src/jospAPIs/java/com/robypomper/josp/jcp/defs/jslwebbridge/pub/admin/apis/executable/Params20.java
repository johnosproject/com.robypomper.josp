package com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.apis.executable;


import com.robypomper.josp.types.RESTItemList;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JCP JSL Web Bridge - Admin / APIs / Executable 2.0
 */
public class Params20 extends com.robypomper.josp.defs.admin.apis.executable.Params20 {

    // JCP APIs Executable methods

    public static class Index {

        public final String urlOnline = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_ONLINE;
        public final String urlProcess = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_PROCESS;
        public final String urlJava = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA;
        public final String urlOS = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_OS;
        public final String urlCPU = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_CPU;
        public final String urlMemory = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_MEMORY;
        public final String urlDisks = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_DISKS;
        public final String urlNetworks = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_NETWORKS;

    }

    public static class JavaIndex {

        public final String urlVM = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_VM;
        public final String urlRuntime = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_RUNTIME;
        public final String urlTimes = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_TIMES;
        public final String urlClasses = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_CLASSES;
        public final String urlMemory = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_MEMORY;
        public final String urlThreads = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_THREADS;

    }

}
