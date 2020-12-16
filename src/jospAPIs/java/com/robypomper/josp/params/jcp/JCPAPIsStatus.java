package com.robypomper.josp.params.jcp;

import com.robypomper.josp.types.josp.gw.GWType;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.List;

public class JCPAPIsStatus {

    public final Date timeStart;
    public final long timeRunning;
    public final double cpuUsed;
    public final double memoryUsed;

    public JCPAPIsStatus() {
        timeStart = new Date(ManagementFactory.getRuntimeMXBean().getStartTime());
        timeRunning = ManagementFactory.getRuntimeMXBean().getUptime();
        cpuUsed = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        ;
        memoryUsed = (double) Runtime.getRuntime().freeMemory() / JCPStatus.BYTE_TRANSFORM;
    }

    public static class Objects {
        public long count;
        public long onlineCount;
        public long offlineCount;
        public long activeCount;
        public long inactiveCount;
        public long ownersCount;
    }

    public static class GWs {

        public static class Client {

            public final String id;
            public final boolean isConnected;

            public Client(String id, boolean isConnected) {
                this.id = id;
                this.isConnected = isConnected;
            }

        }

        public String id;
        public GWType type;
        public boolean isRunning;
        public String address;
        public String hostName;
        public String hostNameCanonical;
        public int port;
        public int clientsCount;
        public List<Client> clientsList;
    }

    public static class Services {
        public long count;
        public long onlineCount;
        public long offlineCount;
        public long instancesCount;
        public long instancesOnlineCount;
        public long instancesOfflineCount;
    }

    public static class Users {
        public long count;
    }

}