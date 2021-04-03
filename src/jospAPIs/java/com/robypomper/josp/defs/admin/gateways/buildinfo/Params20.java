package com.robypomper.josp.defs.admin.gateways.buildinfo;

import com.robypomper.josp.types.RESTItemList;

import java.util.List;


/**
 * JOSP Admin - Gateways / Build Info 2.0
 */
public class Params20 extends com.robypomper.josp.jcp.defs.base.internal.status.buildinfo.Params20 {

    // List

    public static class GatewaysServers {

        public List<RESTItemList> serverList;

    }

}
