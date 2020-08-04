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

package com.robypomper.discovery;

import java.util.concurrent.CountDownLatch;


public class TestDiscoverListener implements DiscoverListener {

    // Internal vars

    private final String serviceName;
    public CountDownLatch onServiceDiscovered = new CountDownLatch(1);
    public CountDownLatch onServiceLost = new CountDownLatch(1);

    public TestDiscoverListener(String serviceName) {
        this.serviceName = serviceName;
    }


    // Events

    @Override
    public void onServiceDiscovered(DiscoveryService discSrv) {
        if (discSrv.name.equalsIgnoreCase(serviceName)) {
            System.out.println(String.format("%s\t\tTEST onServiceDiscovered(%s)", "????????", discSrv.name));
            onServiceDiscovered.countDown();
        }
    }

    @Override
    public void onServiceLost(DiscoveryService lostSrv) {
        if (lostSrv.name.equalsIgnoreCase(serviceName)) {
            System.out.println(String.format("%s\t\tTEST onServiceLost(%s)", "????????", lostSrv.name));
            onServiceLost.countDown();
        }
    }

}
