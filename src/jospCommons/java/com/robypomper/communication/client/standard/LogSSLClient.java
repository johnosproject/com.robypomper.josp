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

import com.robypomper.communication.client.DefaultSSLClient;
import com.robypomper.communication.client.events.*;

import javax.net.ssl.SSLContext;
import java.net.InetAddress;

public class LogSSLClient extends DefaultSSLClient {

    protected LogSSLClient(SSLContext sslCtx, String clientId, InetAddress serverAddr, int serverPort, ClientLocalEvents clientLocalEventsListener, ClientServerEvents clientServerEventsListener, ClientMessagingEvents clientMessagingEventsListener) {
        super(sslCtx, clientId, serverAddr, serverPort,
                new LogClientLocalEventsListener(),
                new LogClientServerEventsListener(),
                new LogClientMessagingEventsListener());
    }

}
