package com.robypomper.josp.jcp.gw;

import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.Server;
import com.robypomper.josp.jcp.db.ObjectDBService;
import com.robypomper.josp.jcp.db.entities.Object;
import com.robypomper.josp.jcp.db.entities.ObjectStatus;
import com.robypomper.josp.protocol.JOSPPermissions;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;


public class GWObject {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final Server server;
    private final ClientInfo client;
    private final String objId;
    private final Object objDB;
    private final ObjectDBService objectDBService;
    private final JOSPGWsBroker gwBroker;


    // Constructor

    public GWObject(Server server, ClientInfo client, ObjectDBService objectDBService, JOSPGWsBroker gwBroker) throws ObjectNotRegistered {
        this.server = server;
        this.client = client;
        this.objId = client.getClientId();
        this.objectDBService = objectDBService;
        this.gwBroker = gwBroker;

        Optional<Object> objOpt = objectDBService.find(objId);
        if (!objOpt.isPresent())
            throw new ObjectNotRegistered("Object '%s' is NOT registered to JCP");
        objDB = objOpt.get();
        if (objDB.getStatus() == null) {
            objDB.setStatus(new ObjectStatus());
            objDB.getStatus().setObjId(objId);
            objectDBService.save(objDB);
        }

        objDB.getStatus().setOnline(true);
        updateStatusToDB();
        gwBroker.registerObject(this);
    }


    // Getters and setters

    public Object getObj() {
        return objDB;
    }

    public String getObjId() {
        return objId;
    }

    public void setOffline() {
        objDB.getStatus().setOnline(false);
        updateStatusToDB();
    }


    // Processors

    public boolean processFromObjectMsg(String msg) {
        if (JOSPProtocol_ObjectToService.isObjectInfoMsg(msg))
            return processObjectInfoMsg(msg);
        else if (JOSPProtocol_ObjectToService.isObjectStructMsg(msg))
            return processObjectStructMsg(msg);
        else if (JOSPProtocol_ObjectToService.isObjectPermsMsg(msg))
            return processObjectPermsMsg(msg);
        else if (JOSPProtocol_ObjectToService.isObjectStateUpdMsg(msg))
            return processUpdateMsg(msg);
        else
            return false;
    }

    private boolean processObjectInfoMsg(String msg) {
        // store info to db
        updateInfoToDB(msg);

        // send info to services
        return gwBroker.sendToServices(this, msg, JOSPPermissions.Type.Status);
    }

    private boolean processObjectStructMsg(String msg) {
        // store struct to db
        updateStructToDB(msg);

        // send struct to services
        return gwBroker.sendToServices(this, msg, JOSPPermissions.Type.Status);
    }

    private boolean processObjectPermsMsg(String msg) {
        // store perms to db
        updatePermsToDB(msg);

        // send perms to services
        boolean sendAll = gwBroker.sendToServices(this, msg, JOSPPermissions.Type.CoOwner);

        // send serPerm to service(s)
        for (GWService service : gwBroker.getAllowedServices(this, JOSPPermissions.Type.CoOwner)) {
            String srvPermMsg = JOSPProtocol_ObjectToService.createServicePermMsg(getObjId(), JOSPPermissions.Type.CoOwner, JOSPPermissions.Connection.LocalAndCloud);
            gwBroker.sendToSingleCloudService(this, service.getFullId(), srvPermMsg, JOSPPermissions.Type.CoOwner);
        }
        for (GWService service : gwBroker.getAllowedServices(this, JOSPPermissions.Type.Actions)) {
            String srvPermMsg = JOSPProtocol_ObjectToService.createServicePermMsg(getObjId(), JOSPPermissions.Type.Actions, JOSPPermissions.Connection.LocalAndCloud);
            gwBroker.sendToSingleCloudService(this, service.getFullId(), srvPermMsg, JOSPPermissions.Type.Actions);
        }
        for (GWService service : gwBroker.getAllowedServices(this, JOSPPermissions.Type.Status)) {
            String srvPermMsg = JOSPProtocol_ObjectToService.createServicePermMsg(getObjId(), JOSPPermissions.Type.Status, JOSPPermissions.Connection.LocalAndCloud);
            gwBroker.sendToSingleCloudService(this, service.getFullId(), srvPermMsg, JOSPPermissions.Type.Status);
        }

        return sendAll;
    }

    private boolean processUpdateMsg(String msg) {
        // update struct to db
        updateStructToDB(msg);

        // send update to services
        return gwBroker.sendToServices(this, msg, JOSPPermissions.Type.Status);
    }


    // Update to DB

    private void updateStatusToDB() {
        objectDBService.save(objDB.getStatus());
    }

    private void updateInfoToDB(String msg) {
        // ToDo implement updateInfoOnDB() method
    }

    private void updateStructToDB(String msg) {
        // ToDo implement updateStructOnDB() method
        if (JOSPProtocol_ObjectToService.isObjectStructMsg(msg)) {
            String struct;
            try {
                struct = JOSPProtocol_ObjectToService.getObjectStructMsg_Struct(msg);
            } catch (JOSPProtocol.ParsingException e) {
                return;
            }
            objDB.getStatus().setStructure(struct);
            objectDBService.save(objDB.getStatus());

        } else if (JOSPProtocol_ObjectToService.isObjectStateUpdMsg(msg)) {
            // ToDo implement updateStructOnDB()/when msg is state upd
        }
    }

    private void updatePermsToDB(String msg) {
        // ToDo implement updatePermsOnDB() method
    }


    // Communication

    public void sendData(String msg) throws Server.ServerStoppedException, Server.ClientNotConnectedException {
        server.sendData(client, msg);
    }


    // Exceptions

    public static class ObjectNotRegistered extends Throwable {
        public ObjectNotRegistered(String objId) {
            super(String.format("Object '%s' not registered to JCP", objId));
        }
    }

}
