package com.robypomper.communication.server.events;

import com.robypomper.communication.server.Server;

/**
 * Base interface for server's events interfaces.
 */
public interface ServerEvents {

    // Setter

    /**
     * Set the server instance to current event listener instance.
     *
     * This method is called from server to set himself to current listener
     * instance.
     *
     * @param server the server instance corresponding to current event listener.
     */
    void setServer(Server server);

}
