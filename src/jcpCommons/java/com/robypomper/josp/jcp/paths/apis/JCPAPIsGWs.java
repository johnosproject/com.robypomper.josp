package com.robypomper.josp.jcp.paths.apis;

import com.robypomper.josp.info.JCPAPIsVersions;
import com.robypomper.josp.jcp.info.JCPFEVersions;

public class JCPAPIsGWs {
//@formatter:off

    // API info

    public static final String API_NAME = "jcp/gws";
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JCPAPIsVersions.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupRegistration {
        public final static String NAME = "JCP (internal) Gateways registration";
        public final static String DESCR = "";
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

//@formatter:on
}
