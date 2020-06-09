package com.robypomper.josp.protocol;

// {PROTOCOL} {REQUEST/RESPONSE_TYPE} {SEND_TIME}
// {FULL_SRV_ID/OBJ_ID}
// [{key} {value}]
// [...]


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utils class to render and parse the JOSP protocol messages.
 * <p>
 * This class is used by both sides JOD and JSL.
 */
public class JOSPProtocol {

    // Class constants

    public static final String DISCOVERY_TYPE = "_josp2._tcp";


    // Public vars

    private static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
    private static final Calendar calendar = Calendar.getInstance(gmtTimeZone);
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");


    // Called as:
    // public void forwardAction(JSLRemoteObject object/*, JSLAction component, JSLActionCommand command*/) {
    //     String msg = JOSPProtocol.fromCmdToMsg(srvInfo.getFullId(),component.getPath().getString()/*, JSLActionCommand command*/);
    //     ...
    // }
    public static String fromCmdToMsg(String fullSrvId, String componentPath/*, JSLActionCommand command*/) {
        // ToDo implement fromCmdToMsg method
        return "";
    }

    // Called as:
    // ??? obj, component, action = JOSPProtocol.fromMsgToUpd(msg);
    public static StatusUpd fromMsgToUpd(String msg) {
        // ToDo implement fromMsgToUpd method
        return null;
    }


    // Called as:
    // public void dispatchUpdate(JODState component, JODStateUpdate update) {
    //     String updMsg = JOSPProtocol.fromUpdToMsg(objInfo.getObjId(),component.getPath().getString()/*,update*/);
    //     ...
    // }
    public static String fromUpdToMsg(String objId, String componentPath/*, JODStateUpdate update*/) {
        // ToDo implement fromUpdToMsg method
        return "";
    }

    // Called as:
    // ??? service, component, action = JOSPProtocol.fromMsgToCmd(msg);
    public static ActionCmd fromMsgToCmd(String msg) {
        // ToDo implement fromMsgToCmd method
        return null;
    }

    /**
     * Data class to return info contained in status update messages.
     */
    public static class StatusUpd {

        // Internal vars

        private final String objectId;
        private final String componentPath;
        private final Object update;


        // Constructor

        StatusUpd(String objectId, String componentPath, Object update) {
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

        public Object getUpdate() {
            return update;
        }

    }

    /**
     * Data class to return info contained in action command messages.
     */
    public static class ActionCmd {

        // Internal vars

        private final String serviceId;
        private final String componentPath;
        private final Object command;


        // Constructor

        ActionCmd(String serviceId, String componentPath, Object command) {
            this.serviceId = serviceId;
            this.componentPath = componentPath;
            this.command = command;
        }


        // Getters

        public String getServiceId() {
            return serviceId;
        }

        public String getComponentPath() {
            return componentPath;
        }

        public Object getCommand() {
            return command;
        }

    }


    // Utils

    public static String getNow() {
        return dateFormatter.format(calendar.getTime());
    }

    public static String getEpoch() {
        return dateFormatter.format(new Date(0));
    }

    public static SimpleDateFormat getDateFormatter() {
        return dateFormatter;
    }

    public static String extractFieldFromResponse(String msg, int msgMinLines, int fieldLine, String msgName) throws JOSPProtocol.ParsingException {
        String[] lines = msg.split("\n");
        if (lines.length < msgMinLines)
            throw new JOSPProtocol.ParsingException(String.format("Few lines in %s", msgName));

        return lines[fieldLine].substring(lines[fieldLine].indexOf(":") + 1);
    }


    // Exceptions

    /**
     * Exceptions for local communication errors.
     */
    public static class ParsingException extends Throwable {
        public ParsingException(String msg) {
            super(msg);
        }

        public ParsingException(String msg, Throwable e) {
            super(msg, e);
        }
    }

}
