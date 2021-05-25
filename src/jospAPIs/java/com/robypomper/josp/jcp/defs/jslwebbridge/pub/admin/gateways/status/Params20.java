package com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.gateways.status;


/**
 * JCP JSL Web Bridge - Admin / Gateways / Status 2.0
 */
public class Params20 extends com.robypomper.josp.defs.admin.gateways.status.Params20 {

    // JCP Gateways Status methods

    public static class Index {

        public final String urlGateways;
        public final String urlBroker;

        public Index(String gwServerId) {
            this.urlGateways = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GWS(gwServerId);
            this.urlBroker = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER(gwServerId);
        }

    }

}
