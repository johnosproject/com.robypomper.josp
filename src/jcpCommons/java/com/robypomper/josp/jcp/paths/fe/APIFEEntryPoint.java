package com.robypomper.josp.jcp.paths.fe;

import com.robypomper.josp.jcp.info.JCPJSLWBVersions;

public class APIFEEntryPoint {
//@formatter:off

    // API info

    public static final String API_NAME = "FE/EntryPoint";
    public static final String API_VER = JCPJSLWBVersions.VER_JCPJSLWB_APIs_1_0;
    public static final String API_PATH = JCPJSLWBVersions.PATH_JSLWB_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupEntryPoint {
        public final static String NAME = "JCP Front End Entry Point";
        public final static String DESCR = "Methods to query Front End entry point";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_ENTRYPOINT = "/entrypoint";
    private static final String MTHD_INIT_JSL_SESSION = "/jslwbsession";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_ENTRYPOINT         = API_PATH + "/" + API_VER + "/" + MTHD_ENTRYPOINT;
    public static final String FULL_PATH_INIT_JSL_SESSION   = API_PATH + "/" + API_VER + "/" + MTHD_INIT_JSL_SESSION;

    // API's descriptions

    public static final String DESCR_PATH_ENTRYPOINT        = "Return the JSL Web Bridge entry point";
    public static final String DESCR_PATH_INIT_JSL_SESSION  = "Require to JSL Web Bridge to initialize a JSL instance";

//@formatter:on
}
