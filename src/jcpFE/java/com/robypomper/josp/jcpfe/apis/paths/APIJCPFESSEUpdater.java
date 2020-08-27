package com.robypomper.josp.jcpfe.apis.paths;

import com.robypomper.josp.jcpfe.info.JCPFEAPIsVersions;

public class APIJCPFESSEUpdater {
//@formatter:off

    // API info

    public static final String API_NAME = "sse";
    public static final String API_VER = JCPFEAPIsVersions.VER_JCPFE_APIs_1_0;
    public static final String API_PATH = JcpFEAPI.PATH_API_BASE + "/" + API_NAME;


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
