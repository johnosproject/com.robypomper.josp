package com.robypomper.josp.protocol;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

}
