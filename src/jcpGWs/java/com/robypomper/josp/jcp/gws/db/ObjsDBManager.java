package com.robypomper.josp.jcp.gws.db;

import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.clients.JCPAPIsClient;
import com.robypomper.josp.jcp.clients.apis.gws.JCPAPIGWsClient;
import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.db.apis.entities.Object;
import com.robypomper.josp.jcp.gws.broker.BrokerObjDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjsDBManager {

    // Internal vars

    private final JCPAPIGWsClient jcpAPIsGWs;
    private final Map<String, ObjDB> objsDB = new HashMap<>();
    private final BrokerObjDB broker;
    private final ObjectDBService objectDBService;
    private final PermissionsDBService permissionsDBService;


    // Constructors

    public ObjsDBManager(String region, int apiPort,
                         String jcpAPIsUrl, ClientParams jcpAPIsParams,
                         BrokerObjDB gwBroker, ObjectDBService objectDBService, PermissionsDBService permissionsDBService) {
        this.broker = gwBroker;
        this.objectDBService = objectDBService;
        this.permissionsDBService = permissionsDBService;

        JCPAPIsClient jcpAPIsClient = new JCPAPIsClient(jcpAPIsParams, jcpAPIsUrl, false);
        this.jcpAPIsGWs = new JCPAPIGWsClient(jcpAPIsClient);
        //this.jcpAPIsGWs.getClient().addConnectionListener(jcpAPIsListener_GWRegister);

        loadObjsFromDB();
    }

    public void destroy() {
        List<ObjDB> objsDBTmp = new ArrayList<>(objsDB.values());
        for (ObjDB objDB : objsDBTmp) {
            broker.deregisterObject(objDB);
            objsDB.remove(objDB.getId());
        }
    }


    // DB objects loader

    private void loadObjsFromDB() {
        for (Object obj : objectDBService.findAll()) {
            ObjDB objDB = new ObjDB(obj, permissionsDBService);
            objDB.resume();
            objsDB.put(obj.getObjId(), objDB);
            broker.registerObject(objDB);
        }
    }

}
