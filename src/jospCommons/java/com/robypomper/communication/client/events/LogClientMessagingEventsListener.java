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

import com.robypomper.communication.CommunicationBase;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Log implementation of the {@link ClientMessagingEvents}.
 * <p>
 * The log implementations log all events with {@link Mrk_Commons#COMM_CL_IMPL} marker.
 */
public class LogClientMessagingEventsListener extends DefaultClientEvents implements ClientMessagingEvents {

    // Internal vars

    protected static final Logger log = LogManager.getLogger();


    // Data send events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDataSend(byte[] writtenData) {
        String dataTruncated = CommunicationBase.truncateMid(writtenData, 30);
        log.info(Mrk_Commons.COMM_CL_IMPL, String.format("%-10s.onDataSend     (>Srv: %s, byte[%d] %s)", getClient().getClientId(), (getClient().getServerUrl() != null ? getClient().getServerUrl() : "UNKNOW"), writtenData.length, dataTruncated));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDataSend(String writtenData) {
    }


    // Data received events

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onDataReceived(byte[] readData) throws Throwable {
        String dataTruncated = CommunicationBase.truncateMid(readData, 30);
        log.info(Mrk_Commons.COMM_CL_IMPL, String.format("%-10s.onDataReceived (<Srv: %s, byte[%d] %s)", getClient().getClientId(), getClient().getServerUrl(), readData.length, dataTruncated));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onDataReceived(String readData) throws Throwable {
        return false;
    }

}
