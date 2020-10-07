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

package com.robypomper.josp.jsl.jcpclient;

import com.robypomper.josp.core.jcpclient.DefaultJCPClient2;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jcp.apis.paths.APISrvs;
import com.robypomper.josp.jsl.JSLSettings_002;


/**
 * Service default implementation of {@link DefaultJCPClient2} class.
 * <p>
 * This class initialize a JCPClient object that can be used by Objects.
 */
public class DefaultJCPClient_Service extends DefaultJCPClient2 implements JCPClient_Service, JCPClient2.ConnectListener {

    private final JSLSettings_002 locSettings;

    // Constructor

    public DefaultJCPClient_Service(JSLSettings_002 settings) {
        super(settings.getJCPId(),
                settings.getJCPSecret(),
                settings.getJCPUrlAPIs(),
                settings.getJCPUseSSL(),
                settings.getJCPUrlAuth(),
                "openid",
                settings.getJCPCallback(),
                "jcp",
                settings.getJCPAuthCodeRefreshToken(),
                settings.getJCPRefreshTime());
        addConnectListener(this);
        locSettings = settings;
    }


    // Headers default values setters

    @Override
    public void setServiceId(String srvId) {
        if (srvId != null && !srvId.isEmpty())
            addDefaultHeader(APISrvs.HEADER_SRVID, srvId);
        else
            removeDefaultHeader(APISrvs.HEADER_SRVID);
    }

    @Override
    public void setUserId(String usrId) {
        if (usrId != null && !usrId.isEmpty())
            addDefaultHeader(APISrvs.HEADER_USRID, usrId);
        else
            removeDefaultHeader(APISrvs.HEADER_USRID);
    }


    @Override
    public void setLoginCodeAndReconnect(String loginCode) throws ConnectionException, AuthenticationException, JCPNotReachableException {
        disconnect();
        setLoginCode(loginCode);
        connect();
    }

    // Store refresh tokens

    @Override
    public void onConnected(JCPClient2 jcpClient) {
        if (jcpClient.isClientCredentialFlowEnabled())
            locSettings.setJCPAuthCodeRefreshToken(null);
        if (jcpClient.isAuthCodeFlowEnabled())
            locSettings.setJCPAuthCodeRefreshToken(getAuthCodeRefreshToken());
    }

    @Override
    public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {}

}
