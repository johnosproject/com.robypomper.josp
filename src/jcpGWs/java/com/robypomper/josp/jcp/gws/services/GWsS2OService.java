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
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.clients.JCPAPIsClient;
import com.robypomper.josp.jcp.clients.apis.jcp.APIJCPGWsClient;
import com.robypomper.josp.jcp.db.apis.ServiceDBService;
import com.robypomper.josp.jcp.gws.broker.GWsBroker;
import com.robypomper.josp.jcp.gws.s2o.GWService;
import com.robypomper.josp.jcp.info.JCPFEVersions;
import com.robypomper.josp.jcp.info.JCPGWsVersions;
import com.robypomper.josp.jcp.params.jcp.JCPGWsStartup;
import com.robypomper.josp.jcp.params.jcp.JCPGWsStatus;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.types.josp.gw.GWType;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class GWsS2OService extends AbsGWsService {


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private static final Map<String, GWService> services = new HashMap<>();          // static because shared among all JOSPGWsS2O
    @Autowired
    private ServiceDBService serviceDBService;
    @Autowired
    private GWsBroker gwBroker;
    private final APIJCPGWsClient gwsAPI;
    private final String hostName;
    private final int apisPort;
    private final int maxClients;
    private final JCPGWsStatus gwStatus;

    /**
     * Initialize JOSPGWsService with only one internal server.
     *
     * @param hostName
     */
    @Autowired
    public GWsS2OService(@Value("${jcp.gws.s2o.url}") final String hostName,
                         @Value("${jcp.gws.s2o.port}") final int port,
                         @Value("${server.port}") final int apisPort,
                         @Value("${jcp.gws.s2o.maxClients}") final int maxClients,
                         JCPAPIsClient apisClient) {
        super(hostName, port);
        this.gwsAPI = new APIJCPGWsClient(apisClient);
        this.hostName = hostName;
        this.apisPort = apisPort;
        this.maxClients = maxClients;
        this.gwStatus = new JCPGWsStatus(0, maxClients, null, null);

        try {
            getServer().start();

        } catch (com.robypomper.communication.server.Server.ListeningException ignore) {
        }
    }


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
        String gwId = generateGWId(hostName, server.getPort());
        gwStatus.clients++;
        gwStatus.lastClientConnectedAt = JOSPProtocol.getNowDate();
        try {
            gwsAPI.postStatus(gwStatus, gwId);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Can't update JCP GW '%s' status to JCP APIs because '%s'.", gwId, e.getMessage()), e);
        }
    }

    private void onClientDisconnection(ClientInfo client) {
        // Deregister GWService from GW Broker and remove from active services
        try {
            GWService disconnectedService = services.remove(client.getClientId());
            disconnectedService.setOffline();
            gwBroker.deregisterService(disconnectedService);

        } catch (NullPointerException e) {
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Error on client disconnection because service '%s' not known", client.getClientId()));
        }

        // Update GW status to JCP APIs
        String gwId = generateGWId(hostName, getPort());
        gwStatus.clients--;
        gwStatus.lastClientDisconnectedAt = JOSPProtocol.getNowDate();
        try {
            gwsAPI.postStatus(gwStatus, gwId);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Can't update JCP GW '%s' status to JCP APIs because '%s'.", gwId, e.getMessage()), e);
        }
    }

    private boolean onDataReceived(ClientInfo client, String readData) throws Throwable {
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
                String gwId = generateGWId(hostName, getServer().getPort());
                try {
                    JCPGWsStartup gwStartup = new JCPGWsStartup(GWType.Srv2Obj, hostName, getServer().getPort(), hostName, apisPort, maxClients, JCPGWsVersions.VER_JCPGWs_S2O_2_0);
                    gwsAPI.postStartup(gwStartup, gwId);

                } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
                    log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Can't register JCP GW '%s' startup to JCP APIs because '%s'.", gwId, e.getMessage()), e);
                }
            }

            @Override
            public void onStopped() {
                String gwId = generateGWId(hostName, getServer().getPort());
                try {
                    gwsAPI.postShutdown(gwId);

                } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
                    log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Can't register JCP GW '%s' startup to JCP APIs because '%s'.", gwId, e.getMessage()), e);
                }
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

}
