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

package com.robypomper.josp.jod.history;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.paths.APIObjs;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.jcpclient.AbsJCPAPIs;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.protocol.JOSPStatusHistory;

import java.util.Collections;
import java.util.List;


/**
 * Support class for API Events access from the JODEvents synchronization.
 */
public class JCPHistory extends AbsJCPAPIs {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public JCPHistory(JCPClient_Object jcpClient, JODSettings_002 settings) {
        super(jcpClient, settings);
    }


    // Generator methods

    /**
     * Upload a single status to the cloud.
     *
     * @param statusHistory the status to upload.
     */
    public void uploadStatusHistory(JOSPStatusHistory statusHistory) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        uploadEvents(Collections.singletonList(statusHistory));
    }


    /**
     * Upload a list of statuses to the cloud.
     *
     * @param statusesHistory the statuses to upload.
     */
    public void uploadEvents(List<JOSPStatusHistory> statusesHistory) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        jcpClient.execReq(Verb.POST, APIObjs.FULL_PATH_HISTORY, statusesHistory, isSecure());
    }

}
