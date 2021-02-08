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


import com.robypomper.communication_deprecated.server.ClientInfo;

/**
 * Server tx and rx events.
 */
public interface ServerMessagingEvents extends ServerEvents {

    // Data send events

    /**
     * This method is called when data are transmitted to the client.
     *
     * @param writtenData data transmitted to the client.
     */
    void onDataSend(ClientInfo client, byte[] writtenData);

    /**
     * This method is called when data are transmitted to the client.
     *
     * @param writtenData data transmitted to the client.
     */
    void onDataSend(ClientInfo client, String writtenData);


    // Data received events

    /**
     * This method is called when data are received from the client.
     *
     * @param readData received from the client.
     */
    boolean onDataReceived(ClientInfo client, byte[] readData) throws Throwable;

    /**
     * This method is called when data are received from the client.
     *
     * @param readData received from the client.
     */
    boolean onDataReceived(ClientInfo client, String readData) throws Throwable;

}
