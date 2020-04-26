package com.robypomper.communication.server.events;


/**
 * Server's events.
 */
public interface ServerLocalEvents extends ServerEvents {

    // Server start events

    /**
     * This method is called when the server start listening for new client's
     * connections.
     */
    void onStarted();


    // Server stop events

    /**
     * This method is called when the server stop listening for new client's
     * connections.
     */
    void onStopped();

    /**
     * This method is called when occur an error during server stopping.
     */
    void onStopError(Exception e);


    // Server error events

    /**
     * This method is called when an error occur on "server infinite loop" thread.
     * <p>
     * The "server infinite loop" thread is that one that wait for new connection
     * and start "Client processor" threads.
     */
    void onServerError(Exception e);

}
