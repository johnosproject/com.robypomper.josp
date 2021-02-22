package com.robypomper.josp.jcp.gws.exceptions;

public class JSLServiceNotInDBException extends Throwable {

    // Class constants

    private static final String MSG = "Service '%s' not found in DB";


    // Internal vars

    private final String srvId;


    // Constructors

    public JSLServiceNotInDBException(String srvId) {
        super(String.format(MSG, srvId));
        this.srvId = srvId;
    }


    // Getters

    public String getSrvId() {
        return srvId;
    }

}
