package com.robypomper.josp.jcp.paths.gws;

import com.robypomper.josp.info.JCPAPIsVersions;

/**
 * Redirect from {@link com.robypomper.josp.paths.APIGWs}.
 */
public class JCPGWsAccessInfo {
//@formatter:off

    // API info

    public static final String API_NAME = "JCP/GWs";
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JCPAPIsVersions.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupGWs {
        public final static String NAME = "(internal) JCP Gateways access info";
        public final static String DESCR = "JCP APIs get JCP GW's access info";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_O2S_ACCESS      = "o2s/access";
    private static final String MTHD_S2O_ACCESS      = "s2o/access";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_O2S_ACCESS     = API_PATH + "/" + API_VER + "/" + MTHD_O2S_ACCESS;
    public static final String FULL_PATH_S2O_ACCESS     = API_PATH + "/" + API_VER + "/" + MTHD_S2O_ACCESS;


    // API's descriptions

    public static final String DESCR_PATH_O2S_ACCESS    = "Set object's certificate and return JCP GW Obj2Srv's access info";
    public static final String DESCR_PATH_S2O_ACCESS    = "Set service's certificate and return JCP GW Srv2Obj's access info";

//@formatter:on
}
