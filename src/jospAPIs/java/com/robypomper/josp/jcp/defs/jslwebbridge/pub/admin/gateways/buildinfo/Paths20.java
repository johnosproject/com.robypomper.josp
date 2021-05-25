package com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.gateways.buildinfo;


/**
 * JCP JSL Web Bridge - Admin / Gateways / Build Info 2.0
 */
public class Paths20 extends com.robypomper.josp.defs.admin.gateways.buildinfo.Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to manage JCP as Admin";


    // API Paths

    //@formatter:off
    // JCP Gateways Build Info methods
    public static final String FULL_PATH_JSLWB_ADMIN_GATEWAYS_LIST          = API_PATH + "/" + API_VER + "/" + MTHD_LIST;
    public static final String FULL_PATH_JSLWB_ADMIN_GATEWAYS_BUILDINFO     = API_PATH + "/" + API_VER + "/" + PARAM_URL_GW_SERVER + "/" + MTHD_BUILDINFO;
    
    //@formatter:on


    // API Paths composers

    //@formatter:off
    // JCP Gateways Build Info methods
    public static String FULL_PATH_JSLWB_ADMIN_GATEWAYS_BUILDINFO            (String gwServerId){ return FULL_PATH_JSLWB_ADMIN_GATEWAYS_BUILDINFO             .replace(PARAM_URL_GW_SERVER,gwServerId); }
    //@formatter:off


    // API Descriptions

    //@formatter:off
    // JCP Gateways Build Info methods
    public static final String DESCR_PATH_JSLWB_ADMIN_GATEWAYS_BUILDINFO_LIST       = DESCR_PATH_JCP_GWS_BUILDINFO_LIST;
    public static final String DESCR_PATH_JSLWB_ADMIN_GATEWAYS_BUILDINFO            = DESCR_PATH_JCP_GWS_BUILDINFO;
    
    //@formatter:on

}
