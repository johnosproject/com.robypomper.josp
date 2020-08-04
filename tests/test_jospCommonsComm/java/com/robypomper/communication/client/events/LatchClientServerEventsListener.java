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

import java.util.concurrent.CountDownLatch;


public class LatchClientServerEventsListener extends LogClientServerEventsListener {

    // Internal vars

    public CountDownLatch onServerConnection = new CountDownLatch(1);
    public CountDownLatch onServerDisconnection = new CountDownLatch(1);
    public CountDownLatch onServerClientDisconnected = new CountDownLatch(1);
    public CountDownLatch onServerGoodbye = new CountDownLatch(1);
    public CountDownLatch onServerTerminated = new CountDownLatch(1);
    public CountDownLatch onServerError = new CountDownLatch(1);


    // Client connection events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerConnection() {
        super.onServerConnection();
        onServerConnection.countDown();
    }


    // Server disconnection events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerDisconnection() {
        super.onServerDisconnection();
        onServerDisconnection.countDown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerClientDisconnected() {
        super.onServerClientDisconnected();
        onServerClientDisconnected.countDown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerGoodbye() {
        super.onServerGoodbye();
        onServerGoodbye.countDown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerTerminated() {
        super.onServerTerminated();
        onServerTerminated.countDown();
    }


    // Server errors events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerError(Throwable e) {
        super.onServerError(e);
        onServerError.countDown();
    }

}
