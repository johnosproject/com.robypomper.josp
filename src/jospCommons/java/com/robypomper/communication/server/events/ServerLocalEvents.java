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


/**
 * Server's events.
 */
public interface ServerLocalEvents extends ServerEvents {

    // Server start events

    /**
     * This method is called when the server start listening for new client's
     * connections.
     */
    void onStarted();


    // Server stop events

    /**
     * This method is called when the server stop listening for new client's
     * connections.
     */
    void onStopped();

    /**
     * This method is called when occur an error during server stopping.
     */
    void onStopError(Exception e);


    // Server error events

    /**
     * This method is called when an error occur on "server infinite loop" thread.
     * <p>
     * The "server infinite loop" thread is that one that wait for new connection
     * and start "Client processor" threads.
     */
    void onServerError(Exception e);

}
