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
    private static final String OBJ_SETNAME_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_SETNAME_REQ";
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
    private static final String OBJ_SETOWNERID_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_SETOWNERID_REQ";
    private static final String OBJ_SETOWNERID_REQ = OBJ_SETOWNERID_REQ_BASE + " %s\nfullSrvId:%s\nobjId:%s\nobjName:%s";

    public static String createObjectSetOwnerIdMsg(String fullSrvId, String objId, String newOwnerId) {
        return String.format(OBJ_SETOWNERID_REQ, JOSPProtocol.getNow(), fullSrvId, objId, newOwnerId);
    }

    public static boolean isObjectSetOwnerIdMsg(String msg) {
        return msg.startsWith(OBJ_SETOWNERID_REQ_BASE);
    }

    public static String getObjectSetOwnerIdMsg_OwnerId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 4, 3, OBJ_SETOWNERID_REQ_NAME);
    }


    // Action Cmd Msg class
    // ToDo rename to ActionCmdMsg

    private static final String CMD_MSG_BASE = JOSPProtocol.JOSP_PROTO + " CMD_MSG";
    private static final String CMD_MSG = CMD_MSG_BASE + " %s\nfullSrvId:%s/%s/%s\nobjId:%s\ncompPath:%s\ncmdType:%s\n%s";

    public static String createObjectCmdMsg(String fullSrvId, String objId, String compPath, JOSPActionCommandParams command) {
        String srvId = JOSPProtocol_Service.fullSrvIdToSrvId(fullSrvId);
        String usrId = JOSPProtocol_Service.fullSrvIdToUsrId(fullSrvId);
        String instId = JOSPProtocol_Service.fullSrvIdToInstId(fullSrvId);
        return ActionCmdMsg.fromCmdToMsg(new ActionCmdMsg(srvId, usrId, instId, objId, compPath, command));
    }

    public static boolean isObjectCmdMsg(String msg) {
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
