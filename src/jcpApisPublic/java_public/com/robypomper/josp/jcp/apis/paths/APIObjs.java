package com.robypomper.josp.jcp.apis.paths;

public class APIObjs {
//@formatter:off

    private static final String version = "ver";


    public static final String PATH_BASE    = "/object";

    public static final String FULL_PATH_BASE   = JcpAPI.PATH_API_BASE + PATH_BASE;

    public static final String URL_PATH_BASE    = JcpAPI.URL_PATH_API_BASE + PATH_BASE;


    public static final String PATH_GENERATEID      = "/" + version + "/generate_id";
    public static final String PATH_REGENERATEID      = "/" + version + "/regenerate_id";
    public static final String PATH_REGISTER        = "/" + version + "/register";

    public static final String FULL_PATH_GENERATEID     = FULL_PATH_BASE + PATH_GENERATEID;
    public static final String FULL_PATH_REGENERATEID     = FULL_PATH_BASE + PATH_REGENERATEID;
    public static final String FULL_PATH_REGISTER       = FULL_PATH_BASE + PATH_REGISTER;

    public static final String URL_PATH_GENERATEID      = URL_PATH_BASE + PATH_GENERATEID;
    public static final String URL_PATH_REGENERATEID      = URL_PATH_BASE + PATH_REGENERATEID;
    public static final String URL_PATH_REGISTER        = URL_PATH_BASE + PATH_REGISTER;


    public static final String HEADER_OBJID = "JOSP-Obj-ID";

//@formatter:on
}
