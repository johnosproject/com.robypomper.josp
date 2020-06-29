package com.robypomper.communication.server.events;

import com.robypomper.communication.server.ClientInfo;


/**
 * Default implementation of the {@link ServerClientEvents}.
 */
public class DefaultServerClientEventsListener extends DefaultServerEvent implements ServerClientEvents {

    // Client connection events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClientConnection(ClientInfo client) {}


    // Client disconnection events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClientDisconnection(ClientInfo client) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClientServerDisconnected(ClientInfo client) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClientGoodbye(ClientInfo client) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClientTerminated(ClientInfo client) {}


    // Client errors events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClientError(ClientInfo client, Throwable e) {}

}
