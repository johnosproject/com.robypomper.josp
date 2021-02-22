package com.robypomper.josp.jcp.gws.services;

import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.java.JavaJKS;
import com.robypomper.java.JavaSSL;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.db.apis.EventDBService;
import com.robypomper.josp.jcp.db.apis.ServiceDBService;
import com.robypomper.josp.jcp.db.apis.StatusHistoryDBService;
import com.robypomper.josp.jcp.gws.broker.GWBroker;
import com.robypomper.josp.jcp.gws.clients.GWClientJSL;
import com.robypomper.josp.jcp.gws.exceptions.JSLServiceNotRegisteredException;
import com.robypomper.josp.types.josp.gw.GWType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

@Service
public class GWServiceS2O extends GWServiceAbs {

    // Class constants

    private static final String ID = "S2O-%s";


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(GWServiceS2O.class);
    private final Map<String, GWClientJSL> jslGWClients = new HashMap<>();
    @Autowired
    private GWBroker gwBroker;
    @Autowired
    private ServiceDBService serviceDBService;
    @Autowired
    private EventDBService eventsDBService;
    @Autowired
    private StatusHistoryDBService statusesHistoryDBService;


    // Constructors

    @Autowired
    protected GWServiceS2O(@Value("${jcp.gws.region:Central}") final String region,
                           @Value("${jcp.gws.s2o.ip.internal}") final String addrInternal,
                           @Value("${jcp.gws.s2o.ip.public}") final String addrPublic,
                           @Value("${jcp.gws.s2o.port}") final int gwPort,
                           @Value("${server.port}") final int apiPort,
                           @Value("${jcp.gws.s2o.maxClients}") final int maxClients,
                           @Value("${jcp.urlAPIs}") String jcpAPIsUrl,
                           ClientParams jcpAPIsParams) throws ServerStartupException, JavaJKS.GenerationException, JavaSSL.GenerationException {
        super(GWType.Srv2Obj, String.format(ID, region), addrInternal, addrPublic, gwPort, apiPort, maxClients, jcpAPIsUrl, jcpAPIsParams, log);
    }

    @PreDestroy
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

        GWClientJSL gwService;
        try {
            gwService = new GWClientJSL(client, gwBroker, serviceDBService, eventsDBService, statusesHistoryDBService);

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

        GWClientJSL disconnectedService = jslGWClients.remove(client.getRemoteId());

        decreaseClient();
    }


    // GWServer's Messages methods

    @Override
    protected boolean processData(ServerClient client, String data) {
        GWClientJSL gwService = jslGWClients.get(client.getRemoteId());
        return gwService.processFromServiceMsg(data);
    }

}
