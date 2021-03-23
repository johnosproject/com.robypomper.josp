package com.robypomper.josp.jcp.apis.mngs.exceptions;

public class GWNotReachableException extends Throwable {

    // Class constants

    private static final String MSG = "JCP GW '%s' not reachable";


    // Internal vars

    private final String gwId;


    // Constructors

    public GWNotReachableException(String gwId, Throwable cause) {
        super(String.format(MSG, gwId), cause);
        this.gwId = gwId;
    }


    // Getters

    public String getGWId() {
        return gwId;
    }

}
