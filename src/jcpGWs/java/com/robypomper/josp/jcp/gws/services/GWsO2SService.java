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
import com.robypomper.josp.jcp.clients.apis.gws.JCPAPIGWsClient;
import com.robypomper.josp.jcp.db.apis.EventDBService;
import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.db.apis.StatusHistoryDBService;
import com.robypomper.josp.jcp.gws.broker.GWsBroker;
import com.robypomper.josp.jcp.gws.o2s.GWObject;
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
public class GWsO2SService extends AbsGWsService {


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private static final Map<String, GWObject> objects = new HashMap<>();          // static because shared among all JOSPGWsO2S
    @Autowired
    private ObjectDBService objectDBService;
    @Autowired
    private PermissionsDBService permissionsDBService;
    @Autowired
    private EventDBService eventsDBService;
    @Autowired
    private StatusHistoryDBService statusesHistoryDBService;
    @Autowired
    private GWsBroker gwBroker;
    private final JCPAPIGWsClient gwsAPI;
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
    public GWsO2SService(@Value("${jcp.gws.o2s.url}") final String hostName,
                         @Value("${jcp.gws.o2s.port}") final int port,
                         @Value("${server.port}") final int apisPort,
                         @Value("${jcp.gws.o2s.maxClients}") final int maxClients,
                         JCPAPIsClient apisClient) {
        super(hostName, port);
        this.gwsAPI = new JCPAPIGWsClient(apisClient);
        this.hostName = hostName;
        this.apisPort = apisPort;
        this.maxClients = maxClients;
        this.gwStatus = new JCPGWsStatus(0, maxClients, null, null);

        try {
            getServer().start();

        } catch (com.robypomper.communication.server.Server.ListeningException ignore) {
        }
    }

    
    // Object's clients connection

    /**
     * Create {@link GWObject} instance and send ObjectStructure request to
     * connected object.
     */
    private void onClientConnection(com.robypomper.communication.server.Server server, ClientInfo client) {
        // Check if objects already know
        log.trace(Mrk_Commons.COMM_SRV_IMPL, String.format("Checks if connected object '%s' already connected", client.getClientId()));
        GWObject gwObj = objects.get(client.getClientId());
        if (gwObj != null) {
            client.closeConnection();
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Object '%s' already connected to JOSP GW, disconnecting", client.getClientId()));
            return;
        }

        // Create GWObject (that register to GW Broker) and add to active services
        gwObj = new GWObject(server, client, objectDBService, permissionsDBService, eventsDBService, statusesHistoryDBService, gwBroker);
        objects.put(client.getClientId(), gwObj);

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
            GWObject disconnectedObject = objects.remove(client.getClientId());
            disconnectedObject.setOffline();
            gwBroker.deregisterObject(disconnectedObject);
        } catch (NullPointerException e) {
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Error on client disconnection because object '%s' not known", client.getClientId()));
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


    // Object's data received

    private boolean onDataReceived(ClientInfo client, String readData) throws Throwable {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("Data '%s...' received from '%s' object", readData.substring(0, readData.indexOf("\n")), client.getClientId()));
        GWObject obj = objects.get(client.getClientId());
        int count = 0;
        while (obj == null && count < 5) {
            count++;
            try {
                Thread.sleep(100);

            } catch (InterruptedException e) {
                return false;
            }
            obj = objects.get(client.getClientId());
        }

        return obj != null && obj.processFromObjectMsg(readData);
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
                    JCPGWsStartup gwStartup = new JCPGWsStartup(GWType.Obj2Srv, hostName, getServer().getPort(), hostName, apisPort, maxClients, JCPGWsVersions.VER_JCPGWs_O2S_2_0);
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
             * Link to the {@link GWsO2SService#onClientConnection(com.robypomper.communication.server.Server, ClientInfo)} method.
             */
            @Override
            public void onClientConnection(ClientInfo client) {
                GWsO2SService.this.onClientConnection(getServer(), client);
            }

            /**
             * {@inheritDoc}
             * <p>
             * Link to the {@link GWsO2SService#onClientDisconnection(ClientInfo)} method.
             */
            @Override
            public void onClientDisconnection(ClientInfo client) {
                GWsO2SService.this.onClientDisconnection(client);
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
             * Link to the {@link GWsO2SService#onDataReceived(ClientInfo, String)} method.
             */
            @Override
            public boolean onDataReceived(ClientInfo client, String readData) throws Throwable {
                return GWsO2SService.this.onDataReceived(client, readData);
            }

        };
    }

}
