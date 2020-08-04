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

import java.util.concurrent.CountDownLatch;


public class LatchServerClientEventsListener extends LogServerClientEventsListener {

    // Internal vars

    public CountDownLatch onClientConnection = new CountDownLatch(1);
    public CountDownLatch onClientDisconnection = new CountDownLatch(1);
    public CountDownLatch onClientServerDisconnected = new CountDownLatch(1);
    public CountDownLatch onClientGoodbye = new CountDownLatch(1);
    public CountDownLatch onClientTerminated = new CountDownLatch(1);
    public CountDownLatch onClientError = new CountDownLatch(1);


    // Client connection events

    @Override
    public void onClientConnection(ClientInfo client) {
        super.onClientConnection(client);
        onClientConnection.countDown();
    }


    // Client disconnection events

    @Override
    public void onClientDisconnection(ClientInfo client) {
        super.onClientDisconnection(client);
        onClientDisconnection.countDown();
    }

    @Override
    public void onClientServerDisconnected(ClientInfo client) {
        super.onClientServerDisconnected(client);
        onClientServerDisconnected.countDown();
    }

    @Override
    public void onClientGoodbye(ClientInfo client) {
        super.onClientGoodbye(client);
        onClientGoodbye.countDown();
    }

    @Override
    public void onClientTerminated(ClientInfo client) {
        super.onClientTerminated(client);
        onClientTerminated.countDown();
    }


    // Client errors events

    @Override
    public void onClientError(ClientInfo client, Throwable t) {
        super.onClientError(client, t);
        onClientError.countDown();
    }

}
