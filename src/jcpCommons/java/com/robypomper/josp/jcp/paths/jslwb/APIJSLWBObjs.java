package com.robypomper.josp.jcp.paths.jslwb;

import com.robypomper.josp.jcp.info.JCPJSLWBVersions;

public class APIJSLWBObjs {
//@formatter:off

    // API info

    public static final String API_NAME = "objsmngr";
    public static final String API_VER = JCPJSLWBVersions.VER_JCPJSLWB_APIs_1_0;
    public static final String API_PATH = JCPJSLWBVersions.PATH_JSLWB_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupObjs {
        public final static String NAME = "Object's Manager";
        public final static String DESCR = "Methods to query object's info";
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


    // API's descriptions

    public static final String DESCR_PATH_LIST      = "Return the list of available objects";
    public static final String DESCR_PATH_DETAILS   = "Return object's details";
    public static final String DESCR_PATH_OWNER     = "Set object's owner";
    public static final String DESCR_PATH_NAME      = "Set object's name";
    public static final String DESCR_PATH_EVENTS    = "Return object's events list";

//@formatter:on
}
