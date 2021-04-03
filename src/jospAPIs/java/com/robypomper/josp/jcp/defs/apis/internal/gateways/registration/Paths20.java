package com.robypomper.josp.jcp.defs.apis.internal.gateways.registration;


/**
 * JCP APIs - Gateways / Registration 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "JCP GWs register themself to JCP APIs";


    // API Methods

    //@formatter:off
    // Registration methods
    private static final String MTHD_STARTUP    = "startup";
    private static final String MTHD_STATUS     = "status";
    private static final String MTHD_SHUTDOWN   = "shutdown";
    //@formatter:on


    // API Paths

    //@formatter:off
    // Registration methods
    public static final String FULL_PATH_STARTUP    = API_PATH + "/" + API_VER + "/" + MTHD_STARTUP;
    public static final String FULL_PATH_STATUS     = API_PATH + "/" + API_VER + "/" + MTHD_STATUS;
    public static final String FULL_PATH_SHUTDOWN   = API_PATH + "/" + API_VER + "/" + MTHD_SHUTDOWN;
    //@formatter:on


    // API Descriptions

    //@formatter:off
    // Registration methods
    public static final String DESCR_PATH_STARTUP   = "Register JCP GWs startup";
    public static final String DESCR_PATH_STATUS    = "Update JCP GWs status";
    public static final String DESCR_PATH_SHUTDOWN  = "Register JCP GWs shutdown";
    //@formatter:on

}
