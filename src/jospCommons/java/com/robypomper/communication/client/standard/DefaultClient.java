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

package com.robypomper.communication.client.standard;

import com.robypomper.communication.client.AbsClient;
import com.robypomper.communication.client.events.ClientLocalEvents;
import com.robypomper.communication.client.events.ClientMessagingEvents;
import com.robypomper.communication.client.events.ClientServerEvents;

import javax.net.ssl.SSLContext;


/**
 * Default implementation of {@link AbsClient}.
 */
@SuppressWarnings("unused")
public class DefaultClient extends AbsClient {

    // Internal vars

    private final String protoName;
    private final String serverName;


    // Constructor

    protected DefaultClient(SSLContext sslCtx, String clientId, String serverAddr, int serverPort, ClientMessagingEvents clientMessagingEventsListener, String protoName, String serverName) {
        this(clientId, serverAddr, serverPort, null, null, clientMessagingEventsListener, protoName, serverName);
    }

    public DefaultClient(String clientId, String serverAddr, int serverPort, ClientLocalEvents clientLocalEventsListener, ClientServerEvents clientServerEventsListener, ClientMessagingEvents clientMessagingEventsListener, String protoName, String serverName) {
        super(clientId, serverAddr, serverPort, clientLocalEventsListener, clientServerEventsListener, clientMessagingEventsListener);
        this.protoName = protoName;
        this.serverName = serverName;
    }


    // Getter configs

    @Override
    public String getProtocolName() {
        return protoName;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

}