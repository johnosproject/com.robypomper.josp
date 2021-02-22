package com.robypomper.josp.jcp.gws.exceptions;

import com.robypomper.josp.protocol.JOSPPerm;

public class JSLServiceMissingPermissionException extends Throwable {

    // Class constants

    private static final String MSG = "Service's '%s' have NOT enough permission to send message to Object '%s' (min permission required '%s', current permission '%s')";

    // Internal vars

    private final String srvId;
    private final String objId;
    private final JOSPPerm.Type reqPerm;
    private final JOSPPerm.Type currentPerm;


    // Constructors

    public JSLServiceMissingPermissionException(String srvId, String objId, JOSPPerm.Type reqPerm, JOSPPerm.Type currentPerm) {
        super(String.format(MSG, srvId, objId, reqPerm, currentPerm));
        this.srvId = srvId;
        this.objId = objId;
        this.reqPerm = reqPerm;
        this.currentPerm = currentPerm;
    }


    // Getters

    public String getSrvId() {
        return srvId;
    }

    public String getObjId() {
        return objId;
    }

    public JOSPPerm.Type getMinPermReq() {
        return reqPerm;
    }

    public JOSPPerm.Type getCurrentPerm() {
        return currentPerm;
    }

}
