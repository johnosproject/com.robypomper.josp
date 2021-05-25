package com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.apis.status;


/**
 * JCP JSL Web Bridge - Admin / APIs / Status 2.0
 */
public class Params20 extends com.robypomper.josp.defs.admin.apis.status.Params20 {

    // JCP APIs Status methods

    public static class Index {

        public final String urlObjects = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_STATUS_OBJS;
        public final String urlServices = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_STATUS_SRVS;
        public final String urlUsers = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_STATUS_USRS;
        public final String urlGateways = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_STATUS_GWS;

    }

}
