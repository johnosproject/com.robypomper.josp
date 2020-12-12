package com.robypomper.josp.jcp.apis.mngs;

import com.robypomper.java.JavaNetworks;
import com.robypomper.java.JavaThreads;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.clients.JCPGWsClientMngr;
import com.robypomper.josp.jcp.clients.gws.apis.APIGWsGWsClient;
import com.robypomper.josp.jcp.clients.jcp.jcp.GWsClient;
import com.robypomper.josp.jcp.db.apis.GWDBService;
import com.robypomper.josp.jcp.db.apis.entities.GW;
import com.robypomper.josp.jcp.db.apis.entities.GWStatus;
import com.robypomper.josp.jcp.params.jcp.JCPGWsStartup;
import com.robypomper.josp.jcp.params.jcp.JCPGWsStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class GWsManager {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    @Autowired
    private GWDBService gwService;
    @Autowired
    private ClientParams gwsClientsParams;
    @Autowired
    private JCPGWsClientMngr apiGWsGWsClients;


    // Getters

    public void remove(GW gw) {
        delete(gw);
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

    public void update(GW gw, JCPGWsStartup gwStartup) {
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

    public List<GW> getAllObj2Srv() {
        return gwService.getAllSrv2Obj();
    }

    public List<GW> getAllSrv2Obj() {
        return gwService.getAllSrv2Obj();
    }

    public List<GW> getObj2Srv(boolean online) {
        return gwService.getAllSrv2Obj();
    }

    public List<GW> getSrv2Obj(boolean online) {
        return gwService.getAllSrv2Obj();
    }

    public GW getAvailableObj2Srv() {
        if (JavaThreads.isInStackOverflow()) {
            log.warn("GWsManager::getAvailableObj2Srv throw StackOverflow, return null");
            return null;
        }

        List<GW> gws = getObj2Srv(true);
        if (gws.isEmpty())
            return null;

        GW gw = gws.get(0);
        if (checkGWOnline(gw, true))
            return gw;

        checkAllGWsOnline();
        return getAvailableObj2Srv();
    }

    public GW getAvailableSrv2Obj() {
        if (JavaThreads.isInStackOverflow()) {
            log.warn("GWsManager::getAvailableSrv2Obj throw StackOverflow, return null");
            return null;
        }

        List<GW> gws = getSrv2Obj(true);
        if (gws.isEmpty())
            return null;

        GW gw = gws.get(0);
        if (checkGWOnline(gw, true))
            return gw;

        checkAllGWsOnline();
        return getAvailableSrv2Obj();
    }


    // Online checks

    public boolean checkGWOnline(GW gw, boolean autoUpdate) {
        GWsClient cl = getGWsClient(gw);
        // Test JCP Status GW API
        try {
            cl.getJCPGWsReq();

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            if (autoUpdate)
                setGWOffline(gw);
            return false;
        }

        // Test GW JOSP server
        if (!JavaNetworks.pingHost(gw.getGwAddr(), gw.getGwPort(), 4000)) {
            if (autoUpdate)
                setGWOffline(gw);
            return false;
        }

        return true;
    }

    private void setGWOffline(GW gw) {
        gw.getStatus().setOnline(false);
        save(gw);
    }

    /**
     * Start a thread for each GW registered and check if it's online or not.
     * <p>
     * When a gw is offline, it's state will updated to the db via the
     * {@link #setGWOffline(GW)} method.
     * <p>
     * If a check's thread can't join to the main thread, then an interruption
     * is send to the blocking thread. If it's not enought to terminate the
     * thread, and then determinate if the gw is online/offline, then the
     * thread is terminated
     */
    public void checkAllGWsOnline() {
        Map<GW, Thread> ths = new HashMap<>();
        for (GW gw : getAll()) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    checkGWOnline(gw, true);
                }
            });
            t.start();
            ths.put(gw, t);
        }

        for (Map.Entry<GW, Thread> gt : ths.entrySet()) {
            try {
                gt.getValue().join(5000);
            } catch (InterruptedException ignore) {
                gt.getValue().interrupt();
                try {
                    gt.getValue().join(1000);
                } catch (InterruptedException e) {
                    log.error(String.format("Can't check JCP GW %s status because %s", gt.getKey().getGwId(), e.getMessage()), e);
                    setGWOffline(gt.getKey());
                }
            }
        }
    }


    // JCP GWs clients

    public GWsClient getGWsClient(GW gw) {
        return apiGWsGWsClients.getAPIGWsGWsClient(gw.getGwId(), gw.getGwAPIsAddr(), gw.getGwAPIsPort(), GWsClient.class);
    }

    public APIGWsGWsClient getAPIGWsGWsClient(GW gw) {
        return apiGWsGWsClients.getAPIGWsGWsClient(gw.getGwId(), gw.getGwAPIsAddr(), gw.getGwAPIsPort(), APIGWsGWsClient.class);
    }
}
