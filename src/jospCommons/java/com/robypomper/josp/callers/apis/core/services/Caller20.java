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

package com.robypomper.josp.callers.apis.core.services;

import com.robypomper.josp.clients.AbsAPISrv;
import com.robypomper.josp.clients.JCPAPIsClientSrv;


/**
 * JOSP Core - Services 2.0
 */
public class Caller20 extends AbsAPISrv {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public Caller20(JCPAPIsClientSrv jcpClient) {
        super(jcpClient);
        //registerToJCP();
    }


    //// Registration methods
    //
    //public void registerToJCP() {
    //    try {
    //        jcpClient.execReq(Verb.POST, Paths20.FULL_PATH_REGISTER, Params20.SrvName.class, isSecure());
    //
    //    } catch (JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException | JCPClient2.ResponseException ignore) {
    //        jcpClient.addConnectionListener(new JCPClient2.ConnectionListener() {
    //            @Override
    //            public void onConnected(JCPClient2 jcpClient) {
    //                registerToJCP();
    //            }
    //
    //            @Override
    //            public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
    //            }
    //
    //            @Override
    //            public void onAuthenticationFailed(JCPClient2 jcpClient, Throwable t) {
    //            }
    //
    //            @Override
    //            public void onDisconnected(JCPClient2 jcpClient) {
    //            }
    //        });
    //    }
    //}

}
