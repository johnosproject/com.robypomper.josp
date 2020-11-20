package com.robypomper.josp.jcp.paths.fe;

import com.robypomper.josp.jcp.info.JCPFEAPIsVersions;

public class APIJCPAPIMngm {
//@formatter:off

    // API info

    public static final String API_NAME = "CloudStatus";
    public static final String API_VER = JCPFEAPIsVersions.VER_JCPFE_APIs_1_0;
    public static final String API_PATH = JcpFEAPI.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupState {
        public final static String NAME = "JCP FrontEnd's Cloud Status APIs";
        public final static String DESCR = "";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_MNGM_GWS              = "mngm/gws";
    private static final String MTHD_MNGM_OBJS             = "mngm/objects";
    private static final String MTHD_MNGM_SRVS             = "mngm/services";
    private static final String MTHD_MNGM_USRS             = "mngm/users";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_MNGM_GWS          = API_PATH + "/" + API_VER + "/" + MTHD_MNGM_GWS;
    public static final String FULL_PATH_MNGM_OBJS         = API_PATH + "/" + API_VER + "/" + MTHD_MNGM_OBJS;
    public static final String FULL_PATH_MNGM_SRVS         = API_PATH + "/" + API_VER + "/" + MTHD_MNGM_SRVS;
    public static final String FULL_PATH_MNGM_USRS         = API_PATH + "/" + API_VER + "/" + MTHD_MNGM_USRS;

//@formatter:on
}
