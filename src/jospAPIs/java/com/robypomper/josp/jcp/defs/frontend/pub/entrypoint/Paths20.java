package com.robypomper.josp.jcp.defs.frontend.pub.entrypoint;


/**
 * JCP Front End API - Entry Point 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to query Front End entry point";


    // API Methods

    //@formatter:off
    //
    private static final String MTHD_ENTRYPOINT = "/entrypoint";
    private static final String MTHD_INIT_JSL_SESSION = "/jslwbsession";
    //@formatter:on


    // API Paths

    //@formatter:off
    //
    public static final String FULL_PATH_ENTRYPOINT         = API_PATH + "/" + API_VER + "/" + MTHD_ENTRYPOINT;
    public static final String FULL_PATH_INIT_JSL_SESSION   = API_PATH + "/" + API_VER + "/" + MTHD_INIT_JSL_SESSION;
    //@formatter:on


    // API Descriptions

    //@formatter:off
    //
    public static final String DESCR_PATH_ENTRYPOINT        = "Return the JSL Web Bridge entry point";
    public static final String DESCR_PATH_INIT_JSL_SESSION  = "Require to JSL Web Bridge to initialize a JSL instance";
    //@formatter:on

}
