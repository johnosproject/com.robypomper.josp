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

package com.robypomper.josp.jod.jcpclient;

import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.paths.APIObjs;


/**
 * Main JCP client interface for Objects.
 */
public interface JCPClient_Object extends JCPClient2 {

    // Headers default values setters

    /**
     * When set the objId will used as {@value APIObjs#HEADER_OBJID} header
     * value for each request send to the server.
     *
     * @param objId the current object id, or null to reset it.
     */
    void setObjectId(String objId);

}
