package com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.apis.status;


/**
 * JCP JSL Web Bridge - Admin / APIs / Status 2.0
 */
public class Paths20 extends com.robypomper.josp.defs.admin.apis.status.Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to manage JCP as Admin";


    // API Paths

    //@formatter:off
    // JCP APIs Status methods
    public static final String FULL_PATH_JSLWB_ADMIN_APIS_STATUS            = API_PATH + "/" + API_VER + "/" + MTHD_STATUS;
    public static final String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_OBJS       = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_OBJS;
    public static final String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_OBJ        = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_OBJ;
    public static final String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_SRVS       = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_SRVS;
    public static final String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_SRV        = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_SRV;
    public static final String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_USRS       = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_USRS;
    public static final String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_USR        = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_USR;
    public static final String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_GWS        = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_GWS;
    public static final String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_GW         = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_GW;
    //@formatter:on


    // API Paths composers

    //@formatter:off
    // JCP APIs Status methods
    public static String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_OBJ      (String objId){ return FULL_PATH_JSLWB_ADMIN_APIS_STATUS_OBJ        .replace(PARAM_OBJ,objId); }
    public static String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_SRV      (String srvId){ return FULL_PATH_JSLWB_ADMIN_APIS_STATUS_SRV        .replace(PARAM_SRV,srvId); }
    public static String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_USR      (String usrId){ return FULL_PATH_JSLWB_ADMIN_APIS_STATUS_USR        .replace(PARAM_USR,usrId); }
    public static String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_GW       (String gwId){ return FULL_PATH_JSLWB_ADMIN_APIS_STATUS_GW          .replace(PARAM_USR,gwId); }
    //@formatter:off


    // API Descriptions

    //@formatter:off
    // JCP APIs Status methods
    public static final String DESCR_PATH_JSLWB_ADMIN_APIS_STATUS            = DESCR_PATH_JCP_APIS_STATUS;
    public static final String DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_OBJS       = DESCR_PATH_JCP_APIS_STATUS_OBJS;
    public static final String DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_OBJ        = DESCR_PATH_JCP_APIS_STATUS_OBJ;
    public static final String DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_SRVS       = DESCR_PATH_JCP_APIS_STATUS_SRVS;
    public static final String DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_SRV        = DESCR_PATH_JCP_APIS_STATUS_SRV;
    public static final String DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_USRS       = DESCR_PATH_JCP_APIS_STATUS_USRS;
    public static final String DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_USR        = DESCR_PATH_JCP_APIS_STATUS_USR;
    public static final String DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_GWS        = DESCR_PATH_JCP_APIS_STATUS_GWS;
    public static final String DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_GW         = DESCR_PATH_JCP_APIS_STATUS_GW;
    //@formatter:on

}
