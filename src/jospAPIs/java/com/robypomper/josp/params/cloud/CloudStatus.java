package com.robypomper.josp.params.cloud;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.util.*;

@JsonAutoDetect
public class CloudStatus {

    public static int BYTE_TRANSFORM = 1024*1024;

    @JsonAutoDetect
    public static class Process {

        public Process() {

        }

    }
    public static class Java {

        public final String vmName;             // "OpenJDK 64-Bit Server VM"
        public final String vmVersion;          // "25.262-b10"                             "11.0.4+11"
        public final String specName;           // "Java Virtual Machine Specification"
        public final String specVendor;         // "Oracle Corporation"
        public final String specVersion;        // "1.8"                                    "11"
        public final String specMngmVersion;    // "1.2"                                    "2.0"

        public final Map<String,String> runtimeSystemProps;
        public final List<String> runtimeInputArgs;
        public final String runtimePathClass;
        public final String runtimePathBootClass;
        public final String runtimePathLibrary;

        public final Date timeStart;
        public final long timeRunning;

        public final long classesLoaded;
        public final long classesLoadedTotal;
        public final long classesUnoaded;

        public final double memoryInit;
        public final double memoryUsed;
        public final double memoryCommited;
        public final double memoryMax;
        public final double memoryHeapInit;
        public final double memoryHeapUsed;
        public final double memoryHeapFree;
        public final double memoryHeapCommited;
        public final double memoryHeapMax;

        public final long[] threadsIds;
        public final int threadsCount;
        public final int threadsCountDaemon;
        public final int threadsCountPeak;
        public final long threadsCountStarted;

        public Java() {
            vmName = ManagementFactory.getRuntimeMXBean().getVmName();
            vmVersion = ManagementFactory.getRuntimeMXBean().getVmVersion();

            specName = ManagementFactory.getRuntimeMXBean().getSpecName();
            specVendor = ManagementFactory.getRuntimeMXBean().getSpecVendor();
            specVersion = ManagementFactory.getRuntimeMXBean().getSpecVersion();
            specMngmVersion = ManagementFactory.getRuntimeMXBean().getManagementSpecVersion();

            runtimeSystemProps = ManagementFactory.getRuntimeMXBean().getSystemProperties();
            runtimeInputArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
            runtimePathClass = ManagementFactory.getRuntimeMXBean().getClassPath();
            if (ManagementFactory.getRuntimeMXBean().isBootClassPathSupported())
                runtimePathBootClass = ManagementFactory.getRuntimeMXBean().getBootClassPath();
            else
                runtimePathBootClass = "NotSupported by RuntimeMXBean";
            runtimePathLibrary = ManagementFactory.getRuntimeMXBean().getLibraryPath();

            timeStart = new Date(ManagementFactory.getRuntimeMXBean().getStartTime());
            timeRunning = ManagementFactory.getRuntimeMXBean().getUptime();

            classesLoaded = ManagementFactory.getClassLoadingMXBean().getLoadedClassCount();
            classesLoadedTotal = ManagementFactory.getClassLoadingMXBean().getTotalLoadedClassCount();
            classesUnoaded = ManagementFactory.getClassLoadingMXBean().getUnloadedClassCount();

            memoryInit = (double)(ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getInit() / BYTE_TRANSFORM);
            memoryUsed = (double)(ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed() / BYTE_TRANSFORM);
            memoryCommited = (double)(ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getCommitted() / BYTE_TRANSFORM);
            memoryMax = (double)(ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getMax() / BYTE_TRANSFORM);
            memoryHeapInit = (double)(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getInit() / BYTE_TRANSFORM);
            memoryHeapUsed = (double)(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / BYTE_TRANSFORM);
            memoryHeapFree = (double)Runtime.getRuntime().freeMemory() / BYTE_TRANSFORM;
            memoryHeapCommited = (double)(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted() / BYTE_TRANSFORM);
            //(double)Runtime.getRuntime().totalMemory() / byteTransform
            memoryHeapMax = (double)(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / BYTE_TRANSFORM);
            //Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? -1 : (double)Runtime.getRuntime().maxMemory() / byteTransform

            threadsIds = ManagementFactory.getThreadMXBean().getAllThreadIds();
            threadsCount = ManagementFactory.getThreadMXBean().getThreadCount();
            threadsCountDaemon = ManagementFactory.getThreadMXBean().getDaemonThreadCount();
            threadsCountPeak = ManagementFactory.getThreadMXBean().getPeakThreadCount();
            threadsCountStarted = ManagementFactory.getThreadMXBean().getTotalStartedThreadCount();
        }

    }
    public static class JavaThread {

