package com.robypomper.josp.jcp.apis.paths;

import com.robypomper.josp.jcp.info.JCPAPIsVersions;

public class APIJCPStatus {
//@formatter:off

    // API info

    public static final String API_NAME = "Status";
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JcpAPI.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupStatus {
        public final static String NAME = "JCP Status";
        public final static String DESCR = "";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_STATUS_PUBLIC    = "/";
    private static final String MTHD_STATUS_FULL      = "full/";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_STATUS_PUBLIC   = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_PUBLIC;
    public static final String FULL_PATH_STATUS_FULL     = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_FULL;

//@formatter:on
}
