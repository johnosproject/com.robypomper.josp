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

import java.util.List;


/**
 * Interface for Discover implementations.
 * <p>
 * Each AbsDiscover instance is looking for a specific type of services. The
 * Discover's type can be queried with the method {@link #getServiceType()}
 */
public interface Discover {

    // Discovery mngm

    /**
     * @return <code>true</code> if the discovery system is running.
     */
    boolean isRunning();

    /**
     * Start the discovery system.
     * <p>
     * After calling this method, the discovery system start to emit
     * {@link DiscoverListener} events on service discovered/lost.
     */
    void start() throws DiscoveryException;

    /**
     * Start the discovery system.
     * <p>
     * After calling this method, the discovery system start to emit
     * {@link DiscoverListener} events on service discovered/lost.
     */
    void stop() throws DiscoveryException;


    // Getters

    /**
     * Return the service type looked from current discover object.
     */
    String getServiceType();

    /**
     * Return all services discovered.
     */
    List<DiscoveryService> getServicesDiscovered();

    /**
     * Return all available interfaces.
     */
    List<String> getInterfaces();


    // Listener mngm

    /**
     * Add given listener to discovery system listener list.
     *
     * @param listener the listener to add.
     */
    void addListener(DiscoverListener listener);

    /**
     * Remove given listener to discovery system listener list.
     *
     * @param listener the listener to remove.
     */
    void removeListener(DiscoverListener listener);


    // Exceptions

    /**
     * Exceptions thrown on discovery errors.
     */
    class DiscoveryException extends Throwable {
        public DiscoveryException(String msg) {
            super(msg);
        }

        public DiscoveryException(String msg, Throwable e) {
            super(msg, e);
        }
    }

}
