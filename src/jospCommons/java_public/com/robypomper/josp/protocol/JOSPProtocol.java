package com.robypomper.josp.protocol;


/**
 * Utils class to render and parse the JOSP protocol messages.
 * <p>
 * This class is used by both sides JOD and JSL.
 */
public class JOSPProtocol {

    // Class constants

    public static final String DISCOVERY_TYPE = "_josp2._tcp";


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

}
