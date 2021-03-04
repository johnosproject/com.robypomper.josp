package com.robypomper.josp.jcp.jslwebbridge.exceptions;

public class EmittersNotEmptyForSessionException extends WebBridgeException {

    // Class constants

    private static final String MSG = "Can't remove JSL Instance for session '%s' because his emitters list not empty.";


    // Constructors

    public EmittersNotEmptyForSessionException(String sessionId) {
        super(String.format(MSG, sessionId), sessionId);
    }

}
