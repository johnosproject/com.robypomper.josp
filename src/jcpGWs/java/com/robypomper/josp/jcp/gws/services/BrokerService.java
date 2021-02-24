package com.robypomper.josp.jcp.gws.services;

import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.gws.broker.Broker;
import com.robypomper.josp.jcp.gws.broker.BrokerJOD;
import com.robypomper.josp.jcp.gws.broker.BrokerJSL;
import com.robypomper.josp.jcp.gws.broker.BrokerObjDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
public class BrokerService {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(BrokerService.class);
    private final Broker broker;


    // Constructors

    @Autowired
    protected BrokerService(PermissionsDBService permissionsDBService) {
        broker = new Broker(permissionsDBService);
    }


    @PreDestroy
    public void destroy() {
        //broker.dismiss();
        //log.trace("JCP GW O2S service destroyed");
    }


    // Getters

    public BrokerJOD getBrokerJOD() {
        return broker;
    }

    public BrokerJSL getBrokerJSL() {
        return broker;
    }

    public BrokerObjDB getBrokerObjDB() {
        return broker;
    }

}
