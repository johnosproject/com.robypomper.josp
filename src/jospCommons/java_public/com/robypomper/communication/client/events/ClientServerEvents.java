package com.robypomper.communication.client.events;


/**
 * Client's events from server side.
 */
public interface ClientServerEvents extends ClientEvents {

    // Client connection events

    /**
     * This method is called when the connection between current client and the
     * server is ready.
     */
    void onServerConnection();


    // Server disconnection events

    /**
     * This method is called when the connection between client and the
     * server is closed.
     * <p>
     * This method is always called, differently to methods
     * {@link #onServerClientDisconnected()},
     * {@link #onServerGoodbye()} and
     * {@link #onServerTerminated()} that are called depending on how the
     * connection was closed.
     */
    void onServerDisconnection();

    /**
     * This method is called only when the connection was closed by the client.
     * <p>
     * Before closing the connection, the client send the "goodbye" message to
     * the server, then close gracefully the connection.
     * <p>
     * This method is always called in conjunction to {@link #onServerDisconnection()}
     * method.
     */
    void onServerClientDisconnected();

    /**
     * This method is called only when the connection was closed by the server.
     * <p>
     * The connection is closed gracefully by the server when it send the
     * "goodbye" message before closing the connection.
     * <p>
     * This method is always called in conjunction to {@link #onServerDisconnection()}
     * method.
     */
    void onServerGoodbye();

    /**
     * This method is called only when the connection was truncated by the server.
     * <p>
     * The connection is terminated by the server with out the "goodbye" message.
     * The missing "goodbye" message means a fatal error occur on the server.
     * <p>
     * This method is always called in conjunction to {@link #onServerDisconnection()}
     * method.
     */
    void onServerTerminated();


    // Server errors events

    /**
     * This method is called when occur a server error.
     * <p>
     * From the client, server errors are related to the connection and data tx/rx
     * problems.
     */
    void onServerError(Throwable e);

}
