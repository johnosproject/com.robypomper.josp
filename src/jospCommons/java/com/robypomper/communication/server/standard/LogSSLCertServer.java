package com.robypomper.communication.server.standard;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.server.CertSharingSSLServer;
import com.robypomper.communication.server.events.LogServerClientEventsListener;
import com.robypomper.communication.server.events.LogServerLocalEventsListener;
import com.robypomper.communication.server.events.LogServerMessagingEventsListener;


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