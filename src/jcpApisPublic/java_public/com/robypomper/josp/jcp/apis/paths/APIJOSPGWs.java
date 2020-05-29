package com.robypomper.josp.jcp.apis.paths;

public class APIJOSPGWs {
//@formatter:off

    private static final String version = "ver";


    public static final String PATH_BASE    = "/jospgw";

    public static final String FULL_PATH_BASE   = JcpAPI.PATH_API_BASE + PATH_BASE;

    public static final String URL_PATH_BASE    = JcpAPI.URL_PATH_API_BASE + PATH_BASE;


    public static final String PATH_O2S_ACCESS      = "/" + version + "/o2s/access";
    public static final String PATH_S2O_ACCESS      = "/" + version + "/s2o/access";

    public static final String FULL_PATH_O2S_ACCESS     = FULL_PATH_BASE + PATH_O2S_ACCESS;
    public static final String FULL_PATH_S2O_ACCESS     = FULL_PATH_BASE + PATH_S2O_ACCESS;

    public static final String URL_PATH_O2S_ACCESS      = URL_PATH_BASE + PATH_O2S_ACCESS;
    public static final String URL_PATH_S2O_ACCESS      = URL_PATH_BASE + PATH_S2O_ACCESS;

//@formatter:on
}
