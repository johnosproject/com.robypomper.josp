package com.robypomper.josp.jcp.gw;

import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.Server;
import com.robypomper.josp.jcp.db.ObjectDBService;
import com.robypomper.josp.jcp.db.entities.Object;
import com.robypomper.josp.jcp.db.entities.ObjectStatus;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_CloudRequests;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Optional;


public class GWObject {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final Server server;
    private final ClientInfo client;
    private final ObjectDBService objectDBService;
    private final String objId;
    private final ObjectStatus objStatus;


    // Constructor

    public GWObject(Server server, ClientInfo client, ObjectDBService objectDBService) throws ObjectNotRegistered {
        this.server = server;
        this.client = client;
        this.objectDBService = objectDBService;
        this.objId = client.getClientId();

        Optional<Object> objOpt = objectDBService.find(objId);
        if (!objOpt.isPresent())
            throw new ObjectNotRegistered("Object '%s' is NOT registered to JCP");

        Object object = objOpt.get();
        if (object.getStatus() == null) {
            object.setStatus(new ObjectStatus());
            object.getStatus().setObjId(objId);
            objectDBService.save(object);
        }
        objStatus = object.getStatus();

        objStatus.setOnline(true);
        saveToDB();
    }


    // Sync with DB

    private void saveToDB() {
        objectDBService.save(objStatus);
    }


    // Getters and setters

    public Date getLastStructUpdate() {
        return objStatus.getLastStructUpdate();
    }

    public void setOffline() {
        objStatus.setOnline(false);
        saveToDB();
    }


    // Actions

    public boolean sendAction(String msg) {
        try {
            server.sendData(client, msg);
            return true;

        } catch (Server.ServerStoppedException | Server.ClientNotConnectedException e) {
            return false;
        }
    }


    // Updates

    public boolean processUpdate(String msg) {
        // parse received data (here or in prev methods)
        JOSPProtocol.StatusUpd upd = null;
        try {
            upd = JOSPProtocol.fromMsgToUpdStr(msg);
        } catch (JOSPProtocol.ParsingException e) {
            /* Not a status update message */
            return false;
        }

        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("Processing update '%s...' for '%s' object", msg.substring(0, Math.min(10, msg.length())), objId));

        log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Update '%s' component to '%s' value not implemented", upd.getComponentPath(), upd.getUpdate()));
        // ToDo: implement processUpdate() for GWObject  Obj > Srv
        // Update String objStructure
        // Update Date lastStateUpdate
        //for (GWServiceStatus gwSrvStatus : getStatusAllowedServices())
        //gw.toSrv(gwSrvStatus).sendUpdate(msg);

        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("Update '%s...' processed for '%s' object", msg.substring(0, Math.min(10, msg.length())), objId));
        return true;
    }


    // Cloud requests

    public boolean processCloudRequestResponse(String msg) {
        // Object structure request's response
        if (JOSPProtocol_CloudRequests.isObjectStructureRequestResponse(msg)) {
            log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("Process ObjectStructure response for '%s' object", objId));
            return processObjectStructureResponse(msg);
        }

        return false;
    }

    private boolean processObjectStructureResponse(String msg) {
        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("Processing ObjectStructure message for '%s' object", objId));

        try {
            Date lastUpdated = JOSPProtocol_CloudRequests.extractObjectStructureLastUpdateFromResponse(msg);
            if (getLastStructUpdate() != null
                    && getLastStructUpdate().compareTo(lastUpdated) > 0) {
                log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("ObjectStructure message '%s...' for object '%s' is older than local object structure", msg.substring(0, Math.min(10, msg.length())), objId));
                return true;
            }

            objStatus.setStructure(JOSPProtocol_CloudRequests.extractObjectStructureStructureFromResponse(msg));
            objStatus.setLastStructUpdate(lastUpdated);
            saveToDB();

        } catch (JOSPProtocol.ParsingException/* | ParsingException*/ e) {
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Error on processing ObjectStructure message '%s...' for '%s' object because %s", msg.substring(0, Math.min(10, msg.length())), objId, e.getMessage()), e);
            return false;
        }
        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("ObjectStructure message processed for '%s' object", objId));
        return true;
    }


    // Exceptions

    public static class ObjectNotRegistered extends Throwable {
        public ObjectNotRegistered(String objId) {
            super(String.format("Object '%s' not registered to JCP", objId));
        }
    }

}
