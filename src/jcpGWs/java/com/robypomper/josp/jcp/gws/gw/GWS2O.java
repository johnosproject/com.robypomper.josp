package com.robypomper.josp.jcp.gws.gw;

import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.java.JavaJKS;
import com.robypomper.java.JavaSSL;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.db.apis.EventDBService;
import com.robypomper.josp.jcp.db.apis.ServiceDBService;
import com.robypomper.josp.jcp.db.apis.StatusHistoryDBService;
import com.robypomper.josp.jcp.gws.broker.BrokerJSL;
import com.robypomper.josp.jcp.gws.exceptions.JSLServiceNotRegisteredException;
import com.robypomper.josp.types.josp.gw.GWType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GWS2O extends GWAbs {

    // Class constants

    private static final String ID = "S2O-%s";


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(GWS2O.class);
    private final Map<String, GWClientS2O> jslGWClients = new HashMap<>();
    private final BrokerJSL gwBroker;
    private final ServiceDBService serviceDBService;
    private final EventDBService eventsDBService;
    private final StatusHistoryDBService statusesHistoryDBService;


    // Constructors

    public GWS2O(final String region, final String addrInternal, final String addrPublic, final int gwPort, final int apiPort,
                 final int maxClients,
                 String jcpAPIsUrl, ClientParams jcpAPIsParams,
                 BrokerJSL gwBroker, ServiceDBService serviceDBService, EventDBService eventsDBService, StatusHistoryDBService statusesHistoryDBService) throws ServerStartupException, JavaJKS.GenerationException, JavaSSL.GenerationException {
        super(GWType.Srv2Obj, String.format(ID, region), addrInternal, addrPublic, gwPort, apiPort, maxClients, jcpAPIsUrl, jcpAPIsParams, log);
        this.gwBroker = gwBroker;
        this.serviceDBService = serviceDBService;
        this.eventsDBService = eventsDBService;
        this.statusesHistoryDBService = statusesHistoryDBService;
    }

    public void destroy() {
        super.destroy();
    }


    // GWServer's Client events

    @Override
    protected void onClientConnection(ServerClient client) {
        log.info(String.format("JSL Service '%s' connected to JCP GW '%s'", client.getRemoteId(), getId()));

        if (jslGWClients.get(client.getRemoteId()) != null) {
            disconnectBecauseError(client, "already connected");
            return;
        }

        GWClientS2O gwService;
        try {
            gwService = new GWClientS2O(client, gwBroker, serviceDBService, eventsDBService, statusesHistoryDBService);

        } catch (JSLServiceNotRegisteredException serviceNotRegistered) {
            disconnectBecauseError(client, "not registered");
            return;
        }
        jslGWClients.put(client.getRemoteId(), gwService);

        increaseClient();
    }

    @Override
    protected void onClientDisconnection(ServerClient client) {
        log.info(String.format("JSL Service '%s' disconnected from JCP GW '%s'", client.getRemoteId(), getId()));

        if (jslGWClients.get(client.getRemoteId()) == null)
            return;

        jslGWClients.remove(client.getRemoteId());

        decreaseClient();
    }


    // GWServer's Messages methods

    @Override
    protected boolean processData(ServerClient client, String data) {
        GWClientS2O gwService = jslGWClients.get(client.getRemoteId());
        return gwService.processFromServiceMsg(data);
    }

}
