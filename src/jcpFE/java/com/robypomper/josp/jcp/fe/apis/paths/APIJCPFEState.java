package com.robypomper.josp.jcp.fe.apis.paths;

import com.robypomper.josp.jcp.fe.info.JCPFEAPIsVersions;

public class APIJCPFEState {
//@formatter:off

    // API info

    public static final String API_NAME = "state";
    public static final String API_VER = JCPFEAPIsVersions.VER_JCPFE_APIs_1_0;
    public static final String API_PATH = JcpFEAPI.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupState {
        public final static String NAME = "JCP FrontEnd Object's State";
        public final static String DESCR = "";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_BOOL   = "bool/{obj_id}/{comp_path}/";
    private static final String MTHD_RANGE  = "range/{obj_id}/{comp_path}/";
    private static final String MTHD_STATUS_HISTORY  = "history/{obj_id}/{comp_path}/";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_BOOL       = API_PATH + "/" + API_VER + "/" + MTHD_BOOL;
    public static final String FULL_PATH_RANGE      = API_PATH + "/" + API_VER + "/" + MTHD_RANGE;
    public static final String FULL_STATUS_HISTORY  = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_HISTORY;

    public static String FULL_PATH_BOOL     (String objId, String compPath){ return FULL_PATH_BOOL  .replace("{obj_id}",objId).replace("{comp_path}",compPath); }
    public static String FULL_PATH_RANGE    (String objId, String compPath){ return FULL_PATH_RANGE .replace("{obj_id}",objId).replace("{comp_path}",compPath); }
    public static String FULL_STATUS_HISTORY(String objId, String compPath){ return FULL_STATUS_HISTORY .replace("{obj_id}",objId).replace("{comp_path}",compPath); }
//@formatter:on
}
