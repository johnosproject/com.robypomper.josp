package com.robypomper.josp.jcp.apis.paths;

import com.robypomper.josp.jcp.info.JCPAPIsVersions;

public class APIUsrs {
//@formatter:off

    // API info

    public static final String API_NAME = "User";
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JcpAPI.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupInfo {
        public final static String NAME = "User's info";
        public final static String DESCR = "Get user info...";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_USERNAME    = "";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_USERNAME   = API_PATH + "/" + API_VER + "/" + MTHD_USERNAME;

//@formatter:on
}
