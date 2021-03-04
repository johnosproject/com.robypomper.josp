package com.robypomper.josp.jcp.paths.jslwb;

import com.robypomper.josp.jcp.info.JCPJSLWBVersions;

public class APIJSLWBUsr {
//@formatter:off

    // API info

    public static final String API_NAME = "user";
    public static final String API_VER = JCPJSLWBVersions.VER_JCPJSLWB_APIs_1_0;
    public static final String API_PATH = JCPJSLWBVersions.PATH_JSLWB_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupUser {
        public final static String NAME = "User's Manager";
        public final static String DESCR = "Methods to query user's info and perform login/logout";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_DETAILS        = "";
    private static final String MTHD_LOGIN          = "login/";
    private static final String MTHD_LOGIN_CALLBACK = "login/code/";
    private static final String MTHD_LOGOUT         = "logout/";
    private static final String MTHD_REGISTRATION   = "registration/";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_DETAILS        = API_PATH + "/" + API_VER + "/" + MTHD_DETAILS;
    public static final String FULL_PATH_LOGIN          = API_PATH + "/" + API_VER + "/" + MTHD_LOGIN;
    public static final String FULL_PATH_LOGIN_CALLBACK = API_PATH + "/" + API_VER + "/" + MTHD_LOGIN_CALLBACK;
    public static final String FULL_PATH_LOGOUT         = API_PATH + "/" + API_VER + "/" + MTHD_LOGOUT;
    public static final String FULL_PATH_REGISTRATION   = API_PATH + "/" + API_VER + "/" + MTHD_REGISTRATION;


    // API's descriptions

    public static final String DESCR_PATH_DETAILS           = "Return user's details";
    public static final String DESCR_PATH_LOGIN             = "Redirect (or return) to the auth service login url";
    public static final String DESCR_PATH_LOGIN_CALLBACK    = "Callback method from auth service's login";
    public static final String DESCR_PATH_LOGOUT            = "Redirect (or return) to the auth service logout url";
    public static final String DESCR_PATH_REGISTRATION      = "Redirect (or return) to the auth service registration url";

//@formatter:on
}
