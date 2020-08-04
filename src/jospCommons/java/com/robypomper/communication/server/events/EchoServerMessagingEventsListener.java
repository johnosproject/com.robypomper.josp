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

import com.robypomper.communication.peer.PeerInfo;
import com.robypomper.communication.server.ClientInfo;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Echo implementation of the {@link ServerMessagingEvents}.
 * <p>
 * The echo implementation simply response same data received from the client.
 */
public class EchoServerMessagingEventsListener extends DefaultServerEvent implements ServerMessagingEvents {

    // Internal vars

    protected static final Logger log = LogManager.getLogger();


    // Data send events

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onDataSend(ClientInfo client, byte[] writtenData) {
        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("%s.onDataSend(%s, byte[] %s)", getServer().getServerId(), client.getClientId(), new String(writtenData, PeerInfo.CHARSET)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onDataSend(ClientInfo client, String writtenData) {
        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("%s.onDataSend(%s, String %s)", getServer().getServerId(), client.getClientId(), writtenData));
    }


    // Data received events

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public boolean onDataReceived(ClientInfo client, byte[] readData) {
        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("%s.onDataReceived(%s, byte[] %s)", getServer().getServerId(), client.getClientId(), new String(readData, PeerInfo.CHARSET)));
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Process (echo) received data and log the event.
     * <p>
     * This method receive all bytes send by the client and send them back.
     */
    @Override
    public boolean onDataReceived(ClientInfo client, String readData) throws Throwable {
        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("%s.onDataReceived(%s, String %s)", getServer().getServerId(), client.getClientId(), readData));
        getServer().sendData(client, readData);
        return true;
    }
}
