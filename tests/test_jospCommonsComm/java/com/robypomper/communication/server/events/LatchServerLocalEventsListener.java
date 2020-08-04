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

import java.util.concurrent.CountDownLatch;


public class LatchServerLocalEventsListener extends LogServerLocalEventsListener {

    // Internal vars

    public CountDownLatch onStarted = new CountDownLatch(1);
    public CountDownLatch onStopped = new CountDownLatch(1);
    public CountDownLatch onStopError = new CountDownLatch(1);
    public CountDownLatch onServerError = new CountDownLatch(1);


    // Server start events

    @Override
    public void onStarted() {
        super.onStarted();
        onStarted.countDown();
    }


    // Server stop events

    @Override
    public void onStopped() {
        super.onStopped();
        onStopped.countDown();
    }

    @Override
    public void onStopError(Exception e) {
        super.onStopError(e);
        onStopError.countDown();
    }


    // Server error events

    @Override
    public void onServerError(Exception e) {
        super.onServerError(e);
        onServerError.countDown();
    }

}
