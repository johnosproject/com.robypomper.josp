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

package com.robypomper.josp.jsl.user;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.params.usrs.UsrName;
import com.robypomper.josp.paths.APIUsrs;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.jcpclient.AbsJCPAPIs;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;


/**
 * Support class for API Usr access to the service's user.
 */
public class JCPUserSrv extends AbsJCPAPIs {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public JCPUserSrv(JCPClient_Service jcpClient, JSLSettings_002 settings) {
        super(jcpClient, settings);
    }


    // Getters methods

    public UsrName getUser() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, APIUsrs.FULL_PATH_USERNAME, UsrName.class, isSecure());
    }

}
