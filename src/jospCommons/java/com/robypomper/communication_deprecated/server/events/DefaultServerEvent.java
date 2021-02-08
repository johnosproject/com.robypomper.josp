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

package com.robypomper.communication_deprecated.server.events;

import com.robypomper.communication_deprecated.server.Server;


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
