package com.robypomper.josp.params.jcp;

import com.robypomper.BuildInfo;
import com.robypomper.josp.types.josp.gw.GWType;

import java.util.List;

public class GWsStatus extends ServiceStatus {

    public GWsStatus() {
    }

    public GWsStatus(BuildInfo buildInfo) {
        super(buildInfo);
    }

    public static class Server {
        public String id;
        public GWType type;
        public String status;
        public String internalAddress;
        public String publicAddress;
        public int gwPort;
        public int apisPort;
        public int clientsCount;
        public int maxClientsCount;
        public List<Client> clientsList;
    }

    public static class Client {

        public final String id;
        public final boolean isConnected;
        public final String local;
        public final String remote;

        public Client(String id, boolean isConnected, String local, String remote) {
            this.id = id;
            this.isConnected = isConnected;
            this.local = local;
            this.remote = remote;
        }

    }

}
