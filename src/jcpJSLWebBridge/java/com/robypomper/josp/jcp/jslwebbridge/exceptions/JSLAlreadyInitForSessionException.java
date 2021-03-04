package com.robypomper.josp.jcp.jslwebbridge.exceptions;

public class JSLAlreadyInitForSessionException extends WebBridgeException {

    // Class constants

    private static final String MSG = "Can't initialize JSL instance because already initialized for session '%s'.";


    // Constructors

    public JSLAlreadyInitForSessionException(String sessionId) {
        super(String.format(MSG, sessionId), sessionId);
    }

}
