package com.robypomper.josp.jcpfe.apis.paths;

import com.robypomper.josp.jcpfe.info.JCPFEAPIsVersions;

public class APIJCPFEService {
//@formatter:off

    // API info

    public static final String API_NAME = "service";
    public static final String API_VER = JCPFEAPIsVersions.VER_JCPFE_APIs_1_0;
    public static final String API_PATH = JcpFEAPI.PATH_API_BASE + "/" + API_NAME;


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
