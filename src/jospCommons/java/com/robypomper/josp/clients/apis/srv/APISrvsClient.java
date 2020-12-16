/* *****************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2020 Roberto Pompermaier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **************************************************************************** */

package com.robypomper.josp.clients.apis.srv;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.AbsAPISrv;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.params.srvs.SrvName;
import com.robypomper.josp.paths.APISrvs;


/**
 * Support class for API Srv access to the service's info.
 */
public class APISrvsClient extends AbsAPISrv {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public APISrvsClient(JCPAPIsClientSrv jcpClient) {
        super(jcpClient);
        registerToJCP();
    }

    public void registerToJCP() {
        try {
            jcpClient.execReq(Verb.GET, APISrvs.FULL_PATH_REGISTER, SrvName.class, isSecure());

        } catch (JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException | JCPClient2.ResponseException ignore) {
            jcpClient.addConnectListener(new JCPClient2.ConnectListener() {
                @Override
                public void onConnected(JCPClient2 jcpClient) {
                    registerToJCP();
                }

                @Override
                public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
                }
            });
        }
    }

}