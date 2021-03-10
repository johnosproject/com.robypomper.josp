package com.robypomper.josp.jcp.paths.jslwb;

import com.robypomper.josp.jcp.info.JCPJSLWBVersions;

public class APIJSLWBInit {
//@formatter:off

    // API info

    public static final String API_NAME = "JSL/Init";
    public static final String API_VER = JCPJSLWBVersions.VER_JCPJSLWB_APIs_1_0;
    public static final String API_PATH = JCPJSLWBVersions.PATH_JSLWB_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupInit {
        public final static String NAME = "JSL instance initializers";
        public final static String DESCR = "Initializers for JSL instances in JCP JSL WebBridge";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_JSL_STATUS = "sessionStatus";
    private static final String MTHD_INIT_JSL   = "jsl";
    private static final String MTHD_INIT_SSE   = "sse";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_JSL_STATUS = API_PATH + "/" + API_VER + "/" + MTHD_JSL_STATUS;
    public static final String FULL_PATH_INIT_JSL   = API_PATH + "/" + API_VER + "/" + MTHD_INIT_JSL;
    public static final String FULL_PATH_INIT_SSE   = API_PATH + "/" + API_VER + "/" + MTHD_INIT_SSE;


    // API's descriptions

    public static final String DESCR_PATH_JSL_STATUS    = "Return the session id and JSL instance status";
    public static final String DESCR_PATH_INIT_JSL      = "Initialize new JSL Instance for current session";
    public static final String DESCR_PATH_INIT_SSE      = "Create and return new SSE, if given client* params then it also initialize new JSL Instance for current session";

//@formatter:on
}
