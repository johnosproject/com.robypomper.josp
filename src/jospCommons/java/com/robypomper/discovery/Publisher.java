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
 * Interface for Publisher implementations.
 * <p>
 * Each AbsPublisher instance represent a single service that can be published or
 * hide.
 */
public interface Publisher {

    // Publication mngm

    /**
     * @return <code>true</code> if the represented service's info are published on ALL available interfaces.
     */
    boolean isPublishedFully();

    /**
     * @return <code>true</code> if the represented service's info are published at least on one interface.
     */
    boolean isPublishedPartially();

    /**
     * Publish represented service.
     *
     * @param waitForPublished stop current thread until the service is published.
     */
    void publish(boolean waitForPublished) throws PublishException;

    /**
     * De-publish represented service.
     *
     * @param waitForDepublished stop current thread until the service is de-published.
     */
    void hide(boolean waitForDepublished) throws PublishException;


    // Getters

    /**
     * @return the service name to publish.
     */
    String getServiceName();

    /**
     * @return the service type to publish.
     */
    String getServiceType();

    /**
     * @return the service port to publish.
     */
    int getServicePort();

    /**
     * @return the extra text related with service.
     */
    String getServiceExtraText();

    /**
     * @return the internal discover to check if current service is published or not.
     */
    Discover getInternalDiscovered();

    /**
     * Return all available interfaces.
     */
    List<String> getInterfaces();


    // Exceptions

    /**
     * Exceptions thrown on publication/hiding errors.
     */
    class PublishException extends Throwable {
        public PublishException(String msg) {
            super(msg);
        }

        public PublishException(String msg, Throwable e) {
            super(msg, e);
        }
    }

}
