package com.robypomper.josp.jcp.gws.gw;

import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.java.JavaJKS;
import com.robypomper.java.JavaSSL;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.gws.broker.BrokerJOD;
import com.robypomper.josp.types.josp.gw.GWType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GWO2S extends GWAbs {

    // Class constants

    private static final String ID = "O2S-%s";


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(GWO2S.class);
    private final Map<String, GWClientO2S> jodGWClients = new HashMap<>();
    private final BrokerJOD gwBroker;
    private final ObjectDBService objectDBService;
    private final PermissionsDBService permissionsDBService;


    // Constructors

    public GWO2S(String region, String addrInternal, String addrPublic, int gwPort, int apiPort,
                 int maxClients,
                 String jcpAPIsUrl, ClientParams jcpAPIsParams,
                 BrokerJOD gwBroker, ObjectDBService objectDBService, PermissionsDBService permissionsDBService) throws ServerStartupException, JavaJKS.GenerationException, JavaSSL.GenerationException {
        super(GWType.Obj2Srv, String.format(ID, region), addrInternal, addrPublic, gwPort, apiPort, maxClients, jcpAPIsUrl, jcpAPIsParams, log);
        this.gwBroker = gwBroker;
        this.objectDBService = objectDBService;
        this.permissionsDBService = permissionsDBService;

    }

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

        GWClientO2S gwObject = new GWClientO2S(client, gwBroker, objectDBService, permissionsDBService);
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
        GWClientO2S gwObject = jodGWClients.get(client.getRemoteId());
        return gwObject.processFromObjectMsg(data);
    }

}
