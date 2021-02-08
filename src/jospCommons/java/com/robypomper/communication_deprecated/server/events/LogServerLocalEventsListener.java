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

package com.robypomper.communication_deprecated.server.events;

import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Log implementation of the {@link ServerLocalEvents}.
 * <p>
 * The log implementations log all events with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
 */
public class LogServerLocalEventsListener extends DefaultServerEvent implements ServerLocalEvents {

    // Internal vars

    protected static final Logger log = LogManager.getLogger();


    // Server start events

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onStarted() {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("%s.onStarted()", getServer().getServerId()));
    }


    // Server stop events

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onStopped() {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("%s.onStopped()", getServer().getServerId()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onStopError(Exception e) {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("%s.onStopError(%s)", getServer().getServerId(), e.getMessage()));
    }


    // Server error events

    /**
     * {@inheritDoc}
     * <p>
     * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker.
     */
    @Override
    public void onServerError(Exception e) {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("%s.onServerError(%s)", getServer().getServerId(), e.getMessage()));
    }

}
