package com.robypomper.josp.jcpfe.apis.paths;

import com.robypomper.josp.jcpfe.info.JCPFEAPIsVersions;

public class APIJCPFEUser {
//@formatter:off

    // API info

    public static final String API_NAME = "user";
    public static final String API_VER = JCPFEAPIsVersions.VER_JCPFE_APIs_1_0;
    public static final String API_PATH = JcpFEAPI.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupUser {
        public final static String NAME = "JCP FrontEnd Current user";
        public final static String DESCR = "";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_DETAILS    = "";
    private static final String MTHD_LOGIN    = "login/";
    private static final String MTHD_LOGIN_CALLBACK    = "login/code/";
    private static final String MTHD_LOGOUT    = "logout/";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_DETAILS        = API_PATH + "/" + API_VER + "/" + MTHD_DETAILS;
    public static final String FULL_PATH_LOGIN          = API_PATH + "/" + API_VER + "/" + MTHD_LOGIN;
    public static final String FULL_PATH_LOGIN_CALLBACK = API_PATH + "/" + API_VER + "/" + MTHD_LOGIN_CALLBACK;
    public static final String FULL_PATH_LOGOUT         = API_PATH + "/" + API_VER + "/" + MTHD_LOGOUT;

//@formatter:on
}
