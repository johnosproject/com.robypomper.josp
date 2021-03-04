package com.robypomper.josp.jcp.jslwebbridge.exceptions;

public class JSLNotInitForSessionException extends WebBridgeException {

    // Class constants

    private static final String MSG = "Can't get JSL instance because NOT initialized for session '%s'.";


    // Constructors

    public JSLNotInitForSessionException(String sessionId) {
        super(String.format(MSG, sessionId), sessionId);
    }

}
