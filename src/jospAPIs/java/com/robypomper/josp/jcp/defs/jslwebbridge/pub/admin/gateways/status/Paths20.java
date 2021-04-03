package com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.gateways.status;


/**
 * JCP JSL Web Bridge - Admin / Gateways / Status 2.0
 */
public class Paths20 extends com.robypomper.josp.defs.admin.gateways.status.Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to manage JCP as Admin";


    // API Paths

    //@formatter:off
    // JCP Gateways Status methods
    public static final String FULL_PATH_JSLWB_ADMIN_GATEWAYS_LIST                      = API_PATH + "/" + API_VER + "/" + MTHD_LIST;
    public static final String FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS                    = API_PATH + "/" + API_VER + "/" + PARAM_GW_SERVER + "/" + MTHD_STATUS;
    public static final String FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GWS                = API_PATH + "/" + API_VER + "/" + PARAM_GW_SERVER + "/" + MTHD_STATUS_GWS;
    public static final String FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GW                 = API_PATH + "/" + API_VER + "/" + PARAM_GW_SERVER + "/" + MTHD_STATUS_GW;
    public static final String FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GW_CLIENT          = API_PATH + "/" + API_VER + "/" + PARAM_GW_SERVER + "/" + MTHD_STATUS_GW_CLIENT;
    public static final String FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER             = API_PATH + "/" + API_VER + "/" + PARAM_GW_SERVER + "/" + MTHD_STATUS_BROKER;
    public static final String FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_OBJ         = API_PATH + "/" + API_VER + "/" + PARAM_GW_SERVER + "/" + MTHD_STATUS_BROKER_OBJ;
    public static final String FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_SRV         = API_PATH + "/" + API_VER + "/" + PARAM_GW_SERVER + "/" + MTHD_STATUS_BROKER_SRV;
    public static final String FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_OBJ_DB      = API_PATH + "/" + API_VER + "/" + PARAM_GW_SERVER + "/" + MTHD_STATUS_BROKER_OBJ_DB;
    //@formatter:on


    // API Paths composers

    //@formatter:off
    // JCP Gateways Status methods
    public static String FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS                   (String gwServerId){ return FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS                                                .replace(PARAM_GW_SERVER,gwServerId); }
    public static String FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GWS               (String gwServerId){ return FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GWS                                            .replace(PARAM_GW_SERVER,gwServerId); }
    public static String FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GW                (String gwServerId, String gwId){ return FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GW                                .replace(PARAM_GW_SERVER,gwServerId).replace(PARAM_GW,gwId); }
    public static String FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GW_CLIENT         (String gwServerId, String gwId, String gwClientId){ return FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GW_CLIENT      .replace(PARAM_GW_SERVER,gwServerId).replace(PARAM_GW,gwId).replace(PARAM_GW_CLIENT,gwClientId); }
    public static String FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER            (String gwServerId){ return FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_OBJ                         .replace(PARAM_GW_SERVER,gwServerId); }
    public static String FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_OBJ        (String gwServerId, String objId){ return FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_OBJ           .replace(PARAM_GW_SERVER,gwServerId).replace(PARAM_OBJ,objId); }
    public static String FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_SRV        (String gwServerId, String srvId){ return FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_SRV           .replace(PARAM_GW_SERVER,gwServerId).replace(PARAM_SRV,srvId); }
    public static String FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_OBJ_DB     (String gwServerId, String objId){ return FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_OBJ_DB        .replace(PARAM_GW_SERVER,gwServerId).replace(PARAM_OBJ,objId); }
    //@formatter:off


    // API Descriptions

    //@formatter:off
    // JCP Gateways Status methods
    public static final String DESCR_PATH_JSLWB_ADMIN_GATEWAYS_LIST                      = DESCR_PATH_JCP_GWS_STATUS_LIST;
    public static final String DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS                    = DESCR_PATH_JCP_GWS_STATUS;
    public static final String DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GWS                = DESCR_PATH_JCP_GWS_STATUS_GWS;
    public static final String DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GW                 = DESCR_PATH_JCP_GWS_STATUS_GW;
    public static final String DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GW_CLIENT          = DESCR_PATH_JCP_GWS_STATUS_GW_CLIENT;
    public static final String DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER             = DESCR_PATH_JCP_GWS_STATUS_BROKER;
    public static final String DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_OBJ         = DESCR_PATH_JCP_GWS_STATUS_BROKER_OBJ;
    public static final String DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_SRV         = DESCR_PATH_JCP_GWS_STATUS_BROKER_SRV;
    public static final String DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_OBJ_DB      = DESCR_PATH_JCP_GWS_STATUS_BROKER_OBJ_DB;
    //@formatter:on

}