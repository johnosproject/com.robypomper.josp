package com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.jslwebbridge.status;


/**
 * JCP JSL Web Bridge - Admin / JSL Web Bridge / Status 2.0
 */
public class Paths20 extends com.robypomper.josp.defs.admin.jslwebbridge.status.Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to manage JCP as Admin";


    // API Paths

    //@formatter:off
    // JCP JSL Web Bridge Status methods
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_STATUS               = API_PATH + "/" + API_VER + "/" + MTHD_STATUS;
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_STATUS_SESSIONS      = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_SESSIONS;
    public static final String FULL_PATH_JSLWB_ADMIN_JSLWB_STATUS_SESSION       = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_SESSION;
    //@formatter:on


    // API Paths composers

    //@formatter:off
    // JCP JSL Web Bridge Status methods
    public static String FULL_PATH_JSLWB_ADMIN_JSLWB_STATUS_SESSION         (String sessionId){ return FULL_PATH_JSLWB_ADMIN_JSLWB_STATUS_SESSION      .replace(PARAM_URL_SESSION,sessionId); }
    //@formatter:off


    // API Descriptions

    //@formatter:off
    // JCP JSL Web Bridge Status methods
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_STATUS               = DESCR_PATH_JCP_JSLWB_STATUS;
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_STATUS_SESSIONS      = DESCR_PATH_JCP_JSLWB_STATUS_SESSIONS;
    public static final String DESCR_PATH_JSLWB_ADMIN_JSLWB_STATUS_SESSION       = DESCR_PATH_JCP_JSLWB_STATUS_SESSION;
    //@formatter:on

}
