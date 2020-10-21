package com.robypomper.josp.jcp.apis.params.admin;

import com.robypomper.cloud.params.CloudStatus;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.List;

public class JCPCloudStatus {

    public static class JCPAPI {

        public final Date timeStart;
        public final long timeRunning;
        public final double cpuUsed;
        public final double memoryUsed;

        // public long objectsCount = -1;
        // ...

        public JCPAPI() {
            timeStart = new Date(ManagementFactory.getRuntimeMXBean().getStartTime());
            timeRunning = ManagementFactory.getRuntimeMXBean().getUptime();
            cpuUsed = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();;
            memoryUsed = (double)Runtime.getRuntime().freeMemory() / CloudStatus.BYTE_TRANSFORM;
        }

    }

    public static class JCPGWs {

        public enum Type {
            O2S, S2O
        }

        public static class Client {

            public final String id;
            public final boolean isConnected;

            public Client(String id, boolean isConnected) {
                this.id = id;
                this.isConnected = isConnected;
            }

        }

        public String id;
        public Type type;
        public boolean isRunning;
        public String address;
        public String hostName;
        public String hostNameCanonical;
        public int port;
        public int clientsCount;
        public List<Client> clientsList;

        public JCPGWs() {}

    }

    public static class JCPObjects {
        public long objectsCount;
        public long objectsOnlineCount;
        public long objectsOfflineCount;
        public long objectsActiveCount;
        public long objectsInactiveCount;
        public long objectsOwnersCount;
    }

    public static class JCPServices {
        public long servicesCount;
        public long servicesOnlineCount;
        public long servicesOfflineCount;
        public long servicesInstancesCount;
        public long servicesInstancesOnlineCount;
        public long servicesInstancesOfflineCount;
    }

    public static class JCPUsers {
        public long usersCount;
    }
}
