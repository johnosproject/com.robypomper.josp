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

import com.robypomper.josp.core.jcpclient.DefaultJCPClient2;
import com.robypomper.josp.paths.APIObjs;
import com.robypomper.josp.jod.JODSettings_002;


/**
 * Object default implementation of {@link DefaultJCPClient2} class.
 * <p>
 * This class initialize a JCPClient object that can be used by Objects.
 */
public class DefaultJCPClient_Object extends DefaultJCPClient2 implements JCPClient_Object {

    // Constructor

    public DefaultJCPClient_Object(JODSettings_002 settings) {
        super(settings.getJCPId(),
                settings.getJCPSecret(),
                settings.getJCPUrlAPIs(),
                settings.getJCPUseSSL(),
                settings.getJCPUrlAuth(),
                "openid",
                "",
                "jcp",
                settings.getJCPRefreshTime());
    }


    // Headers default values setters

    @Override
    public void setObjectId(String objId) {
        if (objId != null && !objId.isEmpty())
            addDefaultHeader(APIObjs.HEADER_OBJID, objId);
        else
            removeDefaultHeader(APIObjs.HEADER_OBJID);
    }

}