        public final long id;
        public final String name;
        public final String state;
        public final long timeCpu;
        public final long timeUser;
        public final long timeWaited;
        public final long timeWaitedCount;
        public final long timeBlocked;
        public final long timeBlockedCount;

        public JavaThread(long thId) {
            this.id = ManagementFactory.getThreadMXBean().getThreadInfo(thId).getThreadId();
            this.name = ManagementFactory.getThreadMXBean().getThreadInfo(thId).getThreadName();
            this.state = ManagementFactory.getThreadMXBean().getThreadInfo(thId).getThreadState().toString();
            this.timeCpu = ManagementFactory.getThreadMXBean().getThreadCpuTime(thId);
            this.timeUser = ManagementFactory.getThreadMXBean().getThreadUserTime(thId);
            this.timeWaited = ManagementFactory.getThreadMXBean().getThreadInfo(thId).getWaitedCount();
            this.timeWaitedCount = ManagementFactory.getThreadMXBean().getThreadInfo(thId).getWaitedTime();
            this.timeBlocked = ManagementFactory.getThreadMXBean().getThreadInfo(thId).getBlockedCount();
            this.timeBlockedCount = ManagementFactory.getThreadMXBean().getThreadInfo(thId).getBlockedTime();
        }
    }
    public static class Os {

        public final String name;           // "Mac OS X"  "Linux"
        public final String arch;           // "x86_64"    "amd64"

        public Os() {
            this.name = ManagementFactory.getOperatingSystemMXBean().getName();
            this.arch = ManagementFactory.getOperatingSystemMXBean().getArch();
        }

    }
    public static class CPU {

        public final int count;             // 8        4
        public final double loadAvg;        // 3.05...  0.75

        public CPU() {
            //this.count = Runtime.getRuntime().availableProcessors();
            this.count = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
            this.loadAvg = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        }

    }
    @JsonAutoDetect
    public static class Memory {

        public Memory() {

        }

    }
    @JsonAutoDetect
    public static class Disks {
        List<Disk> list;

        public Disks() {
            list = new ArrayList<>();
            File[] roots = File.listRoots();
            for (File root : roots) {
                list.add(new Disk(root));
            }
        }

    }
    public static class Disk {

        public final String name;
        public final long spaceFree;
        public final long spaceUsable;
        public final long spaceMax;

        public Disk(File root) {
            name = root.getAbsolutePath();
            spaceFree = root.getFreeSpace() / BYTE_TRANSFORM;
            spaceUsable = root.getUsableSpace() / BYTE_TRANSFORM;
            spaceMax = root.getTotalSpace() / BYTE_TRANSFORM;
        }

    }
    public static class Network {

        public final InetAddress addrLoopback;
        public final InetAddress addrLocalhost;
        //                InetAddress.getAddress();
        //                InetAddress.getHostAddress();
        //                InetAddress.getHostName();
        //                InetAddress.getCanonicalHostName();

