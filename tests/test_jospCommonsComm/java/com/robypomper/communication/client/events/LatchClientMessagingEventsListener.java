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

public class LatchClientMessagingEventsListener extends LogClientMessagingEventsListener {

    // Internal vars

    public CountDownLatch onDataSendBytes = new CountDownLatch(1);
    public CountDownLatch onDataSendString = new CountDownLatch(1);
    public CountDownLatch onDataReceivedBytes = new CountDownLatch(1);
    public CountDownLatch onDataReceivedString = new CountDownLatch(1);


    // Data send events

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDataSend(byte[] writtenData) {
        super.onDataSend(writtenData);
        onDataSendBytes.countDown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDataSend(String writtenData) {
        super.onDataSend(writtenData);
        onDataSendString.countDown();
    }


    // Data received events

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onDataReceived(byte[] readData) throws Throwable {
        boolean res = super.onDataReceived(readData);
        onDataReceivedBytes.countDown();
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onDataReceived(String readData) throws Throwable {
        boolean res = super.onDataReceived(readData);
        onDataReceivedString.countDown();
        return res;
    }

}
