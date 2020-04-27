package com.robypomper.communication.client.standard;

import com.robypomper.communication.client.DefaultSSLClient;
import com.robypomper.communication.client.events.ClientLocalEvents;
import com.robypomper.communication.client.events.ClientMessagingEvents;
import com.robypomper.communication.client.events.ClientServerEvents;
import com.robypomper.communication.client.events.LogClientLocalEventsListener;
import com.robypomper.communication.client.events.LogClientMessagingEventsListener;
import com.robypomper.communication.client.events.LogClientServerEventsListener;

import javax.net.ssl.SSLContext;
import java.net.InetAddress;

public class LogSSLClient extends DefaultSSLClient {

    protected LogSSLClient(SSLContext sslCtx, String clientId, InetAddress serverAddr, int serverPort, ClientLocalEvents clientLocalEventsListener, ClientServerEvents clientServerEventsListener, ClientMessagingEvents clientMessagingEventsListener) {
        super(sslCtx, clientId, serverAddr, serverPort,
                new LogClientLocalEventsListener(),
                new LogClientServerEventsListener(),
                new LogClientMessagingEventsListener());
    }

}
