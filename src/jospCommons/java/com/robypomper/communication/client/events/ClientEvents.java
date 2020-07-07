package com.robypomper.communication.client.events;

import com.robypomper.communication.client.Client;


/**
 * Base interface for client's events interfaces.
 */
public interface ClientEvents {

    // Setter

    /**
     * Set the client instance to current event listener instance.
     *
     * This method is called from client to set himself to current listener
     * instance.
     *
     * @param client the client instance corresponding to current event listener.
     */
    void setClient(Client client);

}
