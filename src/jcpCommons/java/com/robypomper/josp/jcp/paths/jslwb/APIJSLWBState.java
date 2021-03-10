package com.robypomper.josp.jcp.paths.jslwb;

import com.robypomper.josp.jcp.info.JCPJSLWBVersions;

public class APIJSLWBState {
//@formatter:off

    // API info

    public static final String API_NAME = "JSL/State";
    public static final String API_VER = JCPJSLWBVersions.VER_JCPJSLWB_APIs_1_0;
    public static final String API_PATH = JCPJSLWBVersions.PATH_JSLWB_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupState {
        public final static String NAME = "Objects States";
        public final static String DESCR = "Methods to get objects states and their histories";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_BOOL   = "bool/{obj_id}/{comp_path}/";
    private static final String MTHD_RANGE  = "range/{obj_id}/{comp_path}/";
    private static final String MTHD_STATUS_HISTORY  = "history/{obj_id}/{comp_path}/";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_BOOL       = API_PATH + "/" + API_VER + "/" + MTHD_BOOL;
    public static final String FULL_PATH_RANGE      = API_PATH + "/" + API_VER + "/" + MTHD_RANGE;
    public static final String FULL_PATH_STATUS_HISTORY  = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_HISTORY;

    public static String FULL_PATH_BOOL             (String objId, String compPath){ return FULL_PATH_BOOL  .replace("{obj_id}",objId).replace("{comp_path}",compPath); }
    public static String FULL_PATH_RANGE            (String objId, String compPath){ return FULL_PATH_RANGE .replace("{obj_id}",objId).replace("{comp_path}",compPath); }
    public static String FULL_PATH_STATUS_HISTORY   (String objId, String compPath){ return FULL_PATH_STATUS_HISTORY .replace("{obj_id}",objId).replace("{comp_path}",compPath); }


    // API's descriptions

    public static final String DESCR_PATH_BOOL      = "Return boolean component state";
    public static final String DESCR_PATH_RANGE     = "Return range component state";
    public static final String DESCR_PATH_STATUS_HISTORY = "Return component state history";

//@formatter:on
}
