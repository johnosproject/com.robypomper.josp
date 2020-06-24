package com.robypomper.communication.server.events;

/**
 * Default implementation of the {@link ServerLocalEvents}.
 */
public class DefaultServerLocalEventsListener extends DefaultServerEvent implements ServerLocalEvents {

    // Server start events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStarted() {}


    // Server stop events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStopped() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStopError(Exception e) {}


    // Server error events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerError(Exception e) {}

}
