package com.robypomper.josp.jcp.gw;

import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.Server;
import com.robypomper.josp.jcp.db.GWObjectStatusDBService;
import com.robypomper.josp.jcp.db.entities.GWObjectStatus;
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
    private final GWObjectStatusDBService gwObjectDB;
    private final GWObjectStatus gwObjectStatus;


    // Constructor

    public GWObject(Server server, ClientInfo client, GWObjectStatusDBService gwObjectDB) {
        this.server = server;
        this.client = client;
        this.gwObjectDB = gwObjectDB;
        String objId = client.getClientId();

        Optional<GWObjectStatus> objOpt = gwObjectDB.find(objId);
        if (objOpt.isPresent())
            gwObjectStatus = objOpt.get();
        else {
            gwObjectStatus = new GWObjectStatus();
            gwObjectStatus.setObjId(objId);
        }

        gwObjectStatus.setOnline(true);
        syncToDB();
    }


    private void syncToDB() {
        gwObjectDB.addOrUpdate(gwObjectStatus);
    }

    public Date getLastStructUpdate() {
        return gwObjectStatus.getLastStructUpdate();
    }

    public void setOffline() {
        gwObjectStatus.setOnline(false);
        syncToDB();
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
        JOSPProtocol.StatusUpd upd = JOSPProtocol.fromMsgToUpd(msg);
        if (upd == null)
            return false;

        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("Processing update '%s...' for '%s' object", msg.substring(0, Math.min(10, msg.length())), gwObjectStatus.getObjId()));

        log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Update '%s' component to '%s' value not implemented", upd.getComponentPath(), upd.getUpdate()));
        // ToDo: implement processUpdate() for GWObject
        // Update String objStructure
        // Update Date lastStateUpdate
        // Forward update to allowed services

        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("Update '%s...' processed for '%s' object", msg.substring(0, Math.min(10, msg.length())), gwObjectStatus.getObjId()));
        return true;
    }


    // Cloud requests

    public boolean processCloudRequestResponse(String msg) {
        // Object structure request's response
        if (JOSPProtocol_CloudRequests.isObjectStructureRequestResponse(msg)) {
            log.info(Mrk_Commons.COMM_SRV_IMPL, String.format("Process ObjectStructure response for '%s' object", gwObjectStatus.getObjId()));
            return processObjectStructureResponse(msg);
        }

        return false;
    }

    private boolean processObjectStructureResponse(String msg) {
        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("Processing ObjectStructure message for '%s' object", gwObjectStatus.getObjId()));

        try {
            Date lastUpdated = JOSPProtocol_CloudRequests.extractObjectStructureLastUpdateFromResponse(msg);
            if (gwObjectStatus.getLastStructUpdate() != null
                    && gwObjectStatus.getLastStructUpdate().compareTo(lastUpdated) > 0) {
                log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("ObjectStructure message '%s...' for object '%s' is older than local object structure", msg.substring(0, Math.min(10, msg.length())), gwObjectStatus.getObjId()));
                return true;
            }

            gwObjectStatus.setStructure(JOSPProtocol_CloudRequests.extractObjectStructureFromResponse(msg));
            gwObjectStatus.setLastStructUpdate(lastUpdated);
            syncToDB();

        } catch (JOSPProtocol.ParsingException/* | ParsingException*/ e) {
            log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Error on processing ObjectStructure message '%s...' for '%s' object because %s", msg.substring(0, Math.min(10, msg.length())), gwObjectStatus.getObjId(), e.getMessage()), e);
            return false;
        }
        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("ObjectStructure message processed for '%s' object", gwObjectStatus.getObjId()));
        return true;
    }

}
