package com.robypomper.communication.client.events;

/**
 * Default implementation of the {@link ClientLocalEvents}.
 */
public class DefaultClientLocalEventsListener extends DefaultClientEvents implements ClientLocalEvents {

    // Client connect events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConnected() {}


    // Client disconnect events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisconnected() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisconnectionError(Exception e) {}

}
