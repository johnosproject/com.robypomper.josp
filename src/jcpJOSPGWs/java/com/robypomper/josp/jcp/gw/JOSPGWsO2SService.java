package com.robypomper.josp.jcp.gw;

import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.events.LogServerClientEventsListener;
import com.robypomper.communication.server.events.LogServerMessagingEventsListener;
import com.robypomper.communication.server.events.ServerClientEvents;
import com.robypomper.communication.server.events.ServerLocalEvents;
import com.robypomper.communication.server.events.ServerMessagingEvents;
import com.robypomper.log.Mrk_Commons;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class JOSPGWsO2SService extends AbsJOSPGWsService {

    // Class constants

    private static final int PORT_MIN = 9101;
    private static final int PORT_MAX = 9200;


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
        return new LogServerClientEventsListener() {

            /**
             * {@inheritDoc}
             * <p>
             * Log the event with {@link Mrk_Commons#COMM_SRV_IMPL} marker and
             * print JOSPGWs servers and clients list.
             */
            @Override
            public void onClientConnection(ClientInfo client) {
                log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("%s.onClientConnection(%s)", getServer().getServerId(), client.getClientId()));

                log.info(Mrk_Commons.COMM_SRV_IMPL, "JOSPGws > Servers > Clients:");
                for (AbsJOSPGWsService gw : getJOSPGWs()) {
                    log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("- %s", gw));
                    for (Map.Entry<String, JOSPGWsO2SService.Server> srv : gw.getJOSPServers().entrySet()) {
                        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("  - %s", srv.getKey()));
                        for (ClientInfo cl : srv.getValue().getClients()) {
                            log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("    - %s", cl.getClientId()));
                        }
                    }
                }
            }

        };
    }

    @Override
    protected ServerMessagingEvents getMessagingEventsListener() {
        return new LogServerMessagingEventsListener();
    }

}
