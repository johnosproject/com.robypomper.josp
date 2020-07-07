package com.robypomper.josp.protocol;

public class JOSPProtocol_Service {

    // Full Srv Id

    public static final String FULL_SRV_ID_FORMAT = "%s/%s/%s";

    public static String fullSrvIdToSrvId(String fullSrvId) {
        return fullSrvId.split("/")[0];
    }

    public static String fullSrvIdToUsrId(String fullSrvId) {
        return fullSrvId.split("/")[1];
    }

    public static String fullSrvIdToInstId(String fullSrvId) {
        return fullSrvId.split("/")[2];
    }

}
