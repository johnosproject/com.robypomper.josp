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

import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.ServerException;
import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.java.JavaDate;
import com.robypomper.java.JavaThreads;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.clients.JCPAPIsClient;
import com.robypomper.josp.jcp.clients.apis.gws.JCPAPIGWsClient;
import com.robypomper.josp.jcp.db.apis.ServiceDBService;
import com.robypomper.josp.jcp.gws.broker.GWsBroker;
import com.robypomper.josp.jcp.gws.s2o.GWService;
import com.robypomper.josp.jcp.params.jcp.JCPGWsStatus;
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
public class GWsS2OService extends AbsGWsService implements JCPClient2.ConnectionListener {


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private static final Map<String, GWService> services = new HashMap<>();          // static because shared among all JOSPGWsS2O
    @Autowired
    private ServiceDBService serviceDBService;
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
    public GWsS2OService(@Value("${jcp.gws.s2o.ip.internal}") final String hostNameInternal,
                         @Value("${jcp.gws.s2o.ip.public}") final String hostNamePublic,
                         @Value("${jcp.gws.s2o.port}") final int port,
                         @Value("${server.port}") final int apisPort,
                         @Value("${jcp.gws.s2o.maxClients}") final int maxClients,
                         @Value("${jcp.urlAPIs}") String urlAPIs,
                         ClientParams params) {
        super(hostNameInternal, hostNamePublic, port);
        JCPAPIsClient apisClient = new JCPAPIsClient(params, urlAPIs, false);
        this.gwsAPI = new JCPAPIGWsClient(apisClient);
        apisClient.addConnectionListener(this);
        this.apisPort = apisPort;
        this.maxClients = maxClients;
        this.gwStatus = new JCPGWsStatus(0, maxClients, null, null);

        try {
            getServer().startup();

        } catch (ServerStartupException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void destroy() {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("Halt server %s", getServer().getLocalId()));
        try {
            getServer().shutdown();

        } catch (ServerException e) {
            e.printStackTrace();
        }

        if (getServer().getState().isRunning())
            JavaThreads.softSleep(1000);

        if (getServer().getState().isRunning()) {
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Gateway %s not halted, force it.", this.getClass().getSimpleName()));
            deregister(gwsAPI, getInternalAddress());
        } else
            log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("Halted %s gateway", this.getClass().getSimpleName()));
    }


    // AbsGWsService implementations

    @Override
    protected void onServerStarted() {
        register(gwsAPI, getInternalAddress(), getPublicAddress(), apisPort, maxClients, GWType.Srv2Obj);
        update(gwsAPI, getInternalAddress(), gwStatus);
    }

    @Override
    protected void onServerStopped() {
        deregister(gwsAPI, getInternalAddress());
    }

    /**
     * Create {@link GWService} instance and send ObjectStructure of all
     * available (and allowed) objects to connected service.
     */
    @Override
    protected void onClientConnection(ServerClient client) {
        // Check if objects already know
        log.trace(Mrk_Commons.COMM_SRV_IMPL, String.format("Checks if connected service '%s' already know", client.getRemoteId()));
        GWService gwSrv = services.get(client.getRemoteId());
        if (gwSrv != null) {
            // multiple connection at same time
            try {
                client.disconnect();
            } catch (PeerDisconnectionException ignore) {
            }
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Service '%s' already connected to JOSP GW, disconnecting", client.getRemoteId()));
            return;
        }

        // Create GWService (that register to GW Broker) and add to active services
        try {
            gwSrv = new GWService(getServer(), client, serviceDBService, gwBroker);
        } catch (GWService.ServiceNotRegistered serviceNotRegistered) {
            try {
                client.disconnect();
            } catch (PeerDisconnectionException ignore) {
            }
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Service '%s' not registered to JOSP GW, disconnecting", client.getRemoteId()));
            return;
        }
        services.put(client.getRemoteId(), gwSrv);

        // Update GW status to JCP APIs
        gwStatus.clients++;
        assert gwStatus.clients == services.size();
        gwStatus.lastClientConnectedAt = JavaDate.getNowDate();
        update(gwsAPI, getInternalAddress(), gwStatus);
    }

    protected void onClientDisconnection(ServerClient client) {
        // Check if client is a registered service
        log.trace(Mrk_Commons.COMM_SRV_IMPL, String.format("Checks if disconnected client '%s' was a service's client", client.getRemoteId()));
        if (services.get(client.getRemoteId()) == null)
            return;

        // Deregister GWService from GW Broker and remove from active services
        try {
            GWService disconnectedService = services.remove(client.getRemoteId());
            disconnectedService.setOffline();
            gwBroker.deregisterService(disconnectedService);

        } catch (NullPointerException e) {
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Error on client disconnection because service '%s' not known", client.getRemoteId()));
        }

        // Update GW status to JCP APIs
        gwStatus.clients--;
        assert gwStatus.clients == services.size();
        gwStatus.lastClientDisconnectedAt = JavaDate.getNowDate();
        update(gwsAPI, getInternalAddress(), gwStatus);
    }

    protected boolean onDataReceived(ServerClient client, String data) {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("Data '%s...' received from '%s' service", data.substring(0, data.indexOf("\n")), client.getRemoteId()));
        GWService srv = services.get(client.getRemoteId());
        return srv.processFromServiceMsg(data);
    }


    // JCP APIs connection listeners

    @Override
    public void onConnected(JCPClient2 jcpClient) {
        if (getServer().getState().isRunning()) {
            register(gwsAPI, getInternalAddress(), getPublicAddress(), apisPort, maxClients, GWType.Srv2Obj);
            update(gwsAPI, getInternalAddress(), gwStatus);

        } else {
            deregister(gwsAPI, getInternalAddress());
        }
    }

    @Override
    public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
    }

    @Override
    public void onAuthenticationFailed(JCPClient2 jcpClient, Throwable t) {
    }

    @Override
    public void onDisconnected(JCPClient2 jcpClient) {
    }

}
