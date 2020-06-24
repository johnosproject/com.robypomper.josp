package com.robypomper.josp.protocol;


import java.util.ArrayList;
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
    private static final String OBJ_PERMS_REQ_FORMAT = "objId:%s;srvId:%s;usrId%s;type:%s;conn:%s";

    public static String createObjectPermsMsg(String objId, String perms) {
        return String.format(OBJ_PERMS_REQ, JOSPProtocol.getNow(), objId, perms);
    }

    public static boolean isObjectPermsMsg(String msg) {
        return msg.startsWith(OBJ_PERMS_REQ_BASE);
    }

    public static List<JOSPPerm> getObjectPermsMsg_Perms(String msg) throws JOSPProtocol.ParsingException {
        List<JOSPPerm> perms = new ArrayList<>();

        String permsStr = JOSPProtocol.extractPayloadFromResponse(msg, 3, 2, OBJ_PERMS_REQ_NAME);
        for (String permStr : permsStr.split("\n"))
            perms.add(JOSPPerm.fromString(permStr));

        return perms;
    }

    public static class JOSPPerm {

        private final String objId;
        private final String srvId;
        private final String usrId;
        private final String permType;
        private final String permConn;

        public JOSPPerm(String objId, String srvId, String usrId, String permType, String permConn) {
            this.objId = objId;
            this.srvId = srvId;
            this.usrId = usrId;
            this.permType = permType;
            this.permConn = permConn;
        }

        public String getObjId() {
            return objId;
        }

        public String getSrvId() {
            return srvId;
        }

        public String getUsrId() {
            return usrId;
        }

        public String getPermType() {
            return permType;
        }

        public String getPermConn() {
            return permConn;
        }

        public static JOSPPerm fromString(String permStr) throws JOSPProtocol.ParsingException {
            String[] permStrs = permStr.split(";");
            if (permStrs.length != 5)
                throw new JOSPProtocol.ParsingException("Few fields in JOSPPerm string");

            String objId = permStrs[0].substring(0, permStrs[0].indexOf(":"));
            String srvId = permStrs[1].substring(0, permStrs[1].indexOf(":"));
            String usrId = permStrs[2].substring(0, permStrs[2].indexOf(":"));
            String permType = permStrs[3].substring(0, permStrs[3].indexOf(":"));
            String permConn = permStrs[4].substring(0, permStrs[4].indexOf(":"));

            return new JOSPPerm(objId, srvId, usrId, permType, permConn);
        }

        public static String toString(JOSPPerm perm) {
            return String.format(OBJ_PERMS_REQ_FORMAT, perm.getObjId(), perm.getSrvId(), perm.getUsrId(), perm.getPermType(), perm.getPermConn());
        }

        public static String toString(List<JOSPPerm> perms) {
            StringBuilder str = new StringBuilder();
            for (JOSPPerm perm : perms) {
                str.append(toString(perm));
                str.append("\n");
            }

            return str.toString();
        }

    }


    // Service Perms

    public static final String SRV_PERMS_REQ_NAME = "ServicePerms";
    private static final String SRV_PERMS_REQ_BASE = JOSPProtocol.JOSP_PROTO + " SRV_PERMS_MSG";
    private static final String SRV_PERMS_REQ = SRV_PERMS_REQ_BASE + " %s\nobjId:%s\npermType:%s\npermConn:%s";

    public static String createServicePermMsg(String objId, JOSPPermissions.Type permType, JOSPPermissions.Connection permConn) {
        return String.format(SRV_PERMS_REQ, JOSPProtocol.getNow(), objId, permType, permConn);
    }

    public static boolean isServicePermsMsg(String msg) {
        return msg.startsWith(SRV_PERMS_REQ_BASE);
    }

    public static String getServicePermsMsg_PermType(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 3, 2, SRV_PERMS_REQ_NAME);
    }

    public static String getServicePermsMsg_PermConn(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 3, 2, SRV_PERMS_REQ_NAME);
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