        public Network() {
            addrLoopback = InetAddress.getLoopbackAddress();
            try {
                addrLocalhost = InetAddress.getLocalHost();

            } catch (UnknownHostException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

    }
    public static class NetworkIntf {

        public final int index;
        public final String name;
        public final String nameDisplay;
        public final boolean isUp;
        public final boolean isLoopback;
        public final boolean isPointToPoint;
        public final boolean isVirtual;
        public final int MTU;
        public final List<InetAddress> address;
        public final List<InterfaceAddress> addressIntf;
        public final List<Integer> subIntfIndexes;

        public NetworkIntf(NetworkInterface itf) throws SocketException {
            index = itf.getIndex();
            name = itf.getName();
            nameDisplay = itf.getDisplayName();
            isUp = itf.isUp();
            isLoopback = itf.isLoopback();
            isPointToPoint = itf.isPointToPoint();
            isVirtual = itf.isVirtual();
            itf.getHardwareAddress();
            address = Collections.list(itf.getInetAddresses());
            addressIntf = itf.getInterfaceAddresses();
            MTU = itf.getMTU();
            List<NetworkInterface> subItfs = Collections.list(itf.getSubInterfaces());
            subIntfIndexes = new ArrayList<>();
            for (NetworkInterface subItf : subItfs) {
                subIntfIndexes.add(subItf.getIndex());
            }
        }
    }


//
//
//    /*
//    jcpfe
//        process
//            %cpu used
//        host
//            cpu
//            memory
//        network
//            address
//            port
//        sessions
//            ...
//     */
//
//    public int sessionCount;
//    public List<Session> sessions;
//
//    public static class Session {
//        public String id;
//
//    }
//    public static class Host {
//        public String hostname;
//
//        public int cpuCount;
//        public long memoryFree;
//        public long memoryMax;
//        public long memoryJvm;
//    }
//    public static class Disk_OLD {
//        public String name;
//
//        public long spaceFree;
//        public long spaceMax;
//        public long spaceUsable;
//    }
//
//
//    public CloudStatus(JSL currentJSL, Map<String, HttpSession> sessions, boolean s) {
///*
//        this.sessionCount = sessions.size();
//        this.name = currentJSL.getServiceInfo().getSrvName();
//
//        this.status = jsl.status();
//        this.isJCPConnected = jsl.getJCPClient().isConnected();
//        this.isCloudConnected = jsl.getCommunication().isCloudConnected();
//        this.isLocalRunning = jsl.getCommunication().isLocalRunning();
//        this.srvId = jsl.getServiceInfo().getSrvId();
//        this.usrId = jsl.getServiceInfo().getUserId();
//        this.instId = jsl.getServiceInfo().getInstanceId();
//        this.jslVersion = jsl.version();
//        this.supportedJCPAPIsVersions = jsl.versionsJCPAPIs();
//        this.supportedJOSPProtocolVersions = jsl.versionsJOSPProtocol();
//        this.supportedJODVersions = jsl.versionsJOSPObject();
//        this.sessionId = session.getId();
//
//        this.id = jslUsrMngr.getUserId();
//        this.name = jslUsrMngr.getUsername();
//        this.isAuthenticated = jslUsrMngr.isUserAuthenticated();
//        this.isAdmin = jslUsrMngr.isAdmin();
//        this.isMaker = jslUsrMngr.isMaker();
//        this.isDeveloper = jslUsrMngr.isDeveloper();
//
// */
//    }
//
//
//    static public void printAll_ManagementFactory() {
//        String FORMAT = "%-60s: %s";
//        String FORMAT_FLOAT = "%-60s: %.2f";
//
//        // OS
//        System.out.println(String.format(FORMAT, "getOperatingSystemMXBean.getName: ", ManagementFactory.getOperatingSystemMXBean().getName()));
//        System.out.println(String.format(FORMAT, "getOperatingSystemMXBean.getArch: ", ManagementFactory.getOperatingSystemMXBean().getArch()));
//        System.out.println();
//
//        // CPU
//        System.out.println(String.format(FORMAT, "getOperatingSystemMXBean.getAvailableProcessors: ", ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors()));
//        System.out.println(String.format(FORMAT, "getOperatingSystemMXBean.getSystemLoadAverage: ", ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage()));
//        System.out.println();
//
//        // Java
//        System.out.println(String.format(FORMAT, "getRuntimeMXBean.getName: ", ManagementFactory.getRuntimeMXBean().getName()));                    // no
//        System.out.println(String.format(FORMAT, "getRuntimeMXBean.getObjectName: ", ManagementFactory.getRuntimeMXBean().getObjectName()));        // no
//        System.out.println(String.format(FORMAT, "getRuntimeMXBean.getVmName: ", ManagementFactory.getRuntimeMXBean().getVmName()));
//        System.out.println(String.format(FORMAT, "getRuntimeMXBean.getVmVersion: ", ManagementFactory.getRuntimeMXBean().getVmVersion()));
//        System.out.println(String.format(FORMAT, "getRuntimeMXBean.getSpecName: ", ManagementFactory.getRuntimeMXBean().getSpecName()));
//        System.out.println(String.format(FORMAT, "getRuntimeMXBean.getSpecVendor: ", ManagementFactory.getRuntimeMXBean().getSpecVendor()));
//        System.out.println(String.format(FORMAT, "getRuntimeMXBean.getSpecVersion: ", ManagementFactory.getRuntimeMXBean().getSpecVersion()));
//        System.out.println(String.format(FORMAT, "getRuntimeMXBean.getManagementSpecVersion: ", ManagementFactory.getRuntimeMXBean().getManagementSpecVersion()));
//        System.out.println(String.format(FORMAT, "getRuntimeMXBean.getSystemProperties: ", ManagementFactory.getRuntimeMXBean().getSystemProperties()));
//        System.out.println(String.format(FORMAT, "getRuntimeMXBean.getInputArguments: ", ManagementFactory.getRuntimeMXBean().getInputArguments()));
//        System.out.println(String.format(FORMAT, "getRuntimeMXBean.getClassPath: ", ManagementFactory.getRuntimeMXBean().getClassPath()));
//        if (ManagementFactory.getRuntimeMXBean().isBootClassPathSupported())
//            System.out.println(String.format(FORMAT, "getRuntimeMXBean.getBootClassPath: ", ManagementFactory.getRuntimeMXBean().getBootClassPath()));
//        System.out.println(String.format(FORMAT, "getRuntimeMXBean.getLibraryPath: ", ManagementFactory.getRuntimeMXBean().getLibraryPath()));
//        System.out.println(String.format(FORMAT, "getRuntimeMXBean.getStartTime: ", ManagementFactory.getRuntimeMXBean().getStartTime()));
//        System.out.println(String.format(FORMAT, "getRuntimeMXBean.getUptime: ", ManagementFactory.getRuntimeMXBean().getUptime()));
//        System.out.println();
//
//        System.out.println(String.format(FORMAT, "getClassLoadingMXBean.getLoadedClassCount: ", ManagementFactory.getClassLoadingMXBean().getLoadedClassCount()));
//        System.out.println(String.format(FORMAT, "getClassLoadingMXBean.getTotalLoadedClassCount: ", ManagementFactory.getClassLoadingMXBean().getTotalLoadedClassCount()));
//        System.out.println(String.format(FORMAT, "getClassLoadingMXBean.getUnloadedClassCount: ", ManagementFactory.getClassLoadingMXBean().getUnloadedClassCount()));
//        System.out.println(String.format(FORMAT, "getCompilationMXBean.getName: ", ManagementFactory.getCompilationMXBean().getName()));                                                // no
//        System.out.println(String.format(FORMAT, "getCompilationMXBean.getTotalCompilationTime: ", ManagementFactory.getCompilationMXBean().getTotalCompilationTime()));                // no
//        System.out.println(String.format(FORMAT, "getGarbageCollectorMXBeans.isEmpty: ", ManagementFactory.getGarbageCollectorMXBeans().isEmpty()));                                    // no
//        for (int i = 0; i<ManagementFactory.getGarbageCollectorMXBeans().size(); i++) {
//            System.out.println(String.format(FORMAT, "getGarbageCollectorMXBeans[" + i + "].getName: ", ManagementFactory.getGarbageCollectorMXBeans().get(i).getName()));                          // no
//            System.out.println(String.format(FORMAT, "getGarbageCollectorMXBeans[" + i + "].getObjectName: ", ManagementFactory.getGarbageCollectorMXBeans().get(i).getObjectName()));              // no
//            System.out.println(String.format(FORMAT, "getGarbageCollectorMXBeans[" + i + "].getMemoryPoolNames: ", Arrays.toString(ManagementFactory.getGarbageCollectorMXBeans().get(i).getMemoryPoolNames())));    // no
//            System.out.println(String.format(FORMAT, "getGarbageCollectorMXBeans[" + i + "].getCollectionCount: ", ManagementFactory.getGarbageCollectorMXBeans().get(i).getCollectionCount()));    // no
//            System.out.println(String.format(FORMAT, "getGarbageCollectorMXBeans[" + i + "].getCollectionTime: ", ManagementFactory.getGarbageCollectorMXBeans().get(i).getCollectionTime()));      // no
//        }
//        System.out.println();
//
//        int byteTransform = 1024*1024; // Bytes to MBytes
//        System.out.println(String.format(FORMAT_FLOAT, "Runtime.getRuntime().freeMemory() (GB): ", (double)Runtime.getRuntime().freeMemory() / byteTransform));
//        System.out.println(String.format(FORMAT_FLOAT, "Runtime.getRuntime().maxMemory() (GB): ", Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? -1 : (double)Runtime.getRuntime().maxMemory() / byteTransform));
//
//
//        // Java/Memory
//        System.out.println(String.format(FORMAT_FLOAT, "getMemoryMXBean.getHeapMemoryUsage.getInit (GB): ", (double)ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getInit() / byteTransform));
//        System.out.println(String.format(FORMAT_FLOAT, "getMemoryMXBean.getHeapMemoryUsage.getUsed (GB): ", (double)ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / byteTransform));
//        System.out.println(String.format(FORMAT_FLOAT, "Runtime.getRuntime().freeMemory() (GB): ", (double)Runtime.getRuntime().freeMemory() / byteTransform));
//        System.out.println(String.format(FORMAT_FLOAT, "getMemoryMXBean.getHeapMemoryUsage.getCommitted (GB): ", (double)ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted() / byteTransform));
//            System.out.println(String.format(FORMAT_FLOAT, "\tRuntime.getRuntime().totalMemory() (GB): ", (double)Runtime.getRuntime().totalMemory() / byteTransform));
//        System.out.println(String.format(FORMAT_FLOAT, "getMemoryMXBean.getHeapMemoryUsage.getMax (GB): ", (double)ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / byteTransform));
//            System.out.println(String.format(FORMAT_FLOAT, "\tRuntime.getRuntime().maxMemory() (GB): ", Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? -1 : (double)Runtime.getRuntime().maxMemory() / byteTransform));
//        System.out.println(String.format(FORMAT_FLOAT, "getMemoryMXBean.getNonHeapMemoryUsage.getInit (GB): ", (double)ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getInit() / byteTransform));
//        System.out.println(String.format(FORMAT_FLOAT, "getMemoryMXBean.getNonHeapMemoryUsage.getUsed (GB): ", (double)ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed() / byteTransform));
//        System.out.println(String.format(FORMAT_FLOAT, "getMemoryMXBean.getNonHeapMemoryUsage.getCommitted (GB): ", (double)ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getCommitted() / byteTransform));
//        System.out.println(String.format(FORMAT_FLOAT, "getMemoryMXBean.getNonHeapMemoryUsage.getMax (GB): ", (double)ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getMax() / byteTransform));
//        System.out.println(String.format(FORMAT, "getMemoryMXBean.getObjectPendingFinalizationCount: ", ManagementFactory.getMemoryMXBean().getObjectPendingFinalizationCount()));              // no
//
//        System.out.println(String.format(FORMAT, "getMemoryManagerMXBeans.getName: ", ManagementFactory.getMemoryManagerMXBeans().get(0).getName()));                               // no
//        System.out.println(String.format(FORMAT, "getMemoryManagerMXBeans.getObjectName: ", ManagementFactory.getMemoryManagerMXBeans().get(0).getObjectName()));                   // no
//        System.out.println(String.format(FORMAT, "getMemoryManagerMXBeans.getMemoryPoolNames: ", ManagementFactory.getMemoryManagerMXBeans().get(0).getMemoryPoolNames()));         // no
//        System.out.println();
//
//        for (int i = 0; i<ManagementFactory.getMemoryPoolMXBeans().size(); i++) {
//            System.out.println(String.format(FORMAT, "getMemoryPoolMXBeans[" + i + "].YY: ", ManagementFactory.getMemoryPoolMXBeans().get(i).getName()));                                          // no
//            System.out.println(String.format(FORMAT, "getMemoryPoolMXBeans[" + i + "].YY: ", ManagementFactory.getMemoryPoolMXBeans().get(i).getObjectName()));                                    // no
//            System.out.println(String.format(FORMAT, "getMemoryPoolMXBeans[" + i + "].YY: ", ManagementFactory.getMemoryPoolMXBeans().get(i).isValid()));                                          // no
//            System.out.println(String.format(FORMAT, "getMemoryPoolMXBeans[" + i + "].YY: ", ManagementFactory.getMemoryPoolMXBeans().get(i).getMemoryManagerNames()));
//            System.out.println(String.format(FORMAT_FLOAT, "getMemoryPoolMXBeans[" + i + "].getPeakUsage ini: ", (double) ManagementFactory.getMemoryPoolMXBeans().get(i).getPeakUsage().getInit() / byteTransform));       // no
//            System.out.println(String.format(FORMAT_FLOAT, "getMemoryPoolMXBeans[" + i + "].getPeakUsage usd: ", (double) ManagementFactory.getMemoryPoolMXBeans().get(i).getPeakUsage().getUsed() / byteTransform));       // no
//            System.out.println(String.format(FORMAT_FLOAT, "getMemoryPoolMXBeans[" + i + "].getPeakUsage com: ", (double) ManagementFactory.getMemoryPoolMXBeans().get(i).getPeakUsage().getCommitted() / byteTransform));  // no
//            System.out.println(String.format(FORMAT_FLOAT, "getMemoryPoolMXBeans[" + i + "].getPeakUsage max: ", (double) ManagementFactory.getMemoryPoolMXBeans().get(i).getPeakUsage().getMax() / byteTransform));        // no
//            System.out.println(String.format(FORMAT, "getMemoryPoolMXBeans[" + i + "].YY: ", ManagementFactory.getMemoryPoolMXBeans().get(i).isCollectionUsageThresholdSupported()));
//            if (ManagementFactory.getMemoryPoolMXBeans().get(i).isCollectionUsageThresholdSupported()) {
//                System.out.println(String.format(FORMAT, "getMemoryPoolMXBeans[" + i + "].YY: ", ManagementFactory.getMemoryPoolMXBeans().get(i).getCollectionUsageThreshold()));           // no
//                System.out.println(String.format(FORMAT, "getMemoryPoolMXBeans[" + i + "].YY: ", ManagementFactory.getMemoryPoolMXBeans().get(i).getCollectionUsageThresholdCount()));      // no
//                System.out.println(String.format(FORMAT, "getMemoryPoolMXBeans[" + i + "].YY: ", ManagementFactory.getMemoryPoolMXBeans().get(i).isCollectionUsageThresholdExceeded()));    // no
//            }
//
//            if (ManagementFactory.getMemoryPoolMXBeans().get(i).getCollectionUsage() != null) {
//                System.out.println(String.format(FORMAT, "getMemoryPoolMXBeans[" + i + "].YY: ", ManagementFactory.getMemoryPoolMXBeans().get(i).getCollectionUsage().getCommitted()));     // no
//                System.out.println(String.format(FORMAT, "getMemoryPoolMXBeans[" + i + "].YY: ", ManagementFactory.getMemoryPoolMXBeans().get(i).getCollectionUsage().getInit()));          // no
//                System.out.println(String.format(FORMAT, "getMemoryPoolMXBeans[" + i + "].YY: ", ManagementFactory.getMemoryPoolMXBeans().get(i).getCollectionUsage().getUsed()));          // no
//                System.out.println(String.format(FORMAT, "getMemoryPoolMXBeans[" + i + "].YY: ", ManagementFactory.getMemoryPoolMXBeans().get(i).getCollectionUsage().getMax()));           // no
//            }
//            System.out.println(String.format(FORMAT, "getMemoryPoolMXBeans[" + i + "].YY: ", ManagementFactory.getMemoryPoolMXBeans().get(i).getType()));       // no
//            System.out.println(String.format(FORMAT, "getMemoryPoolMXBeans[" + i + "].YY: ", ManagementFactory.getMemoryPoolMXBeans().get(i).getUsage()));      // no
//            if (ManagementFactory.getMemoryPoolMXBeans().get(i).isUsageThresholdSupported()) {
//                System.out.println(String.format(FORMAT, "getMemoryPoolMXBeans[" + i + "].YY: ", ManagementFactory.getMemoryPoolMXBeans().get(i).getUsageThreshold()));             // no
//                System.out.println(String.format(FORMAT, "getMemoryPoolMXBeans[" + i + "].YY: ", ManagementFactory.getMemoryPoolMXBeans().get(i).getUsageThresholdCount()));        // no
//                System.out.println(String.format(FORMAT, "getMemoryPoolMXBeans[" + i + "].YY: ", ManagementFactory.getMemoryPoolMXBeans().get(i).isUsageThresholdExceeded()));      // no
//            }
//        }
//        System.out.println();
//
//        System.out.println(String.format(FORMAT, "getThreadMXBean.getAllThreadIds: ", Arrays.toString(ManagementFactory.getThreadMXBean().getAllThreadIds())));
//        System.out.println(String.format(FORMAT, "getThreadMXBean.getDaemonThreadCount: ", ManagementFactory.getThreadMXBean().getDaemonThreadCount()));
//        System.out.println(String.format(FORMAT, "getThreadMXBean.getPeakThreadCount: ", ManagementFactory.getThreadMXBean().getPeakThreadCount()));
//        System.out.println(String.format(FORMAT, "getThreadMXBean.getThreadCount: ", ManagementFactory.getThreadMXBean().getThreadCount()));
//        System.out.println(String.format(FORMAT, "getThreadMXBean.getTotalStartedThreadCount: ", ManagementFactory.getThreadMXBean().getTotalStartedThreadCount()));
//        for (long thId : ManagementFactory.getThreadMXBean().getAllThreadIds()) {
//            System.out.println(String.format(FORMAT, "getThreadMXBean.getThreadId: " + thId + " ", ManagementFactory.getThreadMXBean().getThreadInfo(thId).getThreadId()));
//            System.out.println(String.format(FORMAT, "getThreadMXBean.getThreadName: " + thId + " ", ManagementFactory.getThreadMXBean().getThreadInfo(thId).getThreadName()));
//            System.out.println(String.format(FORMAT, "getThreadMXBean.getThreadState: " + thId + " ", ManagementFactory.getThreadMXBean().getThreadInfo(thId).getThreadState()));
//            System.out.println(String.format(FORMAT, "getThreadMXBean.getThreadCpuTime: " + thId + " ", ManagementFactory.getThreadMXBean().getThreadCpuTime(thId)));
//            System.out.println(String.format(FORMAT, "getThreadMXBean.getThreadUserTime: " + thId + " ", ManagementFactory.getThreadMXBean().getThreadUserTime(thId)));
//            System.out.println(String.format(FORMAT, "getThreadMXBean.getWaitedCount: " + thId + " ", ManagementFactory.getThreadMXBean().getThreadInfo(thId).getWaitedCount()));
//            System.out.println(String.format(FORMAT, "getThreadMXBean.getWaitedTime: " + thId + " ", ManagementFactory.getThreadMXBean().getThreadInfo(thId).getWaitedTime()));
//            System.out.println(String.format(FORMAT, "getThreadMXBean.getBlockedCount: " + thId + " ", ManagementFactory.getThreadMXBean().getThreadInfo(thId).getBlockedCount()));
//            System.out.println(String.format(FORMAT, "getThreadMXBean.getBlockedTime: " + thId + " ", ManagementFactory.getThreadMXBean().getThreadInfo(thId).getBlockedTime()));
//            System.out.println(String.format(FORMAT, "getThreadMXBean.getLockName: " + thId + " ", ManagementFactory.getThreadMXBean().getThreadInfo(thId).getLockName()));             // no
//            System.out.println(String.format(FORMAT, "getThreadMXBean.getLockInfo: " + thId + " ", ManagementFactory.getThreadMXBean().getThreadInfo(thId).getLockInfo()));             // no
//            System.out.println(String.format(FORMAT, "getThreadMXBean.getLockOwnerId: " + thId + " ", ManagementFactory.getThreadMXBean().getThreadInfo(thId).getLockOwnerId()));       // no
//            System.out.println(String.format(FORMAT, "getThreadMXBean.getLockOwnerName: " + thId + " ", ManagementFactory.getThreadMXBean().getThreadInfo(thId).getLockOwnerName()));   // no
//            System.out.println(String.format(FORMAT, "getThreadMXBean.getLockedSynchronizers: " + thId + " ", Arrays.toString(ManagementFactory.getThreadMXBean().getThreadInfo(thId).getLockedSynchronizers())));  // no
//            System.out.println(String.format(FORMAT, "getThreadMXBean.getLockedMonitors: " + thId + " ", Arrays.toString(ManagementFactory.getThreadMXBean().getThreadInfo(thId).getLockedMonitors())));            // no
//        }
//        System.out.println();
//
//        // Java/server
//        System.out.println(String.format(FORMAT, "getPlatformMBeanServer.YY: ", ManagementFactory.getPlatformMBeanServer().getDefaultDomain()));                // no
//        System.out.println(String.format(FORMAT, "getPlatformMBeanServer.YY: ", Arrays.toString(ManagementFactory.getPlatformMBeanServer().getDomains())));     // no
//        System.out.println(String.format(FORMAT, "getPlatformMBeanServer.YY: ", ManagementFactory.getPlatformMBeanServer().getMBeanCount()));                   // no
//    }
//
//    public static void main(String[] args) {
//        //printAll_ManagementFactory();
//
//        CloudStatus obj = new CloudStatus(null,null);
//
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
//            System.out.println(jsonInString);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
///*
//
//    private static String getHostname() {}
//    private static int getHostCPUCores() { return Runtime.getRuntime().availableProcessors(); }
//    private static int getHostCPUProcessLoad() { return ((OperatingSystemMXBean)ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).); }
//    private static int getHostCPUAllLoad() { return Runtime.getRuntime().availableProcessors(); }
//    private static long getHostMemoryFree() { return Runtime.getRuntime().freeMemory(); }
//    private static long getHostMemoryMax() { return Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? -1 : Runtime.getRuntime().maxMemory(); }
//    private static long getHostMemoryJvm() { return Runtime.getRuntime().totalMemory(); }
//
//
//    private static List<Disk> getDisks() {
//        List<Disk> disks = new ArrayList<>();
//        File[] roots = File.listRoots();
//
//        // For each filesystem root, print some info
//        for (File root : roots) {
//            Disk d = new Disk();
//            d.name = root.getAbsolutePath();
//            d.spaceFree = root.getFreeSpace();
//            d.spaceMax = root.getTotalSpace();
//            d.spaceUsable = root.getUsableSpace();
//            disks.add(d);
//        }
//
//        return disks;
//    }
//
//    private static String getHostOS() {
//        ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().
//
//        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
//        //OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
//// What % CPU load this current JVM is taking, from 0.0-1.0
//        System.out.println(osBean.getProcessCpuLoad());
//
//// What % load the overall system is at, from 0.0-1.0
//        System.out.println(osBean.getSystemCpuLoad());
//
//    }
//    private static String getHostMem() {}
//    private static List<String> getHostAddress() {}
//    private static List<String> getHostPort() {}
//*/


}
