package com.robypomper.communication.server.events;

import com.robypomper.communication.server.Server;


/**
 * Default implementation of the {@link ServerEvents} interface.
 */
public class DefaultServerEvent implements ServerEvents {

    // Internal vars

    private Server server;


    // Setter

    /**
     * {@inheritDoc}
     */
    @Override
    public void setServer(Server server) {
        this.server = server;
    }


    // Getter (protected)

    /**
     * Provide the instance of the server that emits events for current
     * {@link ServerEvents} instance.
     * <p>
     * This method is not included in the interface because it must be accessible
     * only by ServerEvent's implementations.
     *
     * @return the server instance corresponding to current event listener.
     */
    protected Server getServer() {
        return server;
    }

}
