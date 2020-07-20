package com.robypomper.josp.jcp.apis.paths;

import com.robypomper.josp.jcp.info.JCPAPIsVersions;

public class APIObjs {
//@formatter:off

    // API info

    public static final String API_NAME = "Object";
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JcpAPI.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupInfo {
        public final static String NAME = "Object's info";
        public final static String DESCR = "Register/get object info, generate object ids...";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_GENERATEID         = "generate_id";
    private static final String MTHD_REGENERATEID       = "regenerate_id";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_GENERATEID         = API_PATH + "/" + API_VER + "/" + MTHD_GENERATEID;
    public static final String FULL_PATH_REGENERATEID       = API_PATH + "/" + API_VER + "/" + MTHD_REGENERATEID;


    public static final String HEADER_OBJID = "JOSP-Obj-ID";

//@formatter:on
}
