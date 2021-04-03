package com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.frontend.status;


/**
 * JCP JSL Web Bridge - Admin / Front End / Status 2.0
 */
public class Paths20 extends com.robypomper.josp.defs.admin.frontend.status.Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to manage JCP as Admin";


    // API Paths

    //@formatter:off
    // JCP Front End Status methods
    public static final String FULL_PATH_JSLWB_ADMIN_FRONTEND_STATUS        = API_PATH + "/" + API_VER + "/" + MTHD_STATUS;
    //@formatter:on


    // API Descriptions

    //@formatter:off
    // JCP Front End Status methods
    public static final String DESCR_PATH_JSLWB_ADMIN_FRONTEND_STATUS        = DESCR_PATH_JCP_FE_STATUS;
    //@formatter:on

}
