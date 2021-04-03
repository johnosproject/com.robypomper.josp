package com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.structure;


/**
 * JCP JSL Web Bridge - Objects / Structure 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to query objects structure and components";


    // API Methods

    //@formatter:off
    //
    private static final String MTHD_STRUCT    = "{obj_id}/";
    private static final String MTHD_COMP      = "{obj_id}/{comp_path}/";
    //@formatter:on


    // API Paths

    //@formatter:off
    //
    public static final String FULL_PATH_STRUCT     = API_PATH + "/" + API_VER + "/" + MTHD_STRUCT;
    public static final String FULL_PATH_COMP       = API_PATH + "/" + API_VER + "/" + MTHD_COMP;
    //@formatter:on

    // API Paths composers

    //@formatter:off
    //
    public static String FULL_PATH_STRUCT   (String objId)                  { return FULL_PATH_STRUCT   .replace("{obj_id}",objId); }
    public static String FULL_PATH_COMP     (String objId, String compPath) { return FULL_PATH_COMP     .replace("{obj_id}",objId).replace("{comp_path}",compPath); }
    //@formatter:on


    // API Descriptions

    //@formatter:off
    //
    public static final String DESCR_PATH_STRUCT    = "Return the object's full structure";
    public static final String DESCR_PATH_COMP      = "Return the object's component";
    //@formatter:on

}
