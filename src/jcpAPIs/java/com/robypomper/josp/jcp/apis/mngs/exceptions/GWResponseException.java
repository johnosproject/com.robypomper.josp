package com.robypomper.josp.jcp.apis.mngs.exceptions;

public class GWResponseException extends Throwable {

    // Class constants

    private static final String MSG = "JCP GW '%s' returned bad response (%s)";


    // Internal vars

    private final String gwId;


    // Constructors

    public GWResponseException(String gwId, Throwable cause) {
        super(String.format(MSG, gwId, cause), cause);
        this.gwId = gwId;
    }


    // Getters

    public String getGWId() {
        return gwId;
    }

}
