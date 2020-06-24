package com.robypomper.communication.client.events;

/**
 * Default implementation of the {@link ClientServerEvents}.
 */
public class DefaultClientServerEventsListener extends DefaultClientEvents implements ClientServerEvents {


    // Client connection events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerConnection() {}


    // Server disconnection events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerDisconnection() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerClientDisconnected() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerGoodbye() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerTerminated() {}


    // Server errors events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerError(Throwable e) {}

}
