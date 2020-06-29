package com.robypomper.communication.server.events;

import com.robypomper.communication.server.ClientInfo;


/**
 * Default implementation of the {@link ServerMessagingEvents}.
 */
public class DefaultServerMessagingEventsListener extends DefaultServerEvent implements ServerMessagingEvents {

    // Data send events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDataSend(ClientInfo client, byte[] writtenData) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDataSend(ClientInfo client, String writtenData) {}


    // Data received events

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onDataReceived(ClientInfo client, byte[] readData) throws Throwable { return false; }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onDataReceived(ClientInfo client, String readData) throws Throwable { return false; }
}
