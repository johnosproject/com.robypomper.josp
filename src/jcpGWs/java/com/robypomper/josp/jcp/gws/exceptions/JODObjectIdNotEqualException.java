package com.robypomper.josp.jcp.gws.exceptions;

public class JODObjectIdNotEqualException extends Throwable {

    // Class constants

    private static final String MSG = "Object's id '%s' on message not equal to current Object instance's id '%s'";

    // Internal vars

    private final String msgObjId;
    private final String currentObjId;


    // Constructors

    public JODObjectIdNotEqualException(String msgObjId, String currentObjId) {
        super(String.format(MSG, msgObjId, currentObjId));
        this.msgObjId = msgObjId;
        this.currentObjId = currentObjId;
    }


    // Getters

    public String getMsgObjId() {
        return msgObjId;
    }

    public String getCurrentObjId() {
        return currentObjId;
    }

}
