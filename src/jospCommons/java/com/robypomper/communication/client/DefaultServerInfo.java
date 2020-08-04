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

package com.robypomper.communication.client;

import com.robypomper.communication.peer.DefaultPeerInfo;

import java.net.Socket;


/**
 * Default implementation of ServerInfo interface.
 */
public class DefaultServerInfo extends DefaultPeerInfo implements ServerInfo {

    // Internal vars

    private final String serverId;
    private final Thread thread;


    // Constructor

    /**
     * Default constructor that initialize the ServerInfo.
     *
     * @param socket   the communication channel socket.
     * @param serverId the represented client's id.
     * @param thread   the thread charged to process all client requests.
     */
    public DefaultServerInfo(Socket socket, String serverId, Thread thread) {
        super(socket);
        this.serverId = serverId;
        this.thread = thread;
    }


    // Server getters

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerId() {
        return serverId;
    }


    // Thread getters

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProcessorRunning() {
        return thread.isAlive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Thread getThread() {
        return thread;
    }

}
