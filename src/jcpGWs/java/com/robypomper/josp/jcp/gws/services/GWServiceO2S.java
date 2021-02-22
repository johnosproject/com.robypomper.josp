package com.robypomper.josp.jcp.gws.services;

import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.java.JavaJKS;
import com.robypomper.java.JavaSSL;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.gws.broker.GWBroker;
import com.robypomper.josp.jcp.gws.clients.GWClientJOD;
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
public class GWServiceO2S extends GWServiceAbs {

    // Class constants

    private static final String ID = "O2S-%s";


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(GWServiceO2S.class);
    private final Map<String, GWClientJOD> jodGWClients = new HashMap<>();
    @Autowired
    private GWBroker gwBroker;
    @Autowired
    private ObjectDBService objectDBService;
    @Autowired
    private PermissionsDBService permissionsDBService;


    // Constructors

    @Autowired
    protected GWServiceO2S(@Value("${jcp.gws.region:Central}") final String region,
                           @Value("${jcp.gws.o2s.ip.internal}") final String addrInternal,
                           @Value("${jcp.gws.o2s.ip.public}") final String addrPublic,
                           @Value("${jcp.gws.o2s.port}") final int gwPort,
                           @Value("${server.port}") final int apiPort,
                           @Value("${jcp.gws.o2s.maxClients}") final int maxClients,
                           @Value("${jcp.urlAPIs}") String jcpAPIsUrl,
                           ClientParams jcpAPIsParams) throws ServerStartupException, JavaJKS.GenerationException, JavaSSL.GenerationException {
        super(GWType.Obj2Srv, String.format(ID, region), addrInternal, addrPublic, gwPort, apiPort, maxClients, jcpAPIsUrl, jcpAPIsParams, log);
    }

    @PreDestroy
    public void destroy() {
        super.destroy();
    }


    // GWServer's Client events

    @Override
    protected void onClientConnection(ServerClient client) {
        log.info(String.format("JOD Object '%s' connected to JCP GW '%s'", client.getRemoteId(), getId()));

        if (jodGWClients.get(client.getRemoteId()) != null) {
            disconnectBecauseError(client, "already connected");
            return;
        }

        GWClientJOD gwObject = new GWClientJOD(client, gwBroker, objectDBService, permissionsDBService);
        jodGWClients.put(client.getRemoteId(), gwObject);

        increaseClient();
    }

    @Override
    protected void onClientDisconnection(ServerClient client) {
        log.info(String.format("JOD Object '%s' disconnected from JCP GW '%s'", client.getRemoteId(), getId()));

        if (jodGWClients.get(client.getRemoteId()) == null)
            return;

        jodGWClients.remove(client.getRemoteId());

        decreaseClient();
    }


    // GWServer's Messages methods

    @Override
    protected boolean processData(ServerClient client, String data) {
        GWClientJOD gwObject = jodGWClients.get(client.getRemoteId());
        return gwObject.processFromObjectMsg(data);
    }

}
