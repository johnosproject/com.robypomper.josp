package com.robypomper.josp.jcp.apis.paths;

public class APIPermissions {
//@formatter:off

    private static final String version = "ver";


    public static final String PATH_BASE    = "/permissions";

    public static final String FULL_PATH_BASE   = JcpAPI.PATH_API_BASE + PATH_BASE;

    public static final String URL_PATH_BASE    = JcpAPI.URL_PATH_API_BASE + PATH_BASE;


    public static final String PATH_OBJGENERATE     = "/" + version + "/obj";
    public static final String PATH_OBJMERGE        = "/" + version + "/obj";

    public static final String FULL_PATH_OBJGENERATE    = FULL_PATH_BASE + PATH_OBJGENERATE;
    public static final String FULL_PATH_OBJMERGE       = FULL_PATH_BASE + PATH_OBJMERGE;

    public static final String URL_PATH_OBJGENERATE     = URL_PATH_BASE + PATH_OBJGENERATE;
    public static final String URL_PATH_OBJMERGE        = URL_PATH_BASE + PATH_OBJMERGE;

//@formatter:on
}
