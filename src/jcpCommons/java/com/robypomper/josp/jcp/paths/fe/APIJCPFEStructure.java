package com.robypomper.josp.jcp.paths.fe;

import com.robypomper.josp.jcp.info.JCPFEAPIsVersions;

public class APIJCPFEStructure {
//@formatter:off

    // API info

    public static final String API_NAME = "structure";
    public static final String API_VER = JCPFEAPIsVersions.VER_JCPFE_APIs_1_0;
    public static final String API_PATH = JcpFEAPI.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupStructure {
        public final static String NAME = "JCP FrontEnd Object's structure";
        public final static String DESCR = "";
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
//@formatter:on
}
