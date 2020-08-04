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
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Log implementation of the {@link ServerClientEvents}.
 * <p>
 * The log implementations log all events with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
 */
public class LogServerClientEventsListener extends DefaultServerEvent implements ServerClientEvents {

    // Internal vars

    protected static final Logger log = LogManager.getLogger();


    // Client connection events

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onClientConnection(ClientInfo client) {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("%s.onClientConnection(%s)", getServer().getServerId(), client.getClientId()));
    }


    // Client disconnection events

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onClientDisconnection(ClientInfo client) {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("%s.onClientDisconnection(%s)", getServer().getServerId(), client.getClientId()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onClientServerDisconnected(ClientInfo client) {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("%s.onClientServerDisconnected(%s)", getServer().getServerId(), client.getClientId()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onClientGoodbye(ClientInfo client) {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("%s.onClientGoodbye(%s)", getServer().getServerId(), client.getClientId()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onClientTerminated(ClientInfo client) {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("%s.onClientTerminated(%s)", getServer().getServerId(), client.getClientId()));
    }


    // Client errors events

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onClientError(ClientInfo client, Throwable e) {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("%s.onClientError(%s, %s)", getServer().getServerId(), client.getClientId(), e.getMessage()));
    }

}
