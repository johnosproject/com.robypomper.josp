package com.robypomper.josp.jcp.clients;

import com.robypomper.josp.clients.AbsAPIJCP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JCPGWsClientMngr<T extends AbsAPIJCP> {

    // Internal vars

    @Autowired
    private ClientParams gwsClientsParams;
    private final Map<String, T> apiGWsGWsClients = new HashMap<>();


    public T getAPIGWsGWsClient(String gwId, String gwAddr, int gwPort, Class<T> apiClass) {
        return getAPIGWsGWsClient(gwId, gwAddr, gwPort, apiClass, 1000);
    }

    public T getAPIGWsGWsClient(String gwId, String gwAddr, int gwPort, Class<T> apiClass, int sleepTime) {
        if (gwsClientsParams == null)
            throw new RuntimeException("Field 'ClientParams gwsClientsParams' not yet set by @Autowired");

        if (apiGWsGWsClients.get(gwId) == null) {
            try {
                String url = String.format("%s:%d", gwAddr, gwPort);
                JCPGWsClient jcpClient = new JCPGWsClient(gwsClientsParams, url);
                T apiClient = apiClass.getConstructor(JCPGWsClient.class).newInstance(jcpClient);
                if (!jcpClient.isConnected())
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException ignore) {
                    }

                apiGWsGWsClients.put(gwId, apiClient);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignore) {
            }

        }
        return apiGWsGWsClients.get(gwId);
    }

}
