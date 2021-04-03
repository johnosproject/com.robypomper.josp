package com.robypomper.josp.defs.admin.gateways.status;


import com.robypomper.josp.types.RESTItemList;

import java.util.List;


/**
 * JOSP Admin - Gateways / Status 2.0
 */
public class Params20 extends com.robypomper.josp.jcp.defs.gateways.internal.status.Params20 {

    // List

    public static class GatewaysServers {

        public List<RESTItemList> serverList;

    }


    // Index

    public static class Index {

        public final String urlGateways;
        public final String urlBroker;

        public Index(String gwServerId) {
            this.urlGateways = Paths20.FULL_PATH_JCP_GWS_STATUS_GWS(gwServerId);
            this.urlBroker = Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER(gwServerId);
        }

    }

}
