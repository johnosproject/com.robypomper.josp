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

package com.robypomper.communication_deprecated.server.standard;

import com.robypomper.communication_deprecated.UtilsJKS;
import com.robypomper.communication_deprecated.UtilsSSL;
import com.robypomper.communication_deprecated.server.CertSharingSSLServer;
import com.robypomper.communication_deprecated.server.events.LogServerClientEventsListener;
import com.robypomper.communication_deprecated.server.events.LogServerLocalEventsListener;
import com.robypomper.communication_deprecated.server.events.LogServerMessagingEventsListener;


public class LogSSLCertServer extends CertSharingSSLServer {

    public LogSSLCertServer(String serverId, int port,
                            String keyStorePath, String keyStorePass, String certAlias, String certPubPath, boolean requireAuth)
            throws UtilsJKS.GenerationException, UtilsJKS.LoadingException, UtilsSSL.GenerationException, SSLCertServer.SSLCertServerException, UtilsJKS.StoreException {
        super(serverId, port,
                keyStorePath, keyStorePass, certAlias, certPubPath, requireAuth,
                new LogServerLocalEventsListener(),
                new LogServerClientEventsListener(),
                new LogServerMessagingEventsListener());
    }

}