package com.robypomper.josp.defs.admin.apis.status;


/**
 * JOSP Admin - APIs / Status 2.0
 */
public class Params20 extends com.robypomper.josp.jcp.defs.apis.internal.status.Params20 {

    // Index

    public static class Index {

        public final String urlObjects = Paths20.FULL_PATH_JCP_APIS_STATUS_OBJS;
        public final String urlServices = Paths20.FULL_PATH_JCP_APIS_STATUS_SRVS;
        public final String urlUsers = Paths20.FULL_PATH_JCP_APIS_STATUS_USRS;
        public final String urlGateways = Paths20.FULL_PATH_JCP_APIS_STATUS_GWS;

    }

}
