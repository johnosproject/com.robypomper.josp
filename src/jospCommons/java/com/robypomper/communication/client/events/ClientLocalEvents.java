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


/**
 * Client's events.
 */
public interface ClientLocalEvents extends ClientEvents {

    // Client connect events

    /**
     * This method is called when the client is connecting to the server.
     */
    void onConnected();


    // Client disconnect events

    /**
     * This method is called when the client disconnect from the server.
     */
    void onDisconnected();

    /**
     * This method is called when occur an error during disconnection from the
     * server.
     */
    void onDisconnectionError(Exception e);

}
