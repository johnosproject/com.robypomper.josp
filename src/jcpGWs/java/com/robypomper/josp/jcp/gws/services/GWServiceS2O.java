package com.robypomper.josp.jcp.gws.services;

import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.java.JavaJKS;
import com.robypomper.java.JavaSSL;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.db.apis.EventDBService;
import com.robypomper.josp.jcp.db.apis.ServiceDBService;
import com.robypomper.josp.jcp.db.apis.StatusHistoryDBService;
import com.robypomper.josp.jcp.gws.gw.GWO2S;
import com.robypomper.josp.jcp.gws.gw.GWS2O;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
public class GWServiceS2O implements ApplicationListener<ContextRefreshedEvent> {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(GWServiceS2O.class);
    private final GWS2O gw;


    // Constructors

    @Autowired
    protected GWServiceS2O(@Value("${jcp.gws.region:Central}") final String region,
                           @Value("${jcp.gws.s2o.ip.internal}") final String addrInternal,
                           @Value("${jcp.gws.s2o.ip.public}") final String addrPublic,
                           @Value("${jcp.gws.s2o.port}") final int gwPort,
                           @Value("${server.port}") final int apiPort,
                           @Value("${jcp.gws.s2o.maxClients}") final int maxClients,
                           @Value("${jcp.urlAPIs}") String jcpAPIsUrl,
                           ClientParams jcpAPIsParams,
                           BrokerService gwBroker, ServiceDBService serviceDBService, EventDBService eventsDBService, StatusHistoryDBService statusesHistoryDBService) throws ServerStartupException, JavaJKS.GenerationException, JavaSSL.GenerationException {
        gw = new GWS2O(region, addrInternal, addrPublic, gwPort, apiPort, maxClients, jcpAPIsUrl, jcpAPIsParams, gwBroker.getBrokerJSL(), serviceDBService, eventsDBService, statusesHistoryDBService);
    }

    @PreDestroy
    public void destroy() {
        gw.destroy();
        log.trace("JCP GW S2O service destroyed");
    }


    // Getters

    public GWS2O get() {
        return gw;
    }


    // Spring events listener

    public void onApplicationEvent(final ContextRefreshedEvent event) {
        gw.onApplicationEvent(event);
    }

}
