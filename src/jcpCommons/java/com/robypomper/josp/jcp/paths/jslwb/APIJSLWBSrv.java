package com.robypomper.josp.jcp.paths.jslwb;

import com.robypomper.josp.jcp.info.JCPJSLWBVersions;

public class APIJSLWBSrv {
//@formatter:off

    // API info

    public static final String API_NAME = "service";
    public static final String API_VER = JCPJSLWBVersions.VER_JCPJSLWB_APIs_1_0;
    public static final String API_PATH = JCPJSLWBVersions.PATH_JSLWB_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupService {
        public final static String NAME = "Service's Manager";
        public final static String DESCR = "Methods to query service's info";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_DETAILS    = "";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_DETAILS    = API_PATH + "/" + API_VER + "/" + MTHD_DETAILS;


    // API's descriptions

    public static final String DESCR_PATH_DETAILS   = "Return current service's details";

//@formatter:on
}
