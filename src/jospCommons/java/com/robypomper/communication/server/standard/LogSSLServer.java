package com.robypomper.communication.server.standard;

import com.robypomper.communication.server.DefaultSSLServer;
import com.robypomper.communication.server.events.LogServerClientEventsListener;
import com.robypomper.communication.server.events.LogServerLocalEventsListener;
import com.robypomper.communication.server.events.LogServerMessagingEventsListener;

import javax.net.ssl.SSLContext;


public class LogSSLServer extends DefaultSSLServer {

    public LogSSLServer(SSLContext sslCtx, String serverId, int port, boolean requireAuth) {
        super(sslCtx, serverId, port, requireAuth,
                new LogServerLocalEventsListener(),
                new LogServerClientEventsListener(),
                new LogServerMessagingEventsListener());
    }

}