package com.robypomper.josp.jcp.apis.paths;

import com.robypomper.josp.jcp.info.JCPAPIsVersions;

public class APIPermissions {
//@formatter:off

    // API info

    public static final String API_NAME = "Permissions";
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JcpAPI.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupObjPerm {
        public final static String NAME = "Object's permissions";
        public final static String DESCR = "Get/merge permissions, set owner...";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_OBJGENERATE     = "obj";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_OBJGENERATE    = API_PATH + "/" + API_VER + "/" + MTHD_OBJGENERATE;

//@formatter:on
}
