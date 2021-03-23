package com.robypomper.josp.jcp.apis.mngs.exceptions;

import com.robypomper.josp.types.josp.gw.GWType;

public class GWNotAvailableException extends Throwable {

    // Class constants

    private static final String MSG = "No JCP GW of '%s' type available";


    // Internal vars

    private final GWType gwType;


    // Constructors

    public GWNotAvailableException(GWType gwType) {
        super(String.format(MSG, gwType));
        this.gwType = gwType;
    }


    // Getters

    public GWType getGWType() {
        return gwType;
    }

}
