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

package com.robypomper.communication_deprecated.client.events;

import com.robypomper.communication_deprecated.client.Client;


/**
 * Base interface for client's events interfaces.
 */
public interface ClientEvents {

    // Setter

    /**
     * Set the client instance to current event listener instance.
     * <p>
     * This method is called from client to set himself to current listener
     * instance.
     *
     * @param client the client instance corresponding to current event listener.
     */
    void setClient(Client client);

}
