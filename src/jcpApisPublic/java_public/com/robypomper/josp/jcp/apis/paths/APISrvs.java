package com.robypomper.josp.jcp.apis.paths;

public class APISrvs {

    //@formatter:off
    private static final String version = "ver";


    public static final String PATH_BASE    = "/service";

    public static final String FULL_PATH_BASE   = JcpAPI.PATH_API_BASE + PATH_BASE;

    public static final String URL_PATH_BASE    = JcpAPI.URL_PATH_API_BASE + PATH_BASE;

    public static final String PATH_USERNAME    = "/" + version + "/service";

    public static final String FULL_PATH_USERNAME        = FULL_PATH_BASE + PATH_USERNAME;

   public static final String URL_PATH_USERNAME         = URL_PATH_BASE + PATH_USERNAME;


    public static final String HEADER_SRVID = "JOSP-Srv-ID";
    public static final String HEADER_USRID = "JOSP-Usr-ID";
    //@formatter:on

}
