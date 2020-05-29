package com.robypomper.josp.jcp.gw;

import com.robypomper.communication.server.events.LogServerMessagingEventsListener;
import com.robypomper.communication.server.events.ServerClientEvents;
import com.robypomper.communication.server.events.ServerLocalEvents;
import com.robypomper.communication.server.events.ServerMessagingEvents;
import org.springframework.stereotype.Service;


@Service
public class JOSPGWsS2OService extends AbsJOSPGWsService {

    // Class constants

    private static final int PORT_MIN = 9201;
    private static final int PORT_MAX = 9300;


    // JOSPGWsService implementations

    @Override
    int getMinPort() {
        return PORT_MIN;
    }

    @Override
    int getMaxPort() {
        return PORT_MAX;
    }

    @Override
    protected ServerLocalEvents getServerEventsListener() {
        return null;
    }

    @Override
    protected ServerClientEvents getClientEventsListener() {
        return null;
    }

    @Override
    protected ServerMessagingEvents getMessagingEventsListener() {
        return new LogServerMessagingEventsListener();
    }

}
