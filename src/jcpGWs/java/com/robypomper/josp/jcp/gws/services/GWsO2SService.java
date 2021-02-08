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
import com.robypomper.josp.jcp.db.apis.EventDBService;
import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.db.apis.StatusHistoryDBService;
import com.robypomper.josp.jcp.gws.broker.GWsBroker;
import com.robypomper.josp.jcp.gws.o2s.GWObject;
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
public class GWsO2SService extends AbsGWsService implements JCPClient2.ConnectionListener {


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
                         @Value("${jcp.urlAPIs}") String urlAPIs,
                         ClientParams params) {
        super(hostnameInternal, hostnamePublic, port);
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
        register(gwsAPI, getInternalAddress(), getPublicAddress(), apisPort, maxClients, GWType.Obj2Srv);
        update(gwsAPI, getInternalAddress(), gwStatus);
    }

    @Override
    protected void onServerStopped() {
        deregister(gwsAPI, getInternalAddress());
    }

    @Override
    protected void onClientConnection(ServerClient client) {
        // Check if objects already know
        log.trace(Mrk_Commons.COMM_SRV_IMPL, String.format("Checks if connected object '%s' already connected", client.getRemoteId()));
        GWObject gwObj = objects.get(client.getRemoteId());
        for (GWObject o : objects.values()) {
            System.out.println(o.getObjId() + " - " + (o.getObj() != null ? o.getObj().getStatus() : "null"));
        }
        if (gwObj != null) {
            try {
                client.disconnect();
            } catch (PeerDisconnectionException ignore) {
            }
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Object '%s' already connected to JOSP GW, disconnecting", client.getRemoteId()));
            return;
        }

        // Create GWObject (that register to GW Broker) and add to active services
        gwObj = new GWObject(getServer(), client, objectDBService, permissionsDBService, eventsDBService, statusesHistoryDBService, gwBroker);
        objects.put(client.getRemoteId(), gwObj);

        // Update GW status to JCP APIs
        gwStatus.clients++;
        assert gwStatus.clients == objects.size();
        gwStatus.lastClientConnectedAt = JavaDate.getNowDate();
        update(gwsAPI, getInternalAddress(), gwStatus);
    }

    @Override
    protected void onClientDisconnection(ServerClient client) {
        // Check if client is a registered service
        log.trace(Mrk_Commons.COMM_SRV_IMPL, String.format("Checks if disconnected client '%s' was a object's client", client.getRemoteId()));
        for (GWObject o : objects.values()) {
            System.out.println(o.getObjId() + " - " + (o.getObj() != null ? o.getObj().getStatus() : "null"));
        }
        if (objects.get(client.getRemoteId()) == null)
            return;

        // Deregister GWService from GW Broker and remove from active services
        try {
            GWObject disconnectedObject = objects.remove(client.getRemoteId());
            disconnectedObject.setOffline();
            gwBroker.deregisterObject(disconnectedObject);
        } catch (NullPointerException e) {
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Error on client disconnection because object '%s' not known", client.getRemoteId()));
        }

        // Update GW status to JCP APIs
        gwStatus.clients--;
        assert gwStatus.clients == objects.size();
        gwStatus.lastClientDisconnectedAt = JavaDate.getNowDate();
        update(gwsAPI, getInternalAddress(), gwStatus);
    }

    @Override
    protected boolean onDataReceived(ServerClient client, String data) {
        log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("Data '%s...' received from '%s' object", data.substring(0, data.indexOf("\n")), client.getRemoteId()));
        GWObject obj = objects.get(client.getRemoteId());
        int count = 0;
        while (obj == null && count < 5) {
            count++;
            try {
                Thread.sleep(100);

            } catch (InterruptedException e) {
                return false;
            }
            obj = objects.get(client.getRemoteId());
        }

        return obj != null && obj.processFromObjectMsg(data);
    }


    // JCP APIs connection listeners

    @Override
    public void onConnected(JCPClient2 jcpClient) {
        if (getServer().getState().isRunning()) {
            register(gwsAPI, getInternalAddress(), getPublicAddress(), apisPort, maxClients, GWType.Obj2Srv);
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
