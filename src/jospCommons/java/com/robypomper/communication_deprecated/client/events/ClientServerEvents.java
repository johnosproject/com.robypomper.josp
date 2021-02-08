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

package com.robypomper.communication_deprecated.client.events;


/**
 * Client's events from server side.
 */
public interface ClientServerEvents extends ClientEvents {

    // Client connection events

    /**
     * This method is called when the connection between current client and the
     * server is ready.
     */
    void onServerConnection();


    // Server disconnection events

    /**
     * This method is called when the connection between client and the
     * server is closed.
     * <p>
     * This method is always called, differently to methods
     * {@link #onServerClientDisconnected()},
     * {@link #onServerGoodbye()} and
     * {@link #onServerTerminated()} that are called depending on how the
     * connection was closed.
     */
    void onServerDisconnection();

    /**
     * This method is called only when the connection was closed by the client.
     * <p>
     * Before closing the connection, the client send the "goodbye" message to
     * the server, then close gracefully the connection.
     * <p>
     * This method is always called in conjunction to {@link #onServerDisconnection()}
     * method.
     */
    void onServerClientDisconnected();

    /**
     * This method is called only when the connection was closed by the server.
     * <p>
     * The connection is closed gracefully by the server when it send the
     * "goodbye" message before closing the connection.
     * <p>
     * This method is always called in conjunction to {@link #onServerDisconnection()}
     * method.
     */
    void onServerGoodbye();

    /**
     * This method is called only when the connection was truncated by the server.
     * <p>
     * The connection is terminated by the server with out the "goodbye" message.
     * The missing "goodbye" message means a fatal error occur on the server.
     * <p>
     * This method is always called in conjunction to {@link #onServerDisconnection()}
     * method.
     */
    void onServerTerminated();


    // Server errors events

    /**
     * This method is called when occur a server error.
     * <p>
     * From the client, server errors are related to the connection and data tx/rx
     * problems.
     */
    void onServerError(Throwable e);

}
