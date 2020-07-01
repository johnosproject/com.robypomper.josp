package com.robypomper.josp.protocol;


public class JOSPProtocol_ServiceToObject {

    // Common utils

    private static final String OBJ_REQ_NAME = "ServiceToObject";

    public static String getFullSrvId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 3, 1, OBJ_REQ_NAME);
    }

    public static String getSrvId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol_Service.fullSrvIdToSrvId(getFullSrvId(msg));
    }

    public static String getUsrId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol_Service.fullSrvIdToUsrId(getFullSrvId(msg));
    }

    public static String getInstId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol_Service.fullSrvIdToInstId(getFullSrvId(msg));
    }

    public static String getObjId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 3, 2, OBJ_REQ_NAME);
    }


    // Object Set Name

    public static final String OBJ_SETNAME_REQ_NAME = "ObjectSetName";
    private static final String OBJ_SETNAME_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_SETNAME_MSG";
    private static final String OBJ_SETNAME_REQ = OBJ_SETNAME_REQ_BASE + " %s\nfullSrvId:%s\nobjId:%s\nobjName:%s";

    public static String createObjectSetNameMsg(String fullSrvId, String objId, String newName) {
        return String.format(OBJ_SETNAME_REQ, JOSPProtocol.getNow(), fullSrvId, objId, newName);
    }

    public static boolean isObjectSetNameMsg(String msg) {
        return msg.startsWith(OBJ_SETNAME_REQ_BASE);
    }

    public static String getObjectSetNameMsg_Name(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 4, 3, OBJ_SETNAME_REQ_NAME);
    }


    // Object Set Owner Id

    public static final String OBJ_SETOWNERID_REQ_NAME = "ObjectSetOwnerId";
    private static final String OBJ_SETOWNERID_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_SETOWNERID_MSG";
    private static final String OBJ_SETOWNERID_REQ = OBJ_SETOWNERID_REQ_BASE + " %s\nfullSrvId:%s\nobjId:%s\nownerId:%s";

    public static String createObjectSetOwnerIdMsg(String fullSrvId, String objId, String newOwnerId) {
        return String.format(OBJ_SETOWNERID_REQ, JOSPProtocol.getNow(), fullSrvId, objId, newOwnerId);
    }

    public static boolean isObjectSetOwnerIdMsg(String msg) {
        return msg.startsWith(OBJ_SETOWNERID_REQ_BASE);
    }

    public static String getObjectSetOwnerIdMsg_OwnerId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 4, 3, OBJ_SETOWNERID_REQ_NAME);
    }


    // Object Add permission

    public static final String OBJ_ADDPERM_REQ_NAME = "ObjectAddPerm";
    private static final String OBJ_ADDPERM_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_ADDPERM_MSG";
    private static final String OBJ_ADDPERM_REQ = OBJ_ADDPERM_REQ_BASE + " %s\nfullSrvId:%s\nobjId:%s\nsrvId:%s\nusrId:%s\npermType:%s\nconnType:%s";

    public static String createObjectAddPermMsg(String fullSrvId, String objId, String srvId, String usrId, JOSPPerm.Type permType, JOSPPerm.Connection connType) {
        return String.format(OBJ_ADDPERM_REQ, JOSPProtocol.getNow(), fullSrvId, objId, srvId, usrId, permType, connType);
    }

    public static boolean isObjectAddPermMsg(String msg) {
        return msg.startsWith(OBJ_ADDPERM_REQ_BASE);
    }

    public static String getObjectAddPermMsg_SrvId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 7, 3, OBJ_ADDPERM_REQ_NAME);
    }

    public static String getObjectAddPermMsg_UsrId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 7, 4, OBJ_ADDPERM_REQ_NAME);
    }

    public static JOSPPerm.Type getObjectAddPermMsg_PermType(String msg) throws JOSPProtocol.ParsingException {
        return JOSPPerm.Type.valueOf(JOSPProtocol.extractFieldFromResponse(msg, 7, 5, OBJ_ADDPERM_REQ_NAME));
    }

    public static JOSPPerm.Connection getObjectAddPermMsg_ConnType(String msg) throws JOSPProtocol.ParsingException {
        return JOSPPerm.Connection.valueOf(JOSPProtocol.extractFieldFromResponse(msg, 7, 6, OBJ_ADDPERM_REQ_NAME));
    }


    // Object Upd permission

    public static final String OBJ_UPDPERM_REQ_NAME = "ObjectUpdPerm";
    private static final String OBJ_UPDPERM_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_UPDPERM_MSG";
    private static final String OBJ_UPDPERM_REQ = OBJ_UPDPERM_REQ_BASE + " %s\nfullSrvId:%s\nobjId:%s\npermId:%s\nsrvId:%s\nusrId:%s\npermType:%s\nconnType:%s";

    public static String createObjectUpdPermMsg(String fullSrvId, String objId, String permId, String srvId, String usrId, JOSPPerm.Type permType, JOSPPerm.Connection connType) {
        return String.format(OBJ_UPDPERM_REQ, JOSPProtocol.getNow(), fullSrvId, objId, permId, srvId, usrId, permType, connType);
    }

    public static boolean isObjectUpdPermMsg(String msg) {
        return msg.startsWith(OBJ_UPDPERM_REQ_BASE);
    }

    public static String getObjectUpdPermMsg_PermId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 8, 3, OBJ_UPDPERM_REQ_NAME);
    }

    public static String getObjectUpdPermMsg_SrvId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 8, 4, OBJ_UPDPERM_REQ_NAME);
    }

    public static String getObjectUpdPermMsg_UsrId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 8, 5, OBJ_UPDPERM_REQ_NAME);
    }

    public static JOSPPerm.Type getObjectUpdPermMsg_PermType(String msg) throws JOSPProtocol.ParsingException {
        return JOSPPerm.Type.valueOf(JOSPProtocol.extractFieldFromResponse(msg, 8, 6, OBJ_UPDPERM_REQ_NAME));
    }

    public static JOSPPerm.Connection getObjectUpdPermMsg_ConnType(String msg) throws JOSPProtocol.ParsingException {
        return JOSPPerm.Connection.valueOf(JOSPProtocol.extractFieldFromResponse(msg, 8, 7, OBJ_UPDPERM_REQ_NAME));
    }


    // Object Rem permission

    public static final String OBJ_REMPERM_REQ_NAME = "ObjectRemPerm";
    private static final String OBJ_REMPERM_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_REMPERM_MSG";
    private static final String OBJ_REMPERM_REQ = OBJ_REMPERM_REQ_BASE + " %s\nfullSrvId:%s\nobjId:%s\npermId:%s";

    public static String createObjectRemPermMsg(String fullSrvId, String objId, String permId) {
        return String.format(OBJ_REMPERM_REQ, JOSPProtocol.getNow(), fullSrvId, objId, permId);
    }

    public static boolean isObjectRemPermMsg(String msg) {
        return msg.startsWith(OBJ_REMPERM_REQ_BASE);
    }

    public static String getObjectRemPermMsg_PermId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 4, 3, OBJ_REMPERM_REQ_NAME);
    }


    // Action Cmd Msg class

    private static final String CMD_MSG_BASE = JOSPProtocol.JOSP_PROTO + " CMD_MSG";
    private static final String CMD_MSG = CMD_MSG_BASE + " %s\nfullSrvId:%s/%s/%s\nobjId:%s\ncompPath:%s\ncmdType:%s\n%s";

    public static String createObjectActionCmdMsg(String fullSrvId, String objId, String compPath, JOSPActionCommandParams command) {
        String srvId = JOSPProtocol_Service.fullSrvIdToSrvId(fullSrvId);
        String usrId = JOSPProtocol_Service.fullSrvIdToUsrId(fullSrvId);
        String instId = JOSPProtocol_Service.fullSrvIdToInstId(fullSrvId);
        return ActionCmdMsg.fromCmdToMsg(new ActionCmdMsg(srvId, usrId, instId, objId, compPath, command));
    }

    public static boolean isObjectActionCmdMsg(String msg) {
        return msg.startsWith(CMD_MSG_BASE);
    }

    /**
     * Data class to return info contained in action command messages.
     */
    public static class ActionCmdMsg {

        // Internal vars

        private final String serviceId;
        private final String userId;
        private final String instanceId;
        private final String objectId;
        private final String componentPath;
        private final JOSPActionCommandParams command;


        // Constructor

        ActionCmdMsg(String serviceId, String userId, String instanceId, String objectId, String componentPath, JOSPActionCommandParams command) {
            this.serviceId = serviceId;
            this.userId = userId;
            this.instanceId = instanceId;
            this.objectId = objectId;
            this.componentPath = componentPath;
            this.command = command;
        }


        // Getters

        public String getServiceId() {
            return serviceId;
        }

        public String getUserId() {
            return userId;
        }

        public String getInstanceId() {
            return instanceId;
        }

        public String getObjectId() {
            return objectId;
        }

        public String getComponentPath() {
            return componentPath;
        }

        public JOSPActionCommandParams getCommand() {
            return command;
        }


        // Casting

        static String fromCmdToMsg(ActionCmdMsg cmd) {
            return String.format(CMD_MSG, JOSPProtocol.getNow(), cmd.getServiceId(), cmd.getUserId(), cmd.getInstanceId(), cmd.getObjectId(),
                    cmd.getComponentPath(), cmd.getCommand().getType(), cmd.getCommand().encode());
        }

    }

}
