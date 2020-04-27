package com.robypomper.communication.client.events;

import com.robypomper.communication.client.Client;
import com.robypomper.communication.client.ServerInfo;


/**
 * Default implementation of the {@link ClientEvents} interface.
 */
public class DefaultClientEvents implements ClientEvents {

    // Internal vars

    private Client client;


    // Setter

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClient(Client client) {
        this.client = client;
    }


    // Getter (protected)

    /**
     * Provide the instance of the client that emits events for current
     * {@link ClientEvents} instance.
     * <p>
     * This method is not included in the interface because it must be accessible
     * only by ClientEvent's implementations.
     *
     * @return the client instance corresponding to current event listener.
     */
    protected Client getClient() {
        return client;
    }

    /**
     * Provide the ServerInfo of the server that current client is connected.
     * <p>
     * This method is not included in the interface because it must be accessible
     * only by ServerEvent's implementations.
     *
     * @return the server instance corresponding to current event listener.
     */
    protected ServerInfo getServer() {
        return client.getServerInfo();
    }

}
