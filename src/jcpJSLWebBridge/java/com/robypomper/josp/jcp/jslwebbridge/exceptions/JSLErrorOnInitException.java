package com.robypomper.josp.jcp.jslwebbridge.exceptions;

public class JSLErrorOnInitException extends WebBridgeException {

    // Class constants

    private static final String MSG = "Error on init JSL instance for session '%s'.";


    // Constructors

    public JSLErrorOnInitException(String sessionId, Throwable cause) {
        super(String.format(MSG, sessionId), sessionId, cause);
    }

}
