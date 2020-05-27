package com.robypomper.josp.protocol;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


// {PROTOCOL} {REQUEST/RESPONSE_TYPE} {SEND_TIME}
// {FULL_SRV_ID/OBJ_ID}
// [{key} {value}]
// [...]

public class JOSPProtocol_ServiceRequests {


    private static final String JOSP_PROTO = "JOSP/2.0";
    private static final String OBJ_INFO_REQ_BASE = JOSP_PROTO + " OBJ_INFO_REQ";
    private static final String OBJ_INFO_REQ = OBJ_INFO_REQ_BASE + " %s\n%s";
    private static final String OBJ_INFO_RES_BASE = JOSP_PROTO + " OBJ_INFO_RES";
    private static final String OBJ_INFO_RES = OBJ_INFO_RES_BASE + " %s\n%s\nobjName:%s\nowner:%s\njodVer:%s";
    private static final String OBJ_STRUCT_REQ_BASE = JOSP_PROTO + " OBJ_STRUCT_REQ";
    private static final String OBJ_STRUCT_REQ = OBJ_STRUCT_REQ_BASE + " %s\n%s\nlastKnowUpd:%s";
    private static final String OBJ_STRUCT_RES_BASE = JOSP_PROTO + " OBJ_STRUCT_RES";
    private static final String OBJ_STRUCT_RES = OBJ_STRUCT_RES_BASE + " %s\n%s\nlastUpd:%s\nstructure:%s";

    private static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
    private static final Calendar calendar = Calendar.getInstance(gmtTimeZone);
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");


    public static String getNow() {
        return formatter.format(calendar.getTime());
    }

    public static String extractFieldFromResponse(String msg, int msgMinLines, int fieldLine, String msgName) throws JOSPProtocol.ParsingException {
        String[] lines = msg.split("\n");
        if (lines.length < msgMinLines)
            throw new JOSPProtocol.ParsingException(String.format("Few lines in %s", msgName));

        return lines[fieldLine].substring(lines[fieldLine].indexOf(":") + 1);
    }

    // Object structure (Req+Res)

    public static String createObjectInfoRequest(String fullSrvId) {
        return String.format(OBJ_INFO_REQ, getNow(), fullSrvId);
    }

    public static boolean isObjectInfoRequest(String msg) {
        return msg.startsWith(OBJ_INFO_REQ_BASE);
    }

    public static String createObjectInfoResponse(String objId, String objName, String ownerId, String jodVersion) {
        return String.format(OBJ_INFO_RES, getNow(), objId, objName, ownerId, jodVersion);
    }

    public static boolean isObjectInfoRequestResponse(String msg) {
        return msg.startsWith(OBJ_INFO_RES_BASE);
    }

    public static String extractObjectInfoObjNameFromResponse(String msg) throws JOSPProtocol.ParsingException {
        return extractFieldFromResponse(msg, 5, 2, "ObjectInfoResponse");
    }

    public static String extractObjectInfoOwnerIdFromResponse(String msg) throws JOSPProtocol.ParsingException {
        return extractFieldFromResponse(msg, 5, 3, "ObjectInfoResponse");
    }

    public static String extractObjectInfoJodVersionFromResponse(String msg) throws JOSPProtocol.ParsingException {
        return extractFieldFromResponse(msg, 5, 4, "ObjectInfoResponse");
    }


    // Object structure (Req+Res)

    public static String createObjectStructureRequest(String fullSrvId, Date lastKnowUpdate) {
        String lastStr = lastKnowUpdate != null ? formatter.format(lastKnowUpdate) : getNow();
        return String.format(OBJ_STRUCT_REQ, getNow(), fullSrvId, lastStr);
    }

    public static boolean isObjectStructureRequest(String msg) {
        return msg.startsWith(OBJ_STRUCT_REQ_BASE);
    }

    public static String createObjectStructureResponse(String objId, Date lastUpdate, String structureStr) {
        return String.format(OBJ_STRUCT_RES, getNow(), objId, formatter.format(lastUpdate), structureStr);
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
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            throw new JOSPProtocol.ParsingException(String.format("Invalid 'lastUpd' field in ObjectStructureResponse ('%s')", dateStr));
        }
    }

    public static String extractObjectStructureFromResponse(String msg) throws JOSPProtocol.ParsingException {
        String[] lines = msg.split("\n");
        if (lines.length < 3)
            throw new JOSPProtocol.ParsingException("Few lines in ObjectStructureResponse");

        return lines[3].substring(lines[3].indexOf(":") + 1);
    }

}