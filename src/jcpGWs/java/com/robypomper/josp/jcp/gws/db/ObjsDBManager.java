package com.robypomper.josp.jcp.gws.db;

import com.robypomper.java.JavaStructures;
import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.db.apis.entities.Object;
import com.robypomper.josp.jcp.gws.broker.BrokerObjDB;
import com.robypomper.josp.protocol.JOSPPerm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjsDBManager {

    // Internal vars

    private final Map<String, ObjDB> objsDB = new HashMap<>();
    private final BrokerObjDB broker;
    private final ObjectDBService objectDBService;
    private final PermissionsDBService permissionsDBService;


    // Constructors

    public ObjsDBManager(BrokerObjDB gwBroker, ObjectDBService objectDBService, PermissionsDBService permissionsDBService) {
        this.broker = gwBroker;
        this.objectDBService = objectDBService;
        this.permissionsDBService = permissionsDBService;

        loadObjsFromDB();
    }

    public void destroy() {
        List<ObjDB> objsDBTmp = new ArrayList<>(objsDB.values());
        for (ObjDB objDB : objsDBTmp) {
            broker.deregisterObject(objDB);
            objsDB.remove(objDB.getId());
        }
    }


    // Getters

    private BrokerObjDB getBroker() {
        return broker;
    }


    // DB objects loader

    private void loadObjsFromDB() {
        for (Object obj : objectDBService.findAll()) {
            ObjDB objDB = new ObjDB(obj, permissionsDBService);
            objDB.resume();
            objsDB.put(obj.getObjId(), objDB);
            broker.registerObject(objDB);
            getBroker().send(objDB, objDB.getMsgOBJ_INFO(), JOSPPerm.Type.Status);
            getBroker().send(objDB, objDB.getMsgOBJ_STRUCT(), JOSPPerm.Type.Status);
            getBroker().send(objDB, objDB.getMsgOBJ_PERM(), JOSPPerm.Type.CoOwner);
            Map<String, JavaStructures.Pair<JOSPPerm.Type, JOSPPerm.Connection>> x = getBroker().getObjectCloudAllowedServices(obj.getObjId());
            for (Map.Entry<String, JavaStructures.Pair<JOSPPerm.Type, JOSPPerm.Connection>> srvPerm : x.entrySet())
                getBroker().send(objDB, srvPerm.getKey(), objDB.getMsgSERVICE_PERM(srvPerm.getValue().getFirst(), srvPerm.getValue().getSecond()), JOSPPerm.Type.None);
        }
    }

}
