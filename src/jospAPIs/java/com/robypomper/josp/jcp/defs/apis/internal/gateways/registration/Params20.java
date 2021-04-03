package com.robypomper.josp.jcp.defs.apis.internal.gateways.registration;

import com.robypomper.josp.types.josp.gw.GWType;

import java.util.Date;


/**
 * JCP APIs - Gateways / Registration 2.0
 */
public class Params20 {

    // Registration

    public static class JCPGWsStartup {

        public GWType type;
        public String gwAddr;
        public int gwPort;
        public String gwAPIsAddr;
        public int gwAPIsPort;
        public int clientsMax;
        public String version;

    }

    public static class JCPGWsStatus {

        public int clients;
        public int clientsMax;
        public Date lastClientConnectedAt;
        public Date lastClientDisconnectedAt;

    }

}
