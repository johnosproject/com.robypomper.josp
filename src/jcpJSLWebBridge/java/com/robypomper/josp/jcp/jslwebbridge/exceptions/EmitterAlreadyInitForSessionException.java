package com.robypomper.josp.jcp.jslwebbridge.exceptions;

public class EmitterAlreadyInitForSessionException extends WebBridgeException {

    // Class constants

    private static final String MSG = "Can't initialize emitter because already initialized for session '%s' at address '%s'.";


    // Internal vars
    private final String clientAddress;


    // Constructors

    public EmitterAlreadyInitForSessionException(String sessionId, String clientAddress) {
        super(String.format(MSG, sessionId, clientAddress), sessionId);
        this.clientAddress = clientAddress;
    }


    // Getters

    public String getClientAddress() {
        return clientAddress;
    }

}
