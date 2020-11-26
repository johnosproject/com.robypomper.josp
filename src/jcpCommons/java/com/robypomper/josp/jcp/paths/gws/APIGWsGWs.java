package com.robypomper.josp.jcp.paths.gws;

import com.robypomper.josp.info.JCPAPIsVersions;

/**
 * Redirect from {@link com.robypomper.josp.paths.APIGWs}.
 */
public class APIGWsGWs {
//@formatter:off

    // API info

    public static final String API_NAME = "gws/gws";
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JCPAPIsVersions.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupGWs {
        public final static String NAME = "All (redirect from JCP APIs)";
        public final static String DESCR = "Object2Service and Service2Object Gateway's APIs redirected from JCP APIs";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_O2S_ACCESS      = "o2s/access";
    private static final String MTHD_S2O_ACCESS      = "s2o/access";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_O2S_ACCESS     = API_PATH + "/" + API_VER + "/" + MTHD_O2S_ACCESS;
    public static final String FULL_PATH_S2O_ACCESS     = API_PATH + "/" + API_VER + "/" + MTHD_S2O_ACCESS;

//@formatter:on
}
