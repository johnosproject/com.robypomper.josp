package com.robypomper.josp.jcp.paths.jslwb;

import com.robypomper.josp.jcp.info.JCPJSLWBVersions;

public class APIJSLWBStruct {
//@formatter:off

    // API info

    public static final String API_NAME = "JSL/Structure";
    public static final String API_VER = JCPJSLWBVersions.VER_JCPJSLWB_APIs_1_0;
    public static final String API_PATH = JCPJSLWBVersions.PATH_JSLWB_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupStructure {
        public final static String NAME = "Objects Structure";
        public final static String DESCR = "Methods to query objects structure and components";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_STRUCT    = "{obj_id}/";
    private static final String MTHD_COMP      = "{obj_id}/{comp_path}/";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_STRUCT     = API_PATH + "/" + API_VER + "/" + MTHD_STRUCT;
    public static final String FULL_PATH_COMP       = API_PATH + "/" + API_VER + "/" + MTHD_COMP;

    public static String FULL_PATH_STRUCT   (String objId)                  { return FULL_PATH_STRUCT   .replace("{obj_id}",objId); }
    public static String FULL_PATH_COMP     (String objId, String compPath) { return FULL_PATH_COMP     .replace("{obj_id}",objId).replace("{comp_path}",compPath); }


    // API's descriptions

    public static final String DESCR_PATH_STRUCT    = "Return the object's full structure";
    public static final String DESCR_PATH_COMP      = "Return the object's component";

//@formatter:on
}
