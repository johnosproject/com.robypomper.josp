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

package com.robypomper.josp.jod.objinfo;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jcp.apis.params.objs.GenerateObjId;
import com.robypomper.josp.jcp.apis.paths.APIObjs;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.jcpclient.AbsJCPAPIs;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;


/**
 * Support class for API Objs access to the Object Info generators.
 */
public class JCPObjectInfo extends AbsJCPAPIs {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public JCPObjectInfo(JCPClient_Object jcpClient, JODSettings_002 settings) {
        super(jcpClient, settings);
    }


    // Generator methods

    /**
     * Generate and return a valid Object's Cloud ID.
     *
     * @param objIdHw the object's Hardware ID.
     * @param ownerId the owner's User ID.
     * @param objId   the object's ID.
     * @return the object's Cloud ID.
     */
    public String generateObjIdCloud(String objIdHw, String ownerId, String objId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        GenerateObjId params = new GenerateObjId(objIdHw, ownerId);
        if (objId == null)
            return jcpClient.execReq(Verb.POST, APIObjs.FULL_PATH_GENERATEID, String.class, params, isSecure());
        else
            return jcpClient.execReq(Verb.POST, APIObjs.FULL_PATH_REGENERATEID, String.class, params, isSecure());
    }

}
