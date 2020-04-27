package com.robypomper.communication.server.standard;

import com.robypomper.communication.server.DefaultServer;
import com.robypomper.communication.server.events.LogServerClientEventsListener;
import com.robypomper.communication.server.events.LogServerLocalEventsListener;
import com.robypomper.communication.server.events.LogServerMessagingEventsListener;


public class LogServer extends DefaultServer {

    public LogServer(String serverId, int port) {
        super(serverId, port,
                new LogServerLocalEventsListener(),
                new LogServerClientEventsListener(),
                new LogServerMessagingEventsListener());
    }

}
