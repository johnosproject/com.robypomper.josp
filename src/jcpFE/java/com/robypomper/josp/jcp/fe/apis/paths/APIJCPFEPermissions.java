package com.robypomper.josp.jcp.fe.apis.paths;

import com.robypomper.josp.jcp.fe.info.JCPFEAPIsVersions;

public class APIJCPFEPermissions {
//@formatter:off

    // API info

    public static final String API_NAME = "permissions";
    public static final String API_VER = JCPFEAPIsVersions.VER_JCPFE_APIs_1_0;
    public static final String API_PATH = JcpFEAPI.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupPermissions {
        public final static String NAME = "JCP FrontEnd Object's permissions";
        public final static String DESCR = "";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_LIST    = "{obj_id}/";
    private static final String MTHD_ADD      = "{obj_id}/add/";
    private static final String MTHD_UPD      = "{obj_id}/upd/{perm_id}/";
    private static final String MTHD_DEL      = "{obj_id}/del/{perm_id}/";
    private static final String MTHD_DUP      = "{obj_id}/dup/{perm_id}/";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_LIST       = API_PATH + "/" + API_VER + "/" + MTHD_LIST;
    public static final String FULL_PATH_ADD        = API_PATH + "/" + API_VER + "/" + MTHD_ADD;
    public static final String FULL_PATH_UPD        = API_PATH + "/" + API_VER + "/" + MTHD_UPD;
    public static final String FULL_PATH_DEL        = API_PATH + "/" + API_VER + "/" + MTHD_DEL;
    public static final String FULL_PATH_DUP        = API_PATH + "/" + API_VER + "/" + MTHD_DUP;

    public static String FULL_PATH_LIST (String objId)                  { return FULL_PATH_LIST .replace("{obj_id}",objId); }
    public static String FULL_PATH_ADD  (String objId)                  { return FULL_PATH_ADD  .replace("{obj_id}",objId); }
    public static String FULL_PATH_UPD  (String objId, String permId)   { return FULL_PATH_UPD  .replace("{obj_id}",objId).replace("{perm_id}",permId); }
    public static String FULL_PATH_DEL  (String objId, String permId)   { return FULL_PATH_DEL  .replace("{obj_id}",objId).replace("{perm_id}",permId); }
    public static String FULL_PATH_DUP  (String objId, String permId)   { return FULL_PATH_DUP  .replace("{obj_id}",objId).replace("{perm_id}",permId); }
//@formatter:on
}
