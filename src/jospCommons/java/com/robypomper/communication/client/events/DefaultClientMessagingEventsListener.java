package com.robypomper.communication.client.events;

/**
 * Default implementation of the {@link ClientMessagingEvents}.
 */
public class DefaultClientMessagingEventsListener extends DefaultClientEvents implements ClientMessagingEvents {

    // Data send events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDataSend(byte[] writtenData) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDataSend(String writtenData) {}


    // Data received events

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onDataReceived(byte[] readData) throws Throwable { return false; }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onDataReceived(String readData) throws Throwable { return false; }

}
