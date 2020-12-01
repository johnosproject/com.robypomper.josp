/* *****************************************************************************
 * The John Cloud Platform set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.josp.jcp.gws.services;

import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.events.*;
import com.robypomper.java.JavaThreads;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.clients.JCPAPIsClient;
import com.robypomper.josp.jcp.clients.apis.gws.JCPAPIGWsClient;
import com.robypomper.josp.jcp.db.apis.ServiceDBService;
import com.robypomper.josp.jcp.gws.broker.GWsBroker;
import com.robypomper.josp.jcp.gws.s2o.GWService;
import com.robypomper.josp.jcp.params.jcp.JCPGWsStatus;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.types.josp.gw.GWType;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;


@Service
public class GWsS2OService extends AbsGWsService implements JCPClient2.ConnectListener {


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private static final Map<String, GWService> services = new HashMap<>();          // static because shared among all JOSPGWsS2O
    @Autowired
    private ServiceDBService serviceDBService;
    @Autowired
    private GWsBroker gwBroker;
    private final JCPAPIGWsClient gwsAPI;
    private final String hostName;
    private final int apisPort;
    private final int maxClients;
    private final JCPGWsStatus gwStatus;

    /**
     * Initialize JOSPGWsService with only one internal server.
     */
    @Autowired
    public GWsS2OService(@Value("${jcp.gws.s2o.url}") final String hostName,
                         @Value("${jcp.gws.s2o.port}") final int port,
                         @Value("${server.port}") final int apisPort,
                         @Value("${jcp.gws.s2o.maxClients}") final int maxClients,
                         JCPAPIsClient apisClient) {
        super(hostName, port);
        this.gwsAPI = new JCPAPIGWsClient(apisClient);
        apisClient.addConnectListener(this);
        this.hostName = hostName;
        this.apisPort = apisPort;
        this.maxClients = maxClients;
        this.gwStatus = new JCPGWsStatus(0, maxClients, null, null);

        try {
            getServer().start();

        } catch (com.robypomper.communication.server.Server.ListeningException ignore) {
        }
    }

    @PreDestroy
    public void destroy() {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("Halt server %s", getServer().getServerId()));
        getServer().stop();

        if (getServer().isRunning())
            JavaThreads.softSleep(1000);

        if (getServer().isRunning()) {
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Gateway %s not halted, force it.", this.getClass().getSimpleName()));
            deregister(gwsAPI, hostName);
        } else
            log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("Halted %s gateway", this.getClass().getSimpleName()));
    }


    // Clients connection

    /**
     * Create {@link GWService} instance and send ObjectStructure of all
     * available (and allowed) objects to connected service.
     */
    private void onClientConnection(com.robypomper.communication.server.Server server, ClientInfo client) {
        // Check if objects already know
        log.trace(Mrk_Commons.COMM_SRV_IMPL, String.format("Checks if connected service '%s' already know", client.getClientId()));
        GWService gwSrv = services.get(client.getClientId());
        if (gwSrv != null) {
            // multiple connection at same time
            client.closeConnection();
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Service '%s' already connected to JOSP GW, disconnecting", client.getClientId()));
            return;
        }

        // Create GWService (that register to GW Broker) and add to active services
        try {
            gwSrv = new GWService(server, client, serviceDBService, gwBroker);
        } catch (GWService.ServiceNotRegistered serviceNotRegistered) {
            client.closeConnection();
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Service '%s' not registered to JOSP GW, disconnecting", client.getClientId()));
            return;
        }
        services.put(client.getClientId(), gwSrv);

        // Update GW status to JCP APIs
        gwStatus.clients++;
        assert gwStatus.clients == services.size();
        gwStatus.lastClientConnectedAt = JOSPProtocol.getNowDate();
        update(gwsAPI, hostName, gwStatus);
    }

    private void onClientDisconnection(ClientInfo client) {
        // Check if client is a registered service
        log.trace(Mrk_Commons.COMM_SRV_IMPL, String.format("Checks if disconnected client '%s' was a service's client", client.getClientId()));
        if (services.get(client.getClientId()) == null)
            return;

        // Deregister GWService from GW Broker and remove from active services
        try {
            GWService disconnectedService = services.remove(client.getClientId());
            disconnectedService.setOffline();
            gwBroker.deregisterService(disconnectedService);

        } catch (NullPointerException e) {
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Error on client disconnection because service '%s' not known", client.getClientId()));
        }

        // Update GW status to JCP APIs
        gwStatus.clients--;
        assert gwStatus.clients == services.size();
        gwStatus.lastClientDisconnectedAt = JOSPProtocol.getNowDate();
        update(gwsAPI, hostName, gwStatus);
    }

    private boolean onDataReceived(ClientInfo client, String readData) {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("Data '%s...' received from '%s' service", readData.substring(0, readData.indexOf("\n")), client.getClientId()));
        GWService srv = services.get(client.getClientId());
        return srv.processFromServiceMsg(readData);
    }


    // AbsGWsService implementations

    /**
     * {@inheritDoc}
     */
    @Override
    protected ServerLocalEvents getServerEventsListener() {
        return new DefaultServerLocalEventsListener() {

            @Override
            public void onStarted() {
                register(gwsAPI, hostName, apisPort, maxClients, GWType.Srv2Obj);
                update(gwsAPI, hostName, gwStatus);
            }

            @Override
            public void onStopped() {
                deregister(gwsAPI, hostName);
            }

        };
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
             * Link to the {@link GWsS2OService#onClientConnection(com.robypomper.communication.server.Server, ClientInfo)} method.
             */
            @Override
            public void onClientConnection(ClientInfo client) {
                GWsS2OService.this.onClientConnection(getServer(), client);
            }

            /**
             * {@inheritDoc}
             * <p>
             * Link to the {@link GWsS2OService#onClientDisconnection(ClientInfo)} method.
             */
            @Override
            public void onClientDisconnection(ClientInfo client) {
                GWsS2OService.this.onClientDisconnection(client);
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
             * Link to the {@link GWsS2OService#onDataReceived(ClientInfo, String)} method.
             */
            @Override
            public boolean onDataReceived(ClientInfo client, String readData) throws Throwable {
                return GWsS2OService.this.onDataReceived(client, readData);
            }

        };
    }


    // JCP APIs connection listeners

    @Override
    public void onConnected(JCPClient2 jcpClient) {
        if (getServer().isRunning()) {
            register(gwsAPI, hostName, apisPort, maxClients, GWType.Srv2Obj);
            update(gwsAPI, hostName, gwStatus);

        } else {
            deregister(gwsAPI, hostName);
        }
    }

    @Override
    public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
    }

}
