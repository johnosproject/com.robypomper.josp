package com.robypomper.josp.protocol;

import java.text.ParseException;
import java.util.Date;


// {PROTOCOL} {REQUEST/RESPONSE_TYPE} {SEND_TIME}
// {null/OBJ_ID}
// [{key} {value}]
// [...]

public class JOSPProtocol_CloudRequests {

    private static final String OBJ_INFO_RES_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_INFO_RES";
    private static final String OBJ_INFO_RES = OBJ_INFO_RES_BASE + " %s\n%s\nobjName:%s\nowner:%s\njodVer:%s";
    private static final String OBJ_DISCONNECT_RES_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_DISCONNECT";
    private static final String OBJ_DISCONNECT_RES = OBJ_DISCONNECT_RES_BASE + " %s\n%s";
    private static final String OBJ_STRUCT_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_STRUCT_REQ";
    private static final String OBJ_STRUCT_REQ = OBJ_STRUCT_REQ_BASE + " %s\nlastKnowUpd:%s";
    private static final String OBJ_STRUCT_RES_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_STRUCT_RES";
    private static final String OBJ_STRUCT_RES = OBJ_STRUCT_RES_BASE + " %s\n%s\nlastUpd:%s\nstructure:%s\nconnected:%s";


    // Generic

    public static boolean isMsgToObject(String msg) {
        String[] lines = msg.split("\n");
        if (lines.length < 2)
            return false;

        if (lines[1].length() != 17)
            return false;

        if (lines[1].indexOf('-') != 5 && lines[1].lastIndexOf('-') != 11)
            return false;

        return true;
    }

    public static String extractObjectIdFromResponse(String msg) throws JOSPProtocol.ParsingException {
        String[] lines = msg.split("\n");
        if (lines.length < 2)
            throw new JOSPProtocol.ParsingException("Few lines in object's response");

        return lines[1];
    }

    // Object structure (Res)

    public static String createObjectInfoResponse(String objId, String objName, String ownerId, String jodVersion) {
        return String.format(OBJ_INFO_RES, JOSPProtocol.getNow(), objId, objName, ownerId, jodVersion);
    }

    public static boolean isObjectInfoRequestResponse(String msg) {
        return msg.startsWith(OBJ_INFO_RES_BASE);
    }

    public static String extractObjectInfoObjNameFromResponse(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 5, 2, "ObjectInfoResponse");
    }

    public static String extractObjectInfoOwnerIdFromResponse(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 5, 3, "ObjectInfoResponse");
    }

    public static String extractObjectInfoJodVersionFromResponse(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 5, 4, "ObjectInfoResponse");
    }


    // Object structure (Res)

    public static String createObjectDisconnectionResponse(String objId) {
        return String.format(OBJ_DISCONNECT_RES, JOSPProtocol.getNow(), objId);
    }

    public static boolean isObjectDisconnectRequestResponse(String msg) {
        return msg.startsWith(OBJ_DISCONNECT_RES_BASE);
    }


    // Object structure (Req+Res)

    public static String createObjectStructureRequest(Date lastKnowUpdate) {
        String lastKnowStr = lastKnowUpdate != null ? JOSPProtocol.getDateFormatter().format(lastKnowUpdate) : JOSPProtocol.getEpoch();
        return String.format(OBJ_STRUCT_REQ, JOSPProtocol.getNow(), lastKnowStr);
    }

    public static boolean isObjectStructureRequest(String msg) {
        return msg.startsWith(OBJ_STRUCT_REQ_BASE);
    }

    public static String createObjectStructureResponse(String objId, Date lastUpdate, String structureStr, boolean connected) {
        return String.format(OBJ_STRUCT_RES, JOSPProtocol.getNow(), objId, JOSPProtocol.getDateFormatter().format(lastUpdate), structureStr, connected);
    }

    public static boolean isObjectStructureRequestResponse(String msg) {
        return msg.startsWith(OBJ_STRUCT_RES_BASE);
    }

    public static Date extractObjectStructureLastUpdateFromResponse(String msg) throws JOSPProtocol.ParsingException {
        String[] lines = msg.split("\n");
        if (lines.length < 4)
            throw new JOSPProtocol.ParsingException("Few lines in ObjectStructureResponse");

        String dateStr = lines[2].substring(lines[2].indexOf(":") + 1);
        try {
            return JOSPProtocol.getDateFormatter().parse(dateStr);
        } catch (ParseException e) {
            throw new JOSPProtocol.ParsingException(String.format("Invalid 'lastUpd' field in ObjectStructureResponse ('%s')", dateStr));
        }
    }

    public static String extractObjectStructureStructureFromResponse(String msg) throws JOSPProtocol.ParsingException {
        String[] lines = msg.split("\n");
        if (lines.length < 3)
            throw new JOSPProtocol.ParsingException("Few lines in ObjectStructureResponse");

        return lines[3].substring(lines[3].indexOf(":") + 1);
    }

    public static boolean extractObjectStructureConnectedFromResponse(String msg) throws JOSPProtocol.ParsingException {
        String[] lines = msg.split("\n");
        if (lines.length < 4)
            throw new JOSPProtocol.ParsingException("Few lines in ObjectStructureResponse");

        return Boolean.parseBoolean(lines[4].substring(lines[4].indexOf(":") + 1));
    }

}
