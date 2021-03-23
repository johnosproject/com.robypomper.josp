package com.robypomper.josp.jcp.apis.mngs.exceptions;

public class GWNotFoundException extends Throwable {

    // Class constants

    private static final String MSG = "JCP GW '%s' not registered in JCP APIs";


    // Internal vars

    private final String gwId;


    // Constructors

    public GWNotFoundException(String gwId) {
        super(String.format(MSG, gwId));
        this.gwId = gwId;
    }


    // Getters

    public String getGWId() {
        return gwId;
    }

}
