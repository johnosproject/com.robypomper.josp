package com.robypomper.communication.client.standard;

import com.robypomper.communication.client.DefaultClient;
import com.robypomper.communication.client.events.LogClientLocalEventsListener;
import com.robypomper.communication.client.events.LogClientMessagingEventsListener;
import com.robypomper.communication.client.events.LogClientServerEventsListener;

import java.net.InetAddress;


public class LogClient extends DefaultClient {

    public LogClient(String clientId, InetAddress serverAddr, int serverPort) {
        super(clientId, serverAddr, serverPort,
                new LogClientLocalEventsListener(),
                new LogClientServerEventsListener(),
                new LogClientMessagingEventsListener());
    }

}
