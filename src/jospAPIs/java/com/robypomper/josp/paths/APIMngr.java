package com.robypomper.josp.paths;

import com.robypomper.josp.info.JCPAPIsVersions;

public class APIMngr {
//@formatter:off

    // API info

    public static final String API_NAME = "Manager";
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JcpAPI.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupGWs {
        public final static String NAME = "JOSP GWs Mngm APIs";
        public final static String DESCR = "";
    }

    public static class SubGroupObjs {
        public final static String NAME = "JCP APIs's Objects Mngm APIs";
        public final static String DESCR = "";
    }

    public static class SubGroupSrvs {
        public final static String NAME = "JCP APIs's Services Mngm APIs";
        public final static String DESCR = "";
    }

    public static class SubGroupUsrs {
        public final static String NAME = "JCP APIs's Users Mngm APIs";
        public final static String DESCR = "";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_MNGM                  = "";
    private static final String MTHD_MNGM_GW               = "mgnm/jcpapi/gws";
    private static final String MTHD_MNGM_OBJS             = "mgnm/jcpapi/objects";
    private static final String MTHD_MNGM_SRVS             = "mgnm/jcpapi/services";
    private static final String MTHD_MNGM_USRS             = "mgnm/jcpapi/users";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_MNGM                  = API_PATH + "/" + API_VER + "/" + MTHD_MNGM;
    public static final String FULL_PATH_MNGM_GW               = API_PATH + "/" + API_VER + "/" + MTHD_MNGM_GW;
    public static final String FULL_PATH_MNGM_OBJS             = API_PATH + "/" + API_VER + "/" + MTHD_MNGM_OBJS;
    public static final String FULL_PATH_MNGM_SRVS             = API_PATH + "/" + API_VER + "/" + MTHD_MNGM_SRVS;
    public static final String FULL_PATH_MNGM_USRS             = API_PATH + "/" + API_VER + "/" + MTHD_MNGM_USRS;

//@formatter:on
}
