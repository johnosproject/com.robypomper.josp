package com.robypomper.josp.params.jcp;

import com.robypomper.BuildInfo;

public class APIsStatus extends ServiceStatus {

    public APIsStatus() {
    }

    public APIsStatus(BuildInfo buildInfo) {
        super(buildInfo);
    }

    public static class Objects {
        public long count;
        public long onlineCount;
        public long offlineCount;
        public long activeCount;
        public long inactiveCount;
        public long ownersCount;
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
