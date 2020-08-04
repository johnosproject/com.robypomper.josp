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


public class LatchServerMessagingEventsListener extends LogServerMessagingEventsListener {

    // Internal vars

    public CountDownLatch onDataSendBytes = new CountDownLatch(1);
    public CountDownLatch onDataSendString = new CountDownLatch(1);
    public CountDownLatch onDataReceivedBytes = new CountDownLatch(1);
    public CountDownLatch onDataReceivedString = new CountDownLatch(1);


    // Data send events

    @Override
    public void onDataSend(ClientInfo client, byte[] writtenData) {
        super.onDataSend(client, writtenData);
        onDataSendBytes.countDown();
    }

    @Override
    public void onDataSend(ClientInfo client, String writtenData) {
        super.onDataSend(client, writtenData);
        onDataSendString.countDown();
    }


    // Data received events

    @Override
    public boolean onDataReceived(ClientInfo client, byte[] readData) throws Throwable {
        boolean res = super.onDataReceived(client, readData);
        onDataReceivedBytes.countDown();
        return res;
    }

    @Override
    public boolean onDataReceived(ClientInfo client, String readData) throws Throwable {
        boolean res = super.onDataReceived(client, readData);
        onDataReceivedString.countDown();
        return res;
    }

}
