package com.robypomper.josp.jcp.apis.paths;

import com.robypomper.josp.jcp.info.JCPAPIsVersions;

public class APIJOSPGWs {
//@formatter:off

    // API info

    public static final String API_NAME = "Gateways";
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JcpAPI.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupGWs {
        public final static String NAME = "All";
        public final static String DESCR = "\"Object2Service and Service2Object Gateway's APIs";
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
