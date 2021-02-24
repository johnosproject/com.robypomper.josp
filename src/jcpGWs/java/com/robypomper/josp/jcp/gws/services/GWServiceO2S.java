package com.robypomper.josp.jcp.gws.services;

import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.java.JavaJKS;
import com.robypomper.java.JavaSSL;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.db.apis.*;
import com.robypomper.josp.jcp.gws.gw.GWO2S;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
public class GWServiceO2S implements ApplicationListener<ContextRefreshedEvent> {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(GWServiceO2S.class);
    private final GWO2S gw;


    // Constructors

    @Autowired
    protected GWServiceO2S(@Value("${jcp.gws.region:Central}") final String region,
                           @Value("${jcp.gws.o2s.ip.internal}") final String addrInternal,
                           @Value("${jcp.gws.o2s.ip.public}") final String addrPublic,
                           @Value("${jcp.gws.o2s.port}") final int gwPort,
                           @Value("${server.port}") final int apiPort,
                           @Value("${jcp.gws.o2s.maxClients}") final int maxClients,
                           @Value("${jcp.urlAPIs}") String jcpAPIsUrl,
                           ClientParams jcpAPIsParams,
                           BrokerService gwBroker, ObjectDBService objectDBService, PermissionsDBService permissionsDBService) throws ServerStartupException, JavaJKS.GenerationException, JavaSSL.GenerationException {
        gw = new GWO2S(region, addrInternal, addrPublic, gwPort, apiPort, maxClients, jcpAPIsUrl, jcpAPIsParams, gwBroker.getBrokerJOD(), objectDBService, permissionsDBService);
    }

    @PreDestroy
    public void destroy() {
        gw.destroy();
        log.trace("JCP GW O2S service destroyed");
    }


    // Getters

    public GWO2S get() {
        return gw;
    }


    // Spring events listener

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        gw.onApplicationEvent(event);
    }

}
