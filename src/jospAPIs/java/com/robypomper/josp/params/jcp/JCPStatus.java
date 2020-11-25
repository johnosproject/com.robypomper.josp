package com.robypomper.josp.params.jcp;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.util.*;

@JsonAutoDetect
public class JCPStatus {

    public static int BYTE_TRANSFORM = 1024 * 1024;

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

        public final Map<String, String> runtimeSystemProps;
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

            memoryInit = (double) (ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getInit() / BYTE_TRANSFORM);
            memoryUsed = (double) (ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed() / BYTE_TRANSFORM);
            memoryCommited = (double) (ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getCommitted() / BYTE_TRANSFORM);
            memoryMax = (double) (ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getMax() / BYTE_TRANSFORM);
            memoryHeapInit = (double) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getInit() / BYTE_TRANSFORM);
            memoryHeapUsed = (double) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / BYTE_TRANSFORM);
            memoryHeapFree = (double) Runtime.getRuntime().freeMemory() / BYTE_TRANSFORM;
            memoryHeapCommited = (double) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted() / BYTE_TRANSFORM);
            //(double)Runtime.getRuntime().totalMemory() / byteTransform
            memoryHeapMax = (double) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / BYTE_TRANSFORM);
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

}
