package com.robypomper.josp.jcp.paths.apis;

import com.robypomper.josp.info.JCPAPIsVersions;

public class JCPAPIsGWRegistration {
//@formatter:off

    // API info

    public static final String API_NAME = "JCP/GWsReg";
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JCPAPIsVersions.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupRegistration {
        public final static String NAME = "(internal) JCP Gateways registration";
        public final static String DESCR = "JCP GWs register themself to JCP APIs";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_STARTUP    = "startup";
    private static final String MTHD_STATUS     = "status";
    private static final String MTHD_SHUTDOWN   = "shutdown";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_STARTUP    = API_PATH + "/" + API_VER + "/" + MTHD_STARTUP;
    public static final String FULL_PATH_STATUS     = API_PATH + "/" + API_VER + "/" + MTHD_STATUS;
    public static final String FULL_PATH_SHUTDOWN   = API_PATH + "/" + API_VER + "/" + MTHD_SHUTDOWN;


    public static final String HEADER_JCPGWID = "JCP-GW-ID";


    // API's descriptions

    public static final String DESCR_PATH_STARTUP   = "Register JCP GWs startup";
    public static final String DESCR_PATH_STATUS    = "Update JCP GWs status";
    public static final String DESCR_PATH_SHUTDOWN  = "Register JCP GWs shutdown";

//@formatter:on
}
