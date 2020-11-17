package com.robypomper.josp.jcp.fe.apis.paths;

import com.robypomper.josp.jcp.fe.info.JCPFEAPIsVersions;

public class APIJCPFEAction {
//@formatter:off

    // API info

    public static final String API_NAME = "action";
    public static final String API_VER = JCPFEAPIsVersions.VER_JCPFE_APIs_1_0;
    public static final String API_PATH = JcpFEAPI.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupAction {
        public final static String NAME = "JCP FrontEnd Object's State";
        public final static String DESCR = "";
    }


    // API's methods

    // '{mthdName}
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

    // '/apis/{apiName}/{apiVersion}/{mthdName}
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
}
