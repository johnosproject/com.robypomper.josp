package com.robypomper.josp.jcp.apis.paths;

import com.robypomper.josp.jcp.info.JCPAPIsVersions;

public class ExampleAPIs {

    // API info

    public static final String API_NAME = "examples";
    public static final String API_VER = JCPAPIsVersions.VER_TEST_2_0;
    public static final String API_PATH = JcpAPI.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupMethods {
        public final static String NAME = "Methods";
        public final static String DESCR = "Examples...";
    }

    public static class SubGroupDB {
        public final static String NAME = "Database";
        public final static String DESCR = "Examples...";
    }

    public static class SubGroupAuthentication {
        public final static String NAME = "Authentication";
        public final static String DESCR = "Examples...";
    }

    public static class SubGroupAuthorization {
        public final static String NAME = "Authorization";
        public final static String DESCR = "Examples...";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_METHODS_GENERIC        = SubGroupMethods.NAME + "";
    private static final String MTHD_DB_DB                  = SubGroupDB.NAME + "/db";
    private static final String MTHD_AUTHENTICATION_GENERIC = SubGroupAuthentication.NAME + "";
    private static final String MTHD_AUTHORIZATION_GENERIC  = SubGroupAuthorization.NAME + "";

    // '/apis/jospgws/{apiVersion}/{mthdName}
    public static final String FULL_METHODS_GENERIC         = API_PATH + "/" + API_VER + "/" + MTHD_METHODS_GENERIC;
    public static final String FULL_DB_DB                   = API_PATH + "/" + API_VER + "/" + MTHD_DB_DB;
    public static final String FULL_AUTHENTICATION_GENERIC  = API_PATH + "/" + API_VER + "/" + MTHD_AUTHENTICATION_GENERIC;
    public static final String FULL_AUTHORIZATION_GENERIC   = API_PATH + "/" + API_VER + "/" + MTHD_AUTHORIZATION_GENERIC;

}
