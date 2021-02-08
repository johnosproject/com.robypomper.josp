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

import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Log implementation of the {@link ClientLocalEvents}.
 * <p>
 * The log implementations log all events with {@link Mrk_Commons#COMM_CL_IMPL} marker.
 */
public class LogClientLocalEventsListener extends DefaultClientEvents implements ClientLocalEvents {

    // Internal vars

    protected static final Logger log = LogManager.getLogger();


    // Client connect events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConnected() {
        log.info(Mrk_Commons.COMM_CL_IMPL, String.format("%s.onConnected()", getClient().getClientId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConnectionError(Throwable e) {
        log.info(Mrk_Commons.COMM_CL_IMPL, String.format("%s.onConnectionError([%s] %s)", getClient().getClientId(), e.getClass(), e.getMessage()));
    }


    // Client disconnect events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisconnected() {
        log.info(Mrk_Commons.COMM_CL_IMPL, String.format("%s.onDisconnected()", getClient().getClientId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisconnectionError(Throwable e) {
        log.info(Mrk_Commons.COMM_CL_IMPL, String.format("%s.onDisconnectionError([%s] %s)", getClient().getClientId(), e.getClass(), e.getMessage()));
    }

}
