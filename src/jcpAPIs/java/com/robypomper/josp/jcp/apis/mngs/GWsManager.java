package com.robypomper.josp.jcp.apis.mngs;

import com.robypomper.java.JavaThreads;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.clients.JCPGWsClientMngr;
import com.robypomper.josp.jcp.clients.jcp.jcp.GWsClient;
import com.robypomper.josp.jcp.db.apis.GWDBService;
import com.robypomper.josp.jcp.db.apis.entities.GW;
import com.robypomper.josp.jcp.db.apis.entities.GWStatus;
import com.robypomper.josp.jcp.params.jcp.JCPGWsStartup;
import com.robypomper.josp.jcp.params.jcp.JCPGWsStatus;
import com.robypomper.josp.states.StateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GWsManager {

    // Class constants

    private static final int AVAILABILITY_SOCKET_TIMEOUT_MS = 5 * 1000;
    private static final int UNAVAILABILITY_RETRY_TIMEOUT_MS = 5 * 1000;


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final GWDBService gwService;
    private final JCPGWsClientMngr apiGWsGWsClients;

    @Autowired
    public GWsManager(GWDBService gwService, JCPGWsClientMngr apiGWsGWsClients) {
        this.gwService = gwService;
        this.apiGWsGWsClients = apiGWsGWsClients;

        gwService.deleteAll();
    }


    // GW management

    public void remove(GW gw) {
        delete(gw);
        apiGWsGWsClients.removeJCPGWsClient(gw.getGwId());
    }

    public void add(String gwId, JCPGWsStartup gwStartup) {
        GW gw = new GW();
        gw.setGwId(gwId);

        gw.setType(gwStartup.type);
        gw.setGwAddr(gwStartup.gwAddr);
        gw.setGwPort(gwStartup.gwPort);
        gw.setGwAPIsAddr(gwStartup.gwAPIsAddr);
        gw.setGwAPIsPort(gwStartup.gwAPIsPort);
        gw.setClientsMax(gwStartup.clientsMax);
        gw.setVersion(gwStartup.version);

        GWStatus gwStatus = new GWStatus();
        gwStatus.setGwId(gwId);
        gw.setStatus(gwStatus);

        save(gw);
    }

    public void addExisting(GW gw, JCPGWsStartup gwStartup) {
        gw.getStatus().setOnline(true);
        gw.setType(gwStartup.type);
        gw.setGwAddr(gwStartup.gwAddr);
        gw.setGwPort(gwStartup.gwPort);
        gw.setGwAPIsAddr(gwStartup.gwAPIsAddr);
        gw.setGwAPIsPort(gwStartup.gwAPIsPort);
        gw.setClientsMax(gwStartup.clientsMax);
        gw.setVersion(gwStartup.version);

        save(gw);
    }

    public void update(GW gw, JCPGWsStatus gwStatus) {
        gw.getStatus().setOnline(true);
        gw.getStatus().setClients(gwStatus.clients);
        gw.getStatus().setLastClientConnectedAt(gwStatus.lastClientConnectedAt);
        gw.getStatus().setLastClientDisconnectedAt(gwStatus.lastClientDisconnectedAt);
        gw.setClientsMax(gwStatus.clientsMax);

        save(gw);
    }


    // DB methods

    private void save(GW gw) {
        gwService.save(gw);
    }

    private void delete(GW gw) {
        gwService.delete(gw);
    }


    // Getters

    public GW getById(String gwId) {
        Optional<GW> op = gwService.findById(gwId);
        return op.orElse(null);
    }

    public List<GW> getAll() {
        return gwService.getAll();
    }

    public GW getAvailableObj2Srv() {
        List<GW> gws = gwService.getAllObj2Srv();
        if (gws.isEmpty())
            return null;

        for (GW gw : gws)
            if (checkGWAvailability(gw))
                return gw;
            else
                checkGWAvailabilityAndAutoRemove(gw);

        return null;
    }

    public GW getAvailableSrv2Obj() {
        List<GW> gws = gwService.getAllSrv2Obj();
        if (gws.isEmpty())
            return null;

        for (GW gw : gws)
            if (checkGWAvailability(gw))
                return gw;
            else
                checkGWAvailabilityAndAutoRemove(gw);

        return null;
    }


    // Availability checks

    private void checkAllGWsAvailability() {
        for (GW gw : getAll())
            checkGWAvailabilityAndAutoRemove(gw);
    }

    private void checkGWAvailabilityAndAutoRemove(GW gw) {
        JavaThreads.initAndStart(new Runnable() {
            @Override
            public void run() {
                if (!checkGWAvailability(gw)) {
                    JavaThreads.softSleep(UNAVAILABILITY_RETRY_TIMEOUT_MS);
                    if (!checkGWAvailability(gw)) {
                        try {
                            getGWsClient(gw).getClient().disconnect();

                        } catch (StateException ignore) {
                        }
                        remove(gw);
                        log.warn(String.format("JCP APIs removed JCP GWs '%s' of type %s with '%s:%d' address because not reachable", gw.getGwId(), gw.getType(), gw.getGwAddr(), gw.getGwPort()));
                    }
                }
            }
        }, "CHECK_GW_AVAILABILITY", gw.getGwId());
    }

    private boolean checkGWAvailability(GW gw) {
        GWsClient cl = getGWsClient(gw);

        // Test JCP GWs status APIs
        try {
            cl.getJCPGWsReq();

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            return false;
        }

        // Test JCP GW JOSP server
        //if (!JavaNetworks.checkSocketReachability(gw.getGwAddr(), gw.getGwPort(), AVAILABILITY_SOCKET_TIMEOUT_MS))
        //    return false;

        return true;
    }


    // JCP GWs clients

    public GWsClient getGWsClient(GW gw) {
        return apiGWsGWsClients.getGWsClient(gw.getGwId(), gw.getGwAPIsAddr(), gw.getGwAPIsPort());
    }

    public GWsClient getAPIGWsGWsClient(GW gw) {
        return apiGWsGWsClients.getGWsClient(gw.getGwId(), gw.getGwAPIsAddr(), gw.getGwAPIsPort());
    }

}
