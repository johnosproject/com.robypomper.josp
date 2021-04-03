package com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects;


/**
 * JCP JSL Web Bridge - Objects 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to query objects info";


    // API Methods

    //@formatter:off
    //
    private static final String MTHD_LIST       = "";
    private static final String MTHD_DETAILS    = "{obj_id}/";
    private static final String MTHD_OWNER      = "{obj_id}/owner/";
    private static final String MTHD_NAME       = "{obj_id}/name/";
    private static final String MTHD_EVENTS     = "{obj_id}/events/";
    //@formatter:on


    // API Paths

    //@formatter:off
    //
    public static final String FULL_PATH_LIST       = API_PATH + "/" + API_VER + "/" + MTHD_LIST;
    public static final String FULL_PATH_DETAILS    = API_PATH + "/" + API_VER + "/" + MTHD_DETAILS;
    public static final String FULL_PATH_OWNER      = API_PATH + "/" + API_VER + "/" + MTHD_OWNER;
    public static final String FULL_PATH_NAME       = API_PATH + "/" + API_VER + "/" + MTHD_NAME;
    public static final String FULL_PATH_EVENTS     = API_PATH + "/" + API_VER + "/" + MTHD_EVENTS;
    //@formatter:on

    // API Paths composers

    //@formatter:off
    //
    public static String FULL_PATH_DETAILS  (String objId){ return FULL_PATH_DETAILS    .replace("{obj_id}",objId); }
    public static String FULL_PATH_OWNER    (String objId){ return FULL_PATH_OWNER      .replace("{obj_id}",objId); }
    public static String FULL_PATH_NAME     (String objId){ return FULL_PATH_NAME       .replace("{obj_id}",objId); }
    public static String FULL_PATH_EVENTS   (String objId){ return FULL_PATH_EVENTS       .replace("{obj_id}",objId); }
    //@formatter:on


    // API Descriptions

    //@formatter:off
    //
    public static final String DESCR_PATH_LIST      = "Return the list of available objects";
    public static final String DESCR_PATH_DETAILS   = "Return object's details";
    public static final String DESCR_PATH_OWNER     = "Set object's owner";
    public static final String DESCR_PATH_NAME      = "Set object's name";
    public static final String DESCR_PATH_EVENTS    = "Return object's events list";
    //@formatter:on

//@formatter:on
}