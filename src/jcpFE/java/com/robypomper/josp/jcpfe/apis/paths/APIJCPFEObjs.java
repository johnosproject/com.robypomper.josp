package com.robypomper.josp.jcpfe.apis.paths;

import com.robypomper.josp.jcpfe.info.JCPFEAPIsVersions;

public class APIJCPFEObjs {
//@formatter:off

    // API info

    public static final String API_NAME = "objsmngr";
    public static final String API_VER = JCPFEAPIsVersions.VER_JCPFE_APIs_1_0;
    public static final String API_PATH = JcpFEAPI.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupObjs {
        public final static String NAME = "JCP FrontEnd Objects Manager";
        public final static String DESCR = "";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_LIST       = "";
    private static final String MTHD_DETAILS    = "{obj_id}/";
    private static final String MTHD_OWNER      = "{obj_id}/owner/";
    private static final String MTHD_NAME       = "{obj_id}/name/";
    private static final String MTHD_EVENTS     = "{obj_id}/events/";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_LIST       = API_PATH + "/" + API_VER + "/" + MTHD_LIST;
    public static final String FULL_PATH_DETAILS    = API_PATH + "/" + API_VER + "/" + MTHD_DETAILS;
    public static final String FULL_PATH_OWNER      = API_PATH + "/" + API_VER + "/" + MTHD_OWNER;
    public static final String FULL_PATH_NAME       = API_PATH + "/" + API_VER + "/" + MTHD_NAME;
    public static final String FULL_PATH_EVENTS     = API_PATH + "/" + API_VER + "/" + MTHD_EVENTS;

    public static String FULL_PATH_DETAILS  (String objId){ return FULL_PATH_DETAILS    .replace("{obj_id}",objId); }
    public static String FULL_PATH_OWNER    (String objId){ return FULL_PATH_OWNER      .replace("{obj_id}",objId); }
    public static String FULL_PATH_NAME     (String objId){ return FULL_PATH_NAME       .replace("{obj_id}",objId); }
    public static String FULL_PATH_EVENTS   (String objId){ return FULL_PATH_EVENTS       .replace("{obj_id}",objId); }
//@formatter:on
}
