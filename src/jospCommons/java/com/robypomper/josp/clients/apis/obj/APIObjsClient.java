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

package com.robypomper.josp.clients.apis.obj;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.AbsAPIObj;
import com.robypomper.josp.clients.JCPAPIsClientObj;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.params.objs.GenerateObjId;
import com.robypomper.josp.paths.APIObjs;
import com.robypomper.josp.protocol.JOSPStatusHistory;

import java.util.Collections;
import java.util.List;


/**
 * Support class for API Objs access to the Object Info generators.
 */
@SuppressWarnings("unused")
public class APIObjsClient extends AbsAPIObj {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public APIObjsClient(JCPAPIsClientObj jcpClient) {
        super(jcpClient);
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

    /**
     * Upload a single status to the cloud.
     *
     * @param statusHistory the status to upload.
     */
    public void uploadStatusHistory(JOSPStatusHistory statusHistory) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        uploadStatusHistory(Collections.singletonList(statusHistory));
    }


    // History methods

    /**
     * Upload a list of statuses to the cloud.
     *
     * @param statusesHistory the statuses to upload.
     */
    public void uploadStatusHistory(List<JOSPStatusHistory> statusesHistory) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        jcpClient.execReq(Verb.POST, APIObjs.FULL_PATH_HISTORY, statusesHistory, isSecure());
    }

}
