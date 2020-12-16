package com.robypomper.josp.jcp.clients;

import com.robypomper.josp.clients.AbsAPIJCP;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.states.StateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JCPGWsClientMngr {

    // Internal vars

    @Autowired
    private ClientParams gwsClientsParams;
    private final Map<Class<? extends AbsAPIJCP>, Map<String, AbsAPIJCP>> apiGWsGWsClients = new HashMap<>();


    public <T extends AbsAPIJCP> T getAPIGWsGWsClient(String gwId, String gwAddr, int gwPort, Class<T> apiClass) {
        return getAPIGWsGWsClient(gwId, gwAddr, gwPort, apiClass, 1000);
    }

    public <T extends AbsAPIJCP> T getAPIGWsGWsClient(String gwId, String gwAddr, int gwPort, Class<T> apiClass, int sleepTime) {
        if (gwsClientsParams == null)
            throw new RuntimeException("Field 'ClientParams gwsClientsParams' not yet set by @Autowired");

        if (apiGWsGWsClients.get(apiClass) == null)
            apiGWsGWsClients.put(apiClass, new HashMap<>());

        T apiClient;
        if (apiGWsGWsClients.get(apiClass).get(gwId) == null) {
            try {
                String url = String.format("%s:%d", gwAddr, gwPort);
                JCPGWsClient jcpClient = new JCPGWsClient(gwsClientsParams, url, true);
                jcpClient.connect();
                apiClient = apiClass.getConstructor(JCPGWsClient.class).newInstance(jcpClient);
                if (!jcpClient.isConnected())
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException ignore) {
                    }

                apiGWsGWsClients.get(apiClass).put(gwId, apiClient);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | StateException | JCPClient2.AuthenticationException ignore) {
                return null;
            }
        } else
            apiClient = apiClass.cast(apiGWsGWsClients.get(apiClass).get(gwId));

        return apiClient;
    }

}
