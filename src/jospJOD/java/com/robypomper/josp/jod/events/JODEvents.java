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

package com.robypomper.josp.jod.events;


import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.protocol.JOSPEvent;

import java.io.IOException;

/**
 * Interface for Object's events.
 * <p>
 * Events are a collection of structured data that describe what happen on an
 * object. Each object's system register an event when something occurs (see
 * {@link com.robypomper.josp.protocol.JOSPEvent.Type} class).
 * <p>
 * Events are only request by services. No events updates are send to services.
 * <p>
 * This system provide the event registration methid used to all other systems
 * to register events. It also listen the JCPClient connection to sync the
 * events to the Cloud (via APIs) each time a new event is registered. When the
 * Cloud connection is down, it preserve the events on local file.<br>
 * The same file used to provide object's events when requested by a service
 * locally. In fact, when a service require the object's events, first it send
 * the request to the cloud. Only if the cloud is not available, then the service
 * send the events request directly to the object.
 */
public interface JODEvents {

    // Object's systems

    void setJCPClient(JCPClient_Object jcpClient);


    // Register new event

    void register(JOSPEvent.Type type, String phase, String payload);

    void register(JOSPEvent.Type type, String phase, String payload, Throwable error);


    // Mngm methods

    /**
     * Start syncing events to the cloud.
     *
     * When started, Events system uploads all buffered events to the cloud,
     * then each time a new event is registered it's also immediately sync to
     * the cloud.
     *
     * Until it's stopped.
     *
     * If the cloud is not available, then Events system register to the
     * {@link com.robypomper.josp.core.jcpclient.JCPClient2} connection
     * listener. When the connection become available, it uploads all buffered
     * events to the cloud.
     */
    void startCloudSync();

    /**
     * Stop syncing events to the cloud.
     *
     * When stopped, the Events system stop to sync registered events to the
     * cloud. It store latest sync event id for next {@link #startCloudSync()}
     * call.
     */
    void stopCloudSync();

    /**
     * Store all events on file and empty the buffer.
     */
    void storeCache() throws IOException;
}
