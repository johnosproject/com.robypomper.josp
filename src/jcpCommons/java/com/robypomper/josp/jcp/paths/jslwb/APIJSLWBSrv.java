package com.robypomper.josp.jcp.paths.jslwb;

import com.robypomper.josp.jcp.info.JCPFEVersions;

public class APIJSLWBSrv {
//@formatter:off

    // API info

    public static final String API_NAME = "service";
    public static final String API_VER = JCPFEVersions.VER_JCPFE_APIs_1_0;
    public static final String API_PATH = JCPFEVersions.PATH_FE_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupService {
        public final static String NAME = "JCP FrontEnd Service";
        public final static String DESCR = "";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_DETAILS    = "";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_DETAILS    = API_PATH + "/" + API_VER + "/" + MTHD_DETAILS;

//@formatter:on
}
