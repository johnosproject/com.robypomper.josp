package com.robypomper.josp.jcp.defs.apis.internal.status;

import com.robypomper.josp.types.RESTItemList;
import com.robypomper.josp.types.josp.gw.GWType;

import java.util.Date;
import java.util.List;


/**
 * JCP APIs - Status 2.0
 */
public class Params20 {

    // Index

    public static class Index {

        public final String urlObjects = Paths20.FULL_PATH_STATUS_OBJS;
        public final String urlServices = Paths20.FULL_PATH_STATUS_SRVS;
        public final String urlUsers = Paths20.FULL_PATH_STATUS_USRS;
        public final String urlGateways = Paths20.FULL_PATH_STATUS_GWS;

    }


    // Objects

    public static class Objects {

        public long count;
        public long onlineCount;
        public long offlineCount;
        public long activeCount;
        public long inactiveCount;
        public long ownersCount;
        public List<RESTItemList> objectsList;

    }

    public static class Object {

        public String id;
        public String name;
        public String owner;
        public boolean online;
        public boolean active;
        public String version;
        public Date createdAt;
        public Date updatedAt;

    }

    // Services

    public static class Services {

        public long count;
        public long onlineCount;
        public long offlineCount;
        public long instancesCount;
        public long instancesOnlineCount;
        public long instancesOfflineCount;
        public List<RESTItemList> servicesList;

    }

    public static class Service {

        public String id;
        public String name;
        public Date createdAt;
        public Date updatedAt;

    }


    // Users

    public static class Users {

        public long count;
        public List<RESTItemList> usersList;

    }

    public static class User {

        public String id;
        public String name;
        public String first_name;
        public String second_name;
        public String email;
        public Date createdAt;
        public Date updatedAt;

    }


    // Gateways

    public static class Gateways {

        public long count;
        public long removed;
        public long total;
        public List<RESTItemList> gatewaysList;

    }

    public static class Gateway {

        public String id;
        public String name;
        public String gwUrl;
        public String apiUrl;
        public GWType type;
        public String version;
        public boolean connected;
        public int reconnectionAttempts;
        public Date createdAt;
        public Date updatedAt;
        public int currentClients;
        public int maxClients;
        public Date lastClientConnected;
        public Date lastClientDisconnected;

    }

}
