package com.robypomper.josp.jcp.defs.gateways.internal.status;

import com.robypomper.josp.types.RESTItemList;
import com.robypomper.josp.types.josp.gw.GWType;

import java.util.Date;
import java.util.List;


/**
 * JCP Gateways - Status 2.0
 */
public class Params20 {

    // Index

    public static class Index {

        public final String urlGateways = Paths20.FULL_PATH_STATUS_GWS;
        public final String urlBroker = Paths20.FULL_PATH_STATUS_BROKER;

    }

    // GWs

    public static class GWs {

        public String id;
        public List<RESTItemList> gwList;

    }

    public static class GW {

        public String id;
        public GWType type;
        public String status;
        public String internalAddress;
        public String publicAddress;
        public int gwPort;
        public int apisPort;
        public int clientsCount;
        public int maxClientsCount;
        public List<RESTItemList> clientsList;

    }

    public static class GWClient {

        public String id;
        public boolean isConnected;
        public String local;
        public String remote;
        public long bytesRx;
        public long bytesTx;
        public Date lastDataRx;
        public Date lastDataTx;
        public Date lastConnection;
        public Date lastDisconnection;
        public Date lastHeartBeat;
        public Date lastHeartBeatFailed;

    }


    // Broker

    public static class Broker {

        public List<RESTItemList> objsList;
        public List<RESTItemList> srvsList;
        public List<RESTItemList> objsDBList;

    }

    public static class BrokerObject extends BrokerObjectDB {

    }

    public static class BrokerService {

        public String id;
        public String name;
        public String user;

    }

    public static class BrokerObjectDB {

        public String id;
        public String name;
        public String owner;

    }
}
