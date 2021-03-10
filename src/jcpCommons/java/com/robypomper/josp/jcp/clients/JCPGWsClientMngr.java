package com.robypomper.josp.jcp.clients;

import com.robypomper.java.JavaThreads;
import com.robypomper.josp.jcp.clients.jcp.jcp.GWsClient;
import com.robypomper.josp.states.StateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class JCPGWsClientMngr {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JCPGWsClientMngr.class);
    private final ClientParams gwsClientsParams;
    private final Map<String, JCPGWsClient> clients = new HashMap<>();
    private final Map<String, GWsClient> apiInstances = new HashMap<>();

    @Autowired
    public JCPGWsClientMngr(ClientParams gwsClientsParams) {
        this.gwsClientsParams = gwsClientsParams;
    }

    public JCPGWsClient getJCPGWsClient(String gwId, String gwAddr, int gwPort) {
        return getJCPGWsClient(gwId, gwAddr, gwPort, 1 * 1000);
    }

    public JCPGWsClient getJCPGWsClient(String gwId, String gwAddr, int gwPort, int sleepTime) {
        JCPGWsClient cl = clients.get(gwId);
        if (cl == null)
            cl = createJCPGWsClient(gwId, gwAddr, gwPort, sleepTime);

        return cl;
    }

    private JCPGWsClient createJCPGWsClient(String gwId, String gwAddr, int gwPort, int sleepTime) {
        String url = String.format("%s:%d", gwAddr, gwPort);
        JCPGWsClient cl = new JCPGWsClient(gwsClientsParams, url, true, gwId);
        clients.put(gwId, cl);

        if (!cl.isConnected())
            JavaThreads.softSleep(sleepTime);

        log.trace(String.format("Created JCPGWsClient instance for JCP GWs '%s'", gwId));
        return cl;
    }

    public JCPGWsClient removeJCPGWsClient(String gwId) {
        JCPGWsClient cl = clients.remove(gwId);
        if (cl == null)
            return null;

        apiInstances.remove(gwId);
        try {
            cl.disconnect();
            log.trace(String.format("Removed JCPGWsClient instance for JCP GWs '%s'", gwId));

        } catch (StateException e) {
            log.warn(String.format("Error on removing JCPGWsClient for HCP GWs '%s'", gwId), e);
        }

        return cl;
    }

    public GWsClient getGWsClient(String gwId, String gwAddr, int gwPort) {
        return getGWsClient(gwId, gwAddr, gwPort, 1000);
    }

    public GWsClient getGWsClient(String gwId, String gwAddr, int gwPort, int sleepTime) {
        JCPGWsClient cl = getJCPGWsClient(gwId, gwAddr, gwPort, sleepTime);

        GWsClient api = apiInstances.get(gwId);
        if (api == null)
            api = createGWsClient(cl, gwId);

        return api;
    }

    private GWsClient createGWsClient(JCPGWsClient gwClient, String gwId) {
        GWsClient api = new GWsClient(gwClient);
        apiInstances.put(gwId, api);
        return api;
    }

}
