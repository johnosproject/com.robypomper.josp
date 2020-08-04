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

package com.robypomper.josp.jsl.srvinfo;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jcp.apis.params.srvs.SrvName;
import com.robypomper.josp.jcp.apis.paths.APISrvs;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.jcpclient.AbsJCPAPIs;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;


/**
 * Support class for API Srv access to the service's info.
 */
public class JCPServiceInfo extends AbsJCPAPIs {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public JCPServiceInfo(JCPClient_Service jcpClient, JSLSettings_002 settings) {
        super(jcpClient, settings);
        registerToJCP();
    }

    public void registerToJCP() {
        try {
            jcpClient.execReq(Verb.GET, APISrvs.FULL_PATH_REGISTER, SrvName.class, true);

        } catch (JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException | JCPClient2.ResponseException ignore) {
            jcpClient.addConnectListener(new JCPClient2.ConnectListener() {
                @Override
                public void onConnected(JCPClient2 jcpClient) {
                    registerToJCP();
                }

                @Override
                public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {}
            });
        }
    }

}
