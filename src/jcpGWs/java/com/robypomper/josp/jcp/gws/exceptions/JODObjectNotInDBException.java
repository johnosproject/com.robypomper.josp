package com.robypomper.josp.jcp.gws.exceptions;

public class JODObjectNotInDBException extends Throwable {

    // Class constants

    private static final String MSG = "Object '%s' not found in DB";


    // Internal vars

    private final String objId;


    // Constructors

    public JODObjectNotInDBException(String objId) {
        super(String.format(MSG, objId));
        this.objId = objId;
    }


    // Getters

    public String getObjId() {
        return objId;
    }

}
