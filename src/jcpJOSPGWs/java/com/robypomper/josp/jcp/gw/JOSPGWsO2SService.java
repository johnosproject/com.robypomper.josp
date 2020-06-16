package com.robypomper.josp.jcp.gw;

import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.events.LogServerClientEventsListener;
import com.robypomper.communication.server.events.LogServerMessagingEventsListener;
import com.robypomper.communication.server.events.ServerClientEvents;
import com.robypomper.communication.server.events.ServerLocalEvents;
import com.robypomper.communication.server.events.ServerMessagingEvents;
import com.robypomper.josp.jcp.db.ObjectDBService;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_CloudRequests;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class JOSPGWsO2SService extends AbsJOSPGWsService {

    // Class constants

    private static final int PORT_MIN = 9101;
    private static final int PORT_MAX = 9200;


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private static final Map<String, GWObject> objects = new HashMap<>();          // static because shared among all JOSPGWsO2S
    @Autowired
    private ObjectDBService objectDBService;
    @Autowired
    private JOSPGWsBroker gwBroker;


    // Clients connection

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

        try {
            gwObj = new GWObject(server, client, objectDBService, gwBroker);

        } catch (GWObject.ObjectNotRegistered e) {
            client.closeConnection();
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Object '%s' not registered to JOSP GW, disconnecting", client.getClientId()));
            return;
        }
        objects.put(client.getClientId(), gwObj);

        // Send ObjectStructure request
        log.trace(Mrk_Commons.COMM_SRV_IMPL, String.format("Send ObjectStructure request to object '%s'", client.getClientId()));
        try {
            server.sendData(client, JOSPProtocol_CloudRequests.createObjectStructureRequest(gwObj.getLastStructUpdate()));

        } catch (Server.ServerStoppedException | Server.ClientNotConnectedException e) {
            client.closeConnection();
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Error on sending ObjectStructure request to object '%s'", client.getClientId()));
        }
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

    private boolean onDataReceived(ClientInfo client, String readData) throws Throwable {
        GWObject obj = objects.get(client.getClientId());

        if (JOSPProtocol.isUpdMsg(readData) && obj.processUpdate(readData))
            return true;

        if (obj.processCloudRequestResponse(readData))
            return true;

        return false;
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
        return new LogServerClientEventsListener() {

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
        return new LogServerMessagingEventsListener() {

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
