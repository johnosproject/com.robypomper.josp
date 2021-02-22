package com.robypomper.josp.jcp.clients;

import com.robypomper.java.JavaThreads;
import com.robypomper.josp.clients.AbsAPIJCP;
import com.robypomper.josp.states.StateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JCPGWsClientMngr {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JCPGWsClientMngr.class);
    private final ClientParams gwsClientsParams;
    private final Map<String, JCPGWsClient> clients = new HashMap<>();
    private final Map<String, Map<Class<? extends AbsAPIJCP>, AbsAPIJCP>> apiInstances = new HashMap<>();

    @Autowired
    public JCPGWsClientMngr(ClientParams gwsClientsParams) {
        this.gwsClientsParams = gwsClientsParams;
    }

    public JCPGWsClient getGWsClient(String gwId, String gwAddr, int gwPort) {
        return getGWsClient(gwId, gwAddr, gwPort, 1 * 1000);
    }

    public JCPGWsClient getGWsClient(String gwId, String gwAddr, int gwPort, int sleepTime) {
        JCPGWsClient cl = clients.get(gwId);
        if (cl == null)
            cl = createGWsClient(gwId, gwAddr, gwPort, sleepTime);

        return cl;
    }

    private JCPGWsClient createGWsClient(String gwId, String gwAddr, int gwPort, int sleepTime) {
        String url = String.format("%s:%d", gwAddr, gwPort);
        JCPGWsClient cl = new JCPGWsClient(gwsClientsParams, url, true, gwId);
        clients.put(gwId, cl);
        apiInstances.put(gwId, new HashMap<>());

        if (!cl.isConnected())
            JavaThreads.softSleep(sleepTime);

        log.trace(String.format("Created JCPGWsClient instance for JCP GWs '%s'", gwId));
        return cl;
    }

    public JCPGWsClient removeGWsClient(String gwId) {
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

    public <T extends AbsAPIJCP> T getAPIGWsGWsClient(String gwId, String gwAddr, int gwPort, Class<T> apiClass) {
        return getAPIGWsGWsClient(gwId, gwAddr, gwPort, apiClass, 1000);
    }

    public <T extends AbsAPIJCP> T getAPIGWsGWsClient(String gwId, String gwAddr, int gwPort, Class<T> apiClass, int sleepTime) {
        JCPGWsClient cl = getGWsClient(gwId, gwAddr, gwPort, sleepTime);

        T api = apiClass.cast(apiInstances.get(gwId).get(apiClass));
        if (api == null)
            api = createAPIGWs(cl, gwId, apiClass);

        return api;
    }

    private <T extends AbsAPIJCP> T createAPIGWs(JCPGWsClient gwClient, String gwId, Class<T> apiClass) {
        T api;
        try {
            api = apiClass.getConstructor(JCPGWsClient.class).newInstance(gwClient);

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.warn(String.format("Error on creating '%s' AbsAPIJCP instance", apiClass.getSimpleName()), e);
            return null;
        }

        apiInstances.get(gwId).put(apiClass, api);
        log.trace(String.format("Created '%s' AbsAPIJCP instance", apiClass.getSimpleName()));
        return api;
    }

}
