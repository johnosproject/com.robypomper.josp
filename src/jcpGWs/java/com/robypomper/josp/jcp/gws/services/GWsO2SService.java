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
import com.robypomper.josp.jcp.db.apis.EventDBService;
import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.db.apis.StatusHistoryDBService;
import com.robypomper.josp.jcp.gws.broker.GWsBroker;
import com.robypomper.josp.jcp.gws.o2s.GWObject;
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
public class GWsO2SService extends AbsGWsService implements JCPClient2.ConnectListener {


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
    private final int apisPort;
    private final int maxClients;
    private final JCPGWsStatus gwStatus;


    /**
     * Initialize JOSPGWsService with only one internal server.
     */
    @Autowired
    public GWsO2SService(@Value("${jcp.gws.o2s.ip.internal}") final String hostnameInternal,
                         @Value("${jcp.gws.o2s.ip.public}") final String hostnamePublic,
                         @Value("${jcp.gws.o2s.port}") final int port,
                         @Value("${server.port}") final int apisPort,
                         @Value("${jcp.gws.o2s.maxClients}") final int maxClients,
                         JCPAPIsClient apisClient) {
        super(hostnameInternal, hostnamePublic, port);
        this.gwsAPI = new JCPAPIGWsClient(apisClient);
        apisClient.addConnectListener(this);
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
            deregister(gwsAPI, getInternalAddress());
        } else
            log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("Halted %s gateway", this.getClass().getSimpleName()));
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
        gwStatus.clients++;
        assert gwStatus.clients == objects.size();
        gwStatus.lastClientConnectedAt = JOSPProtocol.getNowDate();
        update(gwsAPI, getInternalAddress(), gwStatus);
    }

    private void onClientDisconnection(ClientInfo client) {
        // Check if client is a registered service
        log.trace(Mrk_Commons.COMM_SRV_IMPL, String.format("Checks if disconnected client '%s' was a object's client", client.getClientId()));
        if (objects.get(client.getClientId()) == null)
            return;

        // Deregister GWService from GW Broker and remove from active services
        try {
            GWObject disconnectedObject = objects.remove(client.getClientId());
            disconnectedObject.setOffline();
            gwBroker.deregisterObject(disconnectedObject);
        } catch (NullPointerException e) {
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Error on client disconnection because object '%s' not known", client.getClientId()));
        }

        // Update GW status to JCP APIs
        gwStatus.clients--;
        assert gwStatus.clients == objects.size();
        gwStatus.lastClientDisconnectedAt = JOSPProtocol.getNowDate();
        update(gwsAPI, getInternalAddress(), gwStatus);
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
                register(gwsAPI, getInternalAddress(), getPublicAddress(), apisPort, maxClients, GWType.Obj2Srv);
                update(gwsAPI, getInternalAddress(), gwStatus);
            }

            @Override
            public void onStopped() {
                deregister(gwsAPI, getInternalAddress());
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


    // JCP APIs connection listeners

    @Override
    public void onConnected(JCPClient2 jcpClient) {
        if (getServer().isRunning()) {
            register(gwsAPI, getInternalAddress(), getPublicAddress(), apisPort, maxClients, GWType.Obj2Srv);
            update(gwsAPI, getInternalAddress(), gwStatus);

        } else {
            deregister(gwsAPI, getInternalAddress());
        }
    }

    @Override
    public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
    }

}
