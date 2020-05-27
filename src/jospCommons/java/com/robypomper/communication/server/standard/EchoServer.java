package com.robypomper.communication.server.standard;

import com.robypomper.communication.server.DefaultServer;
import com.robypomper.communication.server.events.LogServerClientEventsListener;
import com.robypomper.communication.server.events.LogServerLocalEventsListener;
import com.robypomper.communication.server.events.EchoServerMessagingEventsListener;

public class EchoServer extends DefaultServer {

    public EchoServer(String serverId, int port) {
        super(serverId, port,
                new LogServerLocalEventsListener(),
                new LogServerClientEventsListener(),
                new EchoServerMessagingEventsListener());
    }

}