package com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.actions;


/**
 * JCP JSL Web Bridge - Objects / Actions 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to send objects actions";


    // API Methods

    //@formatter:off
    //
    private static final String MTHD_BOOL_SWITCH    = "bool/{obj_id}/{comp_path}/switch/";
    private static final String MTHD_BOOL_TRUE      = "bool/{obj_id}/{comp_path}/true/";
    private static final String MTHD_BOOL_FALSE     = "bool/{obj_id}/{comp_path}/false/";
    private static final String MTHD_RANGE_SET      = "range/{obj_id}/{comp_path}/";
    private static final String MTHD_RANGE_SETg     = "range/{obj_id}/{comp_path}/{val}";
    private static final String MTHD_RANGE_INC      = "range/{obj_id}/{comp_path}/inc/";
    private static final String MTHD_RANGE_DEC      = "range/{obj_id}/{comp_path}/dec/";
    private static final String MTHD_RANGE_MAX      = "range/{obj_id}/{comp_path}/max/";
    private static final String MTHD_RANGE_MIN      = "range/{obj_id}/{comp_path}/min/";
    private static final String MTHD_RANGE_1_2      = "range/{obj_id}/{comp_path}/1_2/";
    private static final String MTHD_RANGE_1_3      = "range/{obj_id}/{comp_path}/1_3/";
    private static final String MTHD_RANGE_2_3      = "range/{obj_id}/{comp_path}/2_3/";
    //@formatter:on


    // API Paths

    //@formatter:off
    //
    public static final String FULL_PATH_BOOL_SWITCH    = API_PATH + "/" + API_VER + "/" + MTHD_BOOL_SWITCH;
    public static final String FULL_PATH_BOOL_TRUE      = API_PATH + "/" + API_VER + "/" + MTHD_BOOL_TRUE;
    public static final String FULL_PATH_BOOL_FALSE     = API_PATH + "/" + API_VER + "/" + MTHD_BOOL_FALSE;
    public static final String FULL_PATH_RANGE_SET      = API_PATH + "/" + API_VER + "/" + MTHD_RANGE_SET;
    public static final String FULL_PATH_RANGE_SETg     = API_PATH + "/" + API_VER + "/" + MTHD_RANGE_SETg;
    public static final String FULL_PATH_RANGE_INC     = API_PATH + "/" + API_VER + "/" + MTHD_RANGE_INC;
    public static final String FULL_PATH_RANGE_DEC     = API_PATH + "/" + API_VER + "/" + MTHD_RANGE_DEC;
    public static final String FULL_PATH_RANGE_MAX     = API_PATH + "/" + API_VER + "/" + MTHD_RANGE_MAX;
    public static final String FULL_PATH_RANGE_MIN     = API_PATH + "/" + API_VER + "/" + MTHD_RANGE_MIN;
    public static final String FULL_PATH_RANGE_1_2     = API_PATH + "/" + API_VER + "/" + MTHD_RANGE_1_2;
    public static final String FULL_PATH_RANGE_1_3     = API_PATH + "/" + API_VER + "/" + MTHD_RANGE_1_3;
    public static final String FULL_PATH_RANGE_2_3     = API_PATH + "/" + API_VER + "/" + MTHD_RANGE_2_3;
    //@formatter:on


    // API Paths composers

    //@formatter:off
    //
    public static String FULL_PATH_BOOL_SWITCH  (String objId, String compPath){ return FULL_PATH_BOOL_SWITCH   .replace("{obj_id}",objId).replace("{comp_path}",compPath); }
    public static String FULL_PATH_BOOL_TRUE    (String objId, String compPath){ return FULL_PATH_BOOL_TRUE     .replace("{obj_id}",objId).replace("{comp_path}",compPath); }
    public static String FULL_PATH_BOOL_FALSE   (String objId, String compPath){ return FULL_PATH_BOOL_FALSE    .replace("{obj_id}",objId).replace("{comp_path}",compPath); }
    public static String FULL_PATH_RANGE_SET    (String objId, String compPath){ return FULL_PATH_RANGE_SET     .replace("{obj_id}",objId).replace("{comp_path}",compPath); }
    public static String FULL_PATH_RANGE_SETg   (String objId, String compPath){ return FULL_PATH_RANGE_SETg    .replace("{obj_id}",objId).replace("{comp_path}",compPath); }
    public static String FULL_PATH_RANGE_INC    (String objId, String compPath){ return FULL_PATH_RANGE_INC     .replace("{obj_id}",objId).replace("{comp_path}",compPath); }
    public static String FULL_PATH_RANGE_DEC    (String objId, String compPath){ return FULL_PATH_RANGE_DEC     .replace("{obj_id}",objId).replace("{comp_path}",compPath); }
    public static String FULL_PATH_RANGE_MAX    (String objId, String compPath){ return FULL_PATH_RANGE_MAX     .replace("{obj_id}",objId).replace("{comp_path}",compPath); }
    public static String FULL_PATH_RANGE_MIN    (String objId, String compPath){ return FULL_PATH_RANGE_MIN     .replace("{obj_id}",objId).replace("{comp_path}",compPath); }
    public static String FULL_PATH_RANGE_1_2    (String objId, String compPath){ return FULL_PATH_RANGE_1_2     .replace("{obj_id}",objId).replace("{comp_path}",compPath); }
    public static String FULL_PATH_RANGE_1_3    (String objId, String compPath){ return FULL_PATH_RANGE_1_3     .replace("{obj_id}",objId).replace("{comp_path}",compPath); }
    public static String FULL_PATH_RANGE_2_3    (String objId, String compPath){ return FULL_PATH_RANGE_2_3     .replace("{obj_id}",objId).replace("{comp_path}",compPath); }
    //@formatter:on


    // API Descriptions

    //@formatter:off
    //
    public static final String DESCR_PATH_BOOL_SWITCH   = "Send action to boolean component, invert the component state";
    public static final String DESCR_PATH_BOOL_TRUE     = "Send action to boolean component, set the component state to true";
    public static final String DESCR_PATH_BOOL_FALSE    = "Send action to boolean component, set the component state to false";
    public static final String DESCR_PATH_RANGE_SET     = "Send action to range component, set the component state to given value";
    public static final String DESCR_PATH_RANGE_SETg    = "Send action to range component, set the component state to given value";
    public static final String DESCR_PATH_RANGE_INC     = "Send action to range component, increment the component state of 1 step";
    public static final String DESCR_PATH_RANGE_DEC     = "Send action to range component, decrease the component state of 1 stop";
    public static final String DESCR_PATH_RANGE_MAX     = "Send action to range component, set the component state to max";
    public static final String DESCR_PATH_RANGE_MIN     = "Send action to range component, set the component state to min";
    public static final String DESCR_PATH_RANGE_1_2     = "Send action to range component, set the component state to 1/2";
    public static final String DESCR_PATH_RANGE_1_3     = "Send action to range component, set the component state to 1/3";
    public static final String DESCR_PATH_RANGE_2_3     = "Send action to range component, set the component state to 2/3";
    //@formatter:on

}