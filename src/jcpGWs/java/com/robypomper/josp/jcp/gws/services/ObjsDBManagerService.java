package com.robypomper.josp.jcp.gws.services;

import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.gws.db.ObjsDBManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
public class ObjsDBManagerService {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(ObjsDBManagerService.class);
    private final ObjsDBManager objsMngr;


    // Constructors

    @Autowired
    protected ObjsDBManagerService(BrokerService gwBroker, ObjectDBService objectDBService, PermissionsDBService permissionsDBService) {
        objsMngr = new ObjsDBManager(gwBroker.getBrokerObjDB(), objectDBService, permissionsDBService);
    }

    @PreDestroy
    public void destroy() {
        objsMngr.destroy();
        log.trace("Objects DB Manager service destroyed");
    }

}
