package com.robypomper.communication.client.events;


/**
 * Client's events.
 */
public interface ClientLocalEvents extends ClientEvents {

    // Client connect events

    /**
     * This method is called when the client is connecting to the server.
     */
    void onConnected();


    // Client disconnect events

    /**
     * This method is called when the client disconnect from the server.
     */
    void onDisconnected();

    /**
     * This method is called when occur an error during disconnection from the
     * server.
     */
    void onDisconnectionError(Exception e);

}
