package com.robypomper.josp.jcp.paths.jslwb;

import com.robypomper.josp.jcp.info.JCPFEVersions;

public class APIJSLWBSSEUpdater {
//@formatter:off

    // API info

    public static final String API_NAME = "sse";
    public static final String API_VER = JCPFEVersions.VER_JCPFE_APIs_1_0;
    public static final String API_PATH = JCPFEVersions.PATH_FE_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupState {
        public final static String NAME = "JCP FrontEnd Updater ServerSendEvent";
        public final static String DESCR = "";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_INIT   = "init";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_INIT   = API_PATH + "/" + API_VER + "/" + MTHD_INIT;
//@formatter:on
}
