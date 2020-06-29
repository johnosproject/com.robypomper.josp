package com.robypomper.josp.protocol;


import java.util.List;

public class JOSPProtocol_ObjectToService {

    // Common utils

    private static final String OBJ_REQ_NAME = "ObjectToService";

    public static String getObjId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 2, 1, OBJ_REQ_NAME);
    }


    // Object disconnected

    public static final String OBJ_DISCONNECT_REQ_NAME = "ObjectDisconnect";
    private static final String OBJ_DISCONNECT_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_DISCONNECT_MSG";
    private static final String OBJ_DISCONNECT_REQ = OBJ_DISCONNECT_REQ_BASE + " %s\nobjId:%s";

    public static String createObjectDisconnectMsg(String objId) {
        return String.format(OBJ_DISCONNECT_REQ, JOSPProtocol.getNow(), objId);
    }

    public static boolean isObjectDisconnectMsg(String msg) {
        return msg.startsWith(OBJ_DISCONNECT_REQ_BASE);
    }


    // Object Info

    public static final String OBJ_INF_REQ_NAME = "ObjectInfo";
    private static final String OBJ_INF_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_INF_MSG";
    private static final String OBJ_INF_REQ = OBJ_INF_REQ_BASE + " %s\nobjId:%s\nobjName:%s\njodVersion:%s\nownerId:%s\nmodel:%s\nbrand:%s\ndescr:%s";

    public static String createObjectInfoMsg(String objId, String objName, String jodVersion, String ownerId, String model, String brand, String descr) {
        return String.format(OBJ_INF_REQ, JOSPProtocol.getNow(), objId, objName, jodVersion, ownerId, model, brand, descr);
    }

    public static boolean isObjectInfoMsg(String msg) {
        return msg.startsWith(OBJ_INF_REQ_BASE);
    }

    public static String getObjectInfoMsg_Name(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 8, 2, OBJ_INF_REQ_NAME);
    }

    public static String getObjectInfoMsg_JODVersion(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 8, 3, OBJ_INF_REQ_NAME);
    }

    public static String getObjectInfoMsg_OwnerId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 8, 4, OBJ_INF_REQ_NAME);
    }

    public static String getObjectInfoMsg_Model(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 8, 5, OBJ_INF_REQ_NAME);
    }

    public static String getObjectInfoMsg_Brand(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 8, 6, OBJ_INF_REQ_NAME);
    }

    public static String getObjectInfoMsg_LongDescr(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 8, 7, OBJ_INF_REQ_NAME);
    }


    // Object Struct

    public static final String OBJ_STRUCT_REQ_NAME = "ObjectStruct";
    private static final String OBJ_STRUCT_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_STRUCT_MSG";
    private static final String OBJ_STRUCT_REQ = OBJ_STRUCT_REQ_BASE + " %s\nobjId:%s\n%s";

    public static String createObjectStructMsg(String objId, String struct) {
        return String.format(OBJ_STRUCT_REQ, JOSPProtocol.getNow(), objId, struct);
    }

    public static boolean isObjectStructMsg(String msg) {
        return msg.startsWith(OBJ_STRUCT_REQ_BASE);
    }

    public static String getObjectStructMsg_Struct(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractPayloadFromResponse(msg, 3, 2, OBJ_STRUCT_REQ_NAME);
    }


    // Object Perms

    public static final String OBJ_PERMS_REQ_NAME = "ObjectPerms";
    private static final String OBJ_PERMS_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_PERMS_MSG";
    private static final String OBJ_PERMS_REQ = OBJ_PERMS_REQ_BASE + " %s\nobjId:%s\n%s";

    public static String createObjectPermsMsg(String objId, String perms) {
        return String.format(OBJ_PERMS_REQ, JOSPProtocol.getNow(), objId, perms);
    }

    public static boolean isObjectPermsMsg(String msg) {
        return msg.startsWith(OBJ_PERMS_REQ_BASE);
    }

    public static List<JOSPPerm> getObjectPermsMsg_Perms(String msg) throws JOSPProtocol.ParsingException {
        String permsStr = JOSPProtocol.extractPayloadFromResponse(msg, 3, 2, OBJ_PERMS_REQ_NAME);
        return JOSPPerm.listFromString(permsStr);
    }


    // Service Perms

    public static final String SRV_PERMS_REQ_NAME = "ServicePerms";
    private static final String SRV_PERMS_REQ_BASE = JOSPProtocol.JOSP_PROTO + " SRV_PERMS_MSG";
    private static final String SRV_PERMS_REQ = SRV_PERMS_REQ_BASE + " %s\nobjId:%s\npermType:%s\nconnType:%s";

    public static String createServicePermMsg(String objId, JOSPPerm.Type permType, JOSPPerm.Connection permConn) {
        return String.format(SRV_PERMS_REQ, JOSPProtocol.getNow(), objId, permType, permConn);
    }

    public static boolean isServicePermsMsg(String msg) {
        return msg.startsWith(SRV_PERMS_REQ_BASE);
    }

    public static JOSPPerm.Type getServicePermsMsg_PermType(String msg) throws JOSPProtocol.ParsingException {
        return JOSPPerm.Type.valueOf(JOSPProtocol.extractFieldFromResponse(msg, 4, 2, SRV_PERMS_REQ_NAME));
    }

    public static JOSPPerm.Connection getServicePermsMsg_ConnType(String msg) throws JOSPProtocol.ParsingException {
        return JOSPPerm.Connection.valueOf(JOSPProtocol.extractFieldFromResponse(msg, 4, 3, SRV_PERMS_REQ_NAME));
    }


    // Status Upd Msg class

    private static final String UPD_MSG_BASE = JOSPProtocol.JOSP_PROTO + " UPD_MSG";
    private static final String UPD_MSG = UPD_MSG_BASE + " %s\nobjId:%s\ncompPath:%s\ncmdType:%s\n%s";

    public static String createObjectStateUpdMsg(String objId, String compPath, JOSPStateUpdateParams udpdate) {
        return StateUpdMsg.fromUpdToMsg(new StateUpdMsg(objId, compPath, udpdate));
    }

    public static boolean isObjectStateUpdMsg(String msg) {
        return msg.startsWith(UPD_MSG_BASE);
    }

    /**
     * Data class to return info contained in state update messages.
     */
    public static class StateUpdMsg {

        // Internal vars

        private final String objectId;
        private final String componentPath;
        private final JOSPStateUpdateParams update;


        // Constructor

        StateUpdMsg(String objectId, String componentPath, JOSPStateUpdateParams update) {
            this.objectId = objectId;
            this.componentPath = componentPath;
            this.update = update;
        }


        // Getters

        public String getObjectId() {
            return objectId;
        }

        public String getComponentPath() {
            return componentPath;
        }

        public JOSPStateUpdateParams getUpdate() {
            return update;
        }


        // Casting

        static String fromUpdToMsg(StateUpdMsg upd) {
            return String.format(UPD_MSG, JOSPProtocol.getNow(), upd.getObjectId(),
                    upd.getComponentPath(), upd.getUpdate().getType(), upd.getUpdate().encode());
        }

    }

}
