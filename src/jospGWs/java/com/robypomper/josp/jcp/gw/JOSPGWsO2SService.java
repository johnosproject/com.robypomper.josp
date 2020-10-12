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

package com.robypomper.josp.jcp.gw;

import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.events.*;
import com.robypomper.josp.jcp.db.ObjectDBService;
import com.robypomper.josp.jcp.db.PermissionsDBService;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class JOSPGWsO2SService extends AbsJOSPGWsService {

    // Class constants

    private static final int PORT_MIN = 9101;
    private static final int PORT_MAX = 9110;


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private static final Map<String, GWObject> objects = new HashMap<>();          // static because shared among all JOSPGWsO2S
    @Autowired
    private ObjectDBService objectDBService;
    @Autowired
    private PermissionsDBService permissionsDBService;
    @Autowired
    private JOSPGWsBroker gwBroker;


    /**
     * Initialize JOSPGWsService with only one internal server.
     *
     * @param hostName
     */
    @Autowired
    public JOSPGWsO2SService(@Value("${jospgw.o2s.url}") final String hostName) {
        super(hostName);
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

        gwObj = new GWObject(server, client, objectDBService, permissionsDBService, gwBroker);
        objects.put(client.getClientId(), gwObj);
    }

    private void onClientDisconnection(ClientInfo client) {
        try {
            GWObject disconnectedObject = objects.remove(client.getClientId());
            disconnectedObject.setOffline();
            gwBroker.deregisterObject(disconnectedObject);
        } catch (NullPointerException e) {
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Error on client disconnection because object '%s' not known", client.getClientId()));
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


    // AbsJOSPGWsService implementations

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
             * Link to the {@link JOSPGWsO2SService#onClientConnection(com.robypomper.communication.server.Server, ClientInfo)} method.
             */
            @Override
            public void onClientConnection(ClientInfo client) {
                JOSPGWsO2SService.this.onClientConnection(getServer(), client);
            }

            /**
             * {@inheritDoc}
             * <p>
             * Link to the {@link JOSPGWsO2SService#onClientDisconnection(ClientInfo)} method.
             */
            @Override
            public void onClientDisconnection(ClientInfo client) {
                JOSPGWsO2SService.this.onClientDisconnection(client);
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
             * Link to the {@link JOSPGWsO2SService#onDataReceived(ClientInfo, String)} method.
             */
            @Override
            public boolean onDataReceived(ClientInfo client, String readData) throws Throwable {
                return JOSPGWsO2SService.this.onDataReceived(client, readData);
            }

        };
    }

}
