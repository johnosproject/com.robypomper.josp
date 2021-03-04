package com.robypomper.josp.jcp.jslwebbridge.exceptions;

public class EmittersNotInitForSessionException extends WebBridgeException {

    // Class constants

    private static final String MSG = "Can't get emitters list because NOT initialized for session '%s'.";


    // Constructors

    public EmittersNotInitForSessionException(String sessionId) {
        super(String.format(MSG, sessionId), sessionId);
    }

}
