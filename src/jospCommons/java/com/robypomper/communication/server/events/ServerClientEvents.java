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

package com.robypomper.communication.server.events;


import com.robypomper.communication.server.ClientInfo;

/**
 * Client's events from server side.
 */
public interface ServerClientEvents extends ServerEvents {

    // Client connection events

    /**
     * This method is called when the connection between current client and the
     * server is ready.
     */
    void onClientConnection(ClientInfo client);


    // Client disconnection events

    /**
     * This method is called when the connection between current client and the
     * server is closed.
     * <p>
     * This method is always called, differently to methods
     * {@link #onClientServerDisconnected(ClientInfo)},
     * {@link #onClientGoodbye(ClientInfo)} and
     * {@link #onClientTerminated(ClientInfo)} that are called depending on how the
     * connection was closed.
     */
    void onClientDisconnection(ClientInfo client);

    /**
     * This method is called only when the connection was closed by the server.
     * <p>
     * Before closing the connection, the server send the "goodbye" message to
     * the client, then close gracefully the connection.
     * <p>
     * This method is always called in conjunction to {@link #onClientDisconnection(ClientInfo)}
     * method.
     */
    void onClientServerDisconnected(ClientInfo client);

    /**
     * This method is called only when the connection was closed by the client.
     * <p>
     * The connection is closed gracefully by the client when it send the
     * "goodbye" message before closing the connection.
     * <p>
     * This method is always called in conjunction to {@link #onClientDisconnection(ClientInfo)}
     * method.
     */
    void onClientGoodbye(ClientInfo client);

    /**
     * This method is called only when the connection was truncated by the client.
     * <p>
     * The connection is terminated by the client with out the "goodbye" message.
     * The missing "goodbye" message means a fatal error occur on the client.
     * <p>
     * This method is always called in conjunction to {@link #onClientDisconnection(ClientInfo)}
     * method.
     */
    void onClientTerminated(ClientInfo client);


    // Client errors events

    /**
     * This method is called when occur a client error.
     * <p>
     * From the server, client errors are related to the connection and data tx/rx
     * problems.
     */
    void onClientError(ClientInfo client, Throwable e);

}
