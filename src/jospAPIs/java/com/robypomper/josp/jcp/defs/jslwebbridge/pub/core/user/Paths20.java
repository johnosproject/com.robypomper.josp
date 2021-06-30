package com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.user;


/**
 * JCP JSL Web Bridge - User 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to query user's info and perform login/logout";


    // API Methods

    //@formatter:off
    //
    private static final String MTHD_DETAILS        = "";
    private static final String MTHD_LOGIN          = "login/";
    private static final String MTHD_LOGIN_CALLBACK = "login/code/";
    private static final String MTHD_LOGIN_EXT      = "login_ext/";
    private static final String MTHD_LOGOUT         = "logout/";
    private static final String MTHD_REGISTRATION   = "registration/";
    //@formatter:on


    // API Paths

    //@formatter:off
    //
    public static final String FULL_PATH_DETAILS        = API_PATH + "/" + API_VER + "/" + MTHD_DETAILS;
    public static final String FULL_PATH_LOGIN          = API_PATH + "/" + API_VER + "/" + MTHD_LOGIN;
    public static final String FULL_PATH_LOGIN_CALLBACK = API_PATH + "/" + API_VER + "/" + MTHD_LOGIN_CALLBACK;
    public static final String FULL_PATH_LOGIN_EXT      = API_PATH + "/" + API_VER + "/" + MTHD_LOGIN_EXT;
    public static final String FULL_PATH_LOGOUT         = API_PATH + "/" + API_VER + "/" + MTHD_LOGOUT;
    public static final String FULL_PATH_REGISTRATION   = API_PATH + "/" + API_VER + "/" + MTHD_REGISTRATION;
    //@formatter:on


    // API Descriptions

    //@formatter:off
    //
    public static final String DESCR_PATH_DETAILS           = "Return user's details";
    public static final String DESCR_PATH_LOGIN             = "Redirect (or return) to the auth service login url";
    public static final String DESCR_PATH_LOGIN_CALLBACK    = "Callback method from auth service's login";
    public static final String DESCR_PATH_LOGIN_EXT         = "Redirect (or return) to the auth service login url for external token management";
    public static final String DESCR_PATH_LOGOUT            = "Redirect (or return) to the auth service logout url";
    public static final String DESCR_PATH_REGISTRATION      = "Redirect (or return) to the auth service registration url";
    //@formatter:on

}
