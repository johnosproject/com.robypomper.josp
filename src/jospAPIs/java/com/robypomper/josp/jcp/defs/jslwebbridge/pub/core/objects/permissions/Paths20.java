package com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.permissions;


/**
 * JCP JSL Web Bridge - Objects / Permissions 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to get/upd/rem/dup objects permissions";


    // API Methods

    //@formatter:off
    //
    private static final String MTHD_LIST    = "{obj_id}/";
    private static final String MTHD_ADD      = "{obj_id}/add/";
    private static final String MTHD_UPD      = "{obj_id}/upd/{perm_id}/";
    private static final String MTHD_DEL      = "{obj_id}/del/{perm_id}/";
    private static final String MTHD_DUP      = "{obj_id}/dup/{perm_id}/";
    //@formatter:on


    // API Paths

    //@formatter:off
    //
    public static final String FULL_PATH_LIST       = API_PATH + "/" + API_VER + "/" + MTHD_LIST;
    public static final String FULL_PATH_ADD        = API_PATH + "/" + API_VER + "/" + MTHD_ADD;
    public static final String FULL_PATH_UPD        = API_PATH + "/" + API_VER + "/" + MTHD_UPD;
    public static final String FULL_PATH_DEL        = API_PATH + "/" + API_VER + "/" + MTHD_DEL;
    public static final String FULL_PATH_DUP        = API_PATH + "/" + API_VER + "/" + MTHD_DUP;
    //@formatter:on


    // API Paths composers

    //@formatter:off
    //
    public static String FULL_PATH_LIST (String objId)                  { return FULL_PATH_LIST .replace("{obj_id}",objId); }
    public static String FULL_PATH_ADD  (String objId)                  { return FULL_PATH_ADD  .replace("{obj_id}",objId); }
    public static String FULL_PATH_UPD  (String objId, String permId)   { return FULL_PATH_UPD  .replace("{obj_id}",objId).replace("{perm_id}",permId); }
    public static String FULL_PATH_DEL  (String objId, String permId)   { return FULL_PATH_DEL  .replace("{obj_id}",objId).replace("{perm_id}",permId); }
    public static String FULL_PATH_DUP  (String objId, String permId)   { return FULL_PATH_DUP  .replace("{obj_id}",objId).replace("{perm_id}",permId); }
    //@formatter:on


    // API Descriptions

    //@formatter:off
    //
    public static final String DESCR_PATH_LIST  = "Return the object's permissions list";
    public static final String DESCR_PATH_ADD   = "Send add permission request to object";
    public static final String DESCR_PATH_UPD   = "Send update permission request to object";
    public static final String DESCR_PATH_DEL   = "Send remove permission request to object";
    public static final String DESCR_PATH_DUP   = "Send duplicate permission request to object";
    //@formatter:on

}
