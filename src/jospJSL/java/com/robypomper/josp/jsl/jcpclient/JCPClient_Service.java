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

import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jcp.apis.paths.APISrvs;


/**
 * Main JCP client interface for Services.
 * <p>
 * JCP Client must act as anonymous user client and as authenticated user client.
 * Depending on user login, all requests send to JCP must be performed using the
 * right client authentication flow. Basically, when the login code (from
 * Authentication Flow) is set ({@link #setLoginCode(String)}) then this class
 * starts to send request using the authenticated client. Otherwise it use the
 * anonymous client, that use the Client Credential Flow.
 * <p>
 * The user authentication process can be handle by external classes via the
 * {@link com.robypomper.josp.core.jcpclient.JCPClient2.LoginListener}. It's an observer that throw login and logout events.
 * So his implementations can handle user's logins and logouts.
 */
public interface JCPClient_Service extends JCPClient2 {

    // Headers default values setters

    /**
     * When set the srvId will used as {@value APISrvs#HEADER_SRVID} header
     * value for each request send to the server.
     *
     * @param srvId the current service id, or null to reset it.
     */
    void setServiceId(String srvId);

    /**
     * When set the usrId will used as {@value APISrvs#HEADER_USRID} header
     * value for each request send to the server.
     *
     * @param usrId the current logged user id, or null to reset it.
     */
    void setUserId(String usrId);


    // Login

    void setLoginCodeAndReconnect(String loginCode) throws ConnectionException, AuthenticationException, JCPNotReachableException;

}
