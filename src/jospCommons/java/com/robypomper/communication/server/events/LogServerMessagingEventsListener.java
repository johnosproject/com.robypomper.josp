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

import com.robypomper.communication.CommunicationBase;
import com.robypomper.communication.server.ClientInfo;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Log implementation of the {@link ServerMessagingEvents}.
 * <p>
 * The log implementations log all events with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
 */
public class LogServerMessagingEventsListener extends DefaultServerEvent implements ServerMessagingEvents {

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
        String dataTruncated = CommunicationBase.truncateMid(writtenData, 30);
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("%-10s.onDataSend     (>Cli: %s, byte[%d] %s)", getServer().getServerId(), client.getClientId(), writtenData.length, dataTruncated));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onDataSend(ClientInfo client, String writtenData) {
    }


    // Data received events

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public boolean onDataReceived(ClientInfo client, byte[] readData) throws Throwable {
        String dataTruncated = CommunicationBase.truncateMid(readData, 30);
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("%-10s.onDataReceived (<Cli: %s, byte[%d] %s)", getServer().getServerId(), client.getClientId(), readData.length, dataTruncated));
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public boolean onDataReceived(ClientInfo client, String readData) throws Throwable {
        return false;
    }

}
