package com.robypomper.communication.client.standard;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.UtilsSSL;
import com.robypomper.communication.client.CertSharingSSLClient;
import com.robypomper.communication.client.events.LogClientLocalEventsListener;
import com.robypomper.communication.client.events.LogClientMessagingEventsListener;
import com.robypomper.communication.client.events.LogClientServerEventsListener;

import java.net.InetAddress;

public class LogSSLCertSharingClient extends CertSharingSSLClient {

    public LogSSLCertSharingClient(String clientId, InetAddress serverAddr, int serverPort,
                                   String keyStorePath, String keyStorePass, String certAlias, String certPubPath)
            throws UtilsJKS.GenerationException, SSLCertClient.SSLCertClientException, UtilsJKS.LoadingException, UtilsSSL.GenerationException, UtilsJKS.StoreException {
        super(clientId, serverAddr, serverPort,
                keyStorePath, keyStorePass, certAlias, certPubPath,
                new LogClientLocalEventsListener(),
                new LogClientServerEventsListener(),
                new LogClientMessagingEventsListener());
    }

}
