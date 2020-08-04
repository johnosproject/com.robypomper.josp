/* *****************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

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
