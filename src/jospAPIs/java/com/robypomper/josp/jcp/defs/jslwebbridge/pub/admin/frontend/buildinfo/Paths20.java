package com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.frontend.buildinfo;


/**
 * JCP JSL Web Bridge - Admin / Front End / Build Info 2.0
 */
public class Paths20 extends com.robypomper.josp.defs.admin.frontend.buildinfo.Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to manage JCP as Admin";


    // API Paths

    //@formatter:off
    // JCP Front End Build Info methods
    public static final String FULL_PATH_JSLWB_ADMIN_FRONTEND_BUILDINFO     = API_PATH + "/" + API_VER + "/" + MTHD_BUILDINFO;
    //@formatter:on


    // API Descriptions

    //@formatter:off
    // JCP Front End Build Info methods
    public static final String DESCR_PATH_JSLWB_ADMIN_FRONTEND_BUILDINFO     = DESCR_PATH_JCP_FE_BUILDINFO;
    //@formatter:on

}
