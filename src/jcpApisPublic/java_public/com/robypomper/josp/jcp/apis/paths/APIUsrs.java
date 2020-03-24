package com.robypomper.josp.jcp.apis.paths;

public class APIUsrs {
//@formatter:off

    // Class constants

    private static final String version = "ver";


    // Base

    public static final String PATH_BASE    = "/user";

    public static final String FULL_PATH_BASE   = JcpAPI.PATH_API_BASE + PATH_BASE;

    public static final String URL_PATH_BASE    = JcpAPI.URL_PATH_API_BASE + PATH_BASE;


    // User's info

    public static final String PATH_USERNAME    = "/" + version;// + "/";

    public static final String FULL_PATH_USERNAME   = FULL_PATH_BASE + PATH_USERNAME;

    public static final String URL_PATH_USERNAME    = URL_PATH_BASE + PATH_USERNAME;

//@formatter:on
}
