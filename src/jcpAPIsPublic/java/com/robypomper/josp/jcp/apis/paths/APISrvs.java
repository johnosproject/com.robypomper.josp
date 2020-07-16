package com.robypomper.josp.jcp.apis.paths;

import com.robypomper.josp.jcp.info.JCPAPIsVersions;

public class APISrvs {
//@formatter:off

    // API info

    public static final String API_NAME = "Service";
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JcpAPI.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupInfo {
        public final static String NAME = "Service's info";
        public final static String DESCR = "Get service info...";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_REGISTER        = "service";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_REGISTER   = API_PATH + "/" + API_VER + "/" + MTHD_REGISTER;


    public static final String HEADER_SRVID = "JOSP-Srv-ID";
    public static final String HEADER_USRID = "JOSP-Usr-ID";

//@formatter:on
}
