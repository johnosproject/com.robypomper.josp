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
 * Default implementation of the {@link ServerLocalEvents}.
 */
public class DefaultServerLocalEventsListener extends DefaultServerEvent implements ServerLocalEvents {

    // Server start events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStarted() {}


    // Server stop events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStopped() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStopError(Exception e) {}


    // Server error events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerError(Exception e) {}

}
