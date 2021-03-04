package com.robypomper.josp.jcp.jslwebbridge.exceptions;

public class WebBridgeException extends Throwable {

    // Internal vars

    private final String sessionId;


    // Constructors

    public WebBridgeException(String msg, String sessionId) {
        super(msg);
        this.sessionId = sessionId;
    }

    public WebBridgeException(String msg, String sessionId, Throwable cause) {
        super(msg, cause);
        this.sessionId = sessionId;
    }


    // Getters

    public String getSessionId() {
        return sessionId;
    }

}
