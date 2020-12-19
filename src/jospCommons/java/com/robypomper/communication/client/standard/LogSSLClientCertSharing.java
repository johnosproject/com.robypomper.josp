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

package com.robypomper.communication.client.standard;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.client.AbsSSLClientCertSharing;
import com.robypomper.communication.client.events.LogClientLocalEventsListener;
import com.robypomper.communication.client.events.LogClientMessagingEventsListener;
import com.robypomper.communication.client.events.LogClientServerEventsListener;


/**
 * Log example of {@link AbsSSLClientCertSharing} implementation.
 * <p>
 * It use all client's events listeners {@link LogClientLocalEventsListener},
 * {@link LogClientServerEventsListener} and {@link LogClientMessagingEventsListener}.
 */
@SuppressWarnings("unused")
public class LogSSLClientCertSharing extends AbsSSLClientCertSharing {

    // Class constants

    public static final String NAME_PROTO = "https";
    public static final String NAME_SERVER = "Log SSL Cert Sharing Server";


    // Constructor

    public LogSSLClientCertSharing(String clientId, String serverAddr, int serverPort,
                                   String keyStorePath, String keyStorePass, String certAlias, String certPubPath)
            throws UtilsJKS.GenerationException, SSLCertSharingClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsSSL.GenerationException, UtilsJKS.StoreException {
        super(clientId, serverAddr, serverPort,
                keyStorePath, keyStorePass, certAlias, certPubPath,
                new LogClientLocalEventsListener(),
                new LogClientServerEventsListener(),
                new LogClientMessagingEventsListener());
    }


    // Getter configs

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProtocolName() {
        return NAME_PROTO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerName() {
        return NAME_SERVER;
    }

}
