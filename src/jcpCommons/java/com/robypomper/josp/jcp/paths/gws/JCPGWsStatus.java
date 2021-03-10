package com.robypomper.josp.jcp.paths.gws;

import com.robypomper.josp.info.JCPAPIsVersions;
import com.robypomper.josp.paths.jcp.JCPStatusAbs;

public class JCPGWsStatus {
//@formatter:off

    // API info

    public static final String API_NAME = JCPStatusAbs.API_NAME;
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JCPAPIsVersions.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupGWsStatus {
        public final static String NAME = "(internal) JCP GWs Status APIs";
        public final static String DESCR = "Return JCP GWs's instance status";
    }


    // API's methods

    // '{mthdName}
    public static final String MTHD_GWS_STATUS_CLI             = "gws/status/clients";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_GWS_STATUS_CLI             = API_PATH + "/" + API_VER + "/" + MTHD_GWS_STATUS_CLI;


    // API's descriptions

    public static final String DESCR_PATH_GWS_STATUS_CLI    = "Return JCP GWs clients";

//@formatter:on
}
