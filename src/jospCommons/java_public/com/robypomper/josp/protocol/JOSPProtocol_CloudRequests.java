package com.robypomper.josp.protocol;

import java.text.ParseException;
import java.util.Date;


// {PROTOCOL} {REQUEST/RESPONSE_TYPE} {SEND_TIME}
// {null/OBJ_ID}
// [{key} {value}]
// [...]

public class JOSPProtocol_CloudRequests {


    private static final String JOSP_PROTO = "JOSP/2.0";
    private static final String OBJ_STRUCT_REQ_BASE = JOSP_PROTO + " OBJ_STRUCT_REQ";
    private static final String OBJ_STRUCT_REQ = OBJ_STRUCT_REQ_BASE + " %s\nlastKnowUpd:%s";
    private static final String OBJ_STRUCT_RES_BASE = JOSP_PROTO + " OBJ_STRUCT_RES";
    private static final String OBJ_STRUCT_RES = OBJ_STRUCT_RES_BASE + " %s\n%s\nlastUpd:%s\nstructure:%s";


    // Object structure (Req+Res)

    public static String createObjectStructureRequest(Date lastKnowUpdate) {
        String lastKnowStr = lastKnowUpdate != null ? JOSPProtocol.getDateFormatter().format(lastKnowUpdate) : JOSPProtocol.getEpoch();
        return String.format(OBJ_STRUCT_REQ, JOSPProtocol.getNow(), lastKnowStr);
    }

    public static boolean isObjectStructureRequest(String msg) {
        return msg.startsWith(OBJ_STRUCT_REQ_BASE);
    }

    public static String createObjectStructureResponse(String objId, Date lastUpdate, String structureStr) {
        return String.format(OBJ_STRUCT_RES, JOSPProtocol.getNow(), objId, JOSPProtocol.getDateFormatter().format(lastUpdate), structureStr);
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

    public static String extractObjectStructureFromResponse(String msg) throws JOSPProtocol.ParsingException {
        String[] lines = msg.split("\n");
        if (lines.length < 3)
            throw new JOSPProtocol.ParsingException("Few lines in ObjectStructureResponse");

        return lines[3].substring(lines[3].indexOf(":") + 1);
    }

}
