package com.robypomper.josp.jcp.gw;

import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.events.DefaultServerClientEventsListener;
import com.robypomper.communication.server.events.DefaultServerMessagingEventsListener;
import com.robypomper.communication.server.events.ServerClientEvents;
import com.robypomper.communication.server.events.ServerLocalEvents;
import com.robypomper.communication.server.events.ServerMessagingEvents;
import com.robypomper.josp.jcp.db.ServiceDBService;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class JOSPGWsS2OService extends AbsJOSPGWsService {

    // Class constants

    private static final int PORT_MIN = 9201;
    private static final int PORT_MAX = 9300;


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private static final Map<String, GWService> services = new HashMap<>();          // static because shared among all JOSPGWsS2O
    @Autowired
    private ServiceDBService serviceDBService;
    @Autowired
    private JOSPGWsBroker gwBroker;


    // Clients connection

    /**
     * Create {@link GWService} instance and send ObjectStructure of all
     * available (and allowed) objects to connected service.
     */
    private void onClientConnection(com.robypomper.communication.server.Server server, ClientInfo client) {
        // Check if objects already know
        log.trace(Mrk_Commons.COMM_SRV_IMPL, String.format("Checks if connected object '%s' already know", client.getClientId()));
        GWService gwSrv = services.get(client.getClientId());
        if (gwSrv != null) {
            // multiple connection at same time
            client.closeConnection();
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Service '%s' already connected to JOSP GW, disconnecting", client.getClientId()));
            return;
        }

        try {
            gwSrv = new GWService(server, client, serviceDBService, gwBroker);
        } catch (GWService.ServiceNotRegistered serviceNotRegistered) {
            client.closeConnection();
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Service '%s' not registered to JOSP GW, disconnecting", client.getClientId()));
            return;
        }
        services.put(client.getClientId(), gwSrv);
    }

    private void onClientDisconnection(ClientInfo client) {
        try {
            GWService disconnectedService = services.remove(client.getClientId());
            disconnectedService.setOffline();
            gwBroker.deregisterService(disconnectedService);

        } catch (NullPointerException e) {
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Error on client disconnection because service '%s' not known", client.getClientId()));
        }
    }

    private boolean onDataReceived(ClientInfo client, String readData) throws Throwable {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("Data '%s...' received from '%s' service", readData.substring(0, readData.indexOf("\n")), client.getClientId()));
        GWService srv = services.get(client.getClientId());
        return srv.processFromServiceMsg(readData);
    }


    // JOSPGWsService implementations

    /**
     * {@inheritDoc}
     */
    @Override
    int getMinPort() {
        return PORT_MIN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getMaxPort() {
        return PORT_MAX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ServerLocalEvents getServerEventsListener() {
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Link the {@link ServerClientEvents#onClientConnection(ClientInfo)} and
     * {@link ServerClientEvents#onClientDisconnection(ClientInfo)} events to
     * {@link #onClientConnection(com.robypomper.communication.server.Server, ClientInfo)} and
     * {@link #onClientDisconnection(ClientInfo)} methods.
     */
    @Override
    protected ServerClientEvents getClientEventsListener() {
        return new DefaultServerClientEventsListener() {

            /**
             * {@inheritDoc}
             * <p>
             * Link to the {@link JOSPGWsS2OService#onClientConnection(com.robypomper.communication.server.Server, ClientInfo)} method.
             */
            @Override
            public void onClientConnection(ClientInfo client) {
                JOSPGWsS2OService.this.onClientConnection(getServer(), client);
            }

            /**
             * {@inheritDoc}
             * <p>
             * Link to the {@link JOSPGWsS2OService#onClientDisconnection(ClientInfo)} method.
             */
            @Override
            public void onClientDisconnection(ClientInfo client) {
                JOSPGWsS2OService.this.onClientDisconnection(client);
            }

        };
    }

    /**
     * {@inheritDoc}
     * <p>
     * Link the {@link ServerMessagingEvents#onDataReceived(ClientInfo, String)}
     * event to {@link #onDataReceived(ClientInfo, String)} method.
     */
    @Override
    protected ServerMessagingEvents getMessagingEventsListener() {
        return new DefaultServerMessagingEventsListener() {

            /**
             * {@inheritDoc}
             * <p>
             * Link to the {@link JOSPGWsS2OService#onDataReceived(ClientInfo, String)} method.
             */
            @Override
            public boolean onDataReceived(ClientInfo client, String readData) throws Throwable {
                return JOSPGWsS2OService.this.onDataReceived(client, readData);
            }

        };
    }

}
