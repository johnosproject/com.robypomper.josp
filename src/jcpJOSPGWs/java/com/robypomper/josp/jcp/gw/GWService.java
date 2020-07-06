package com.robypomper.josp.jcp.gw;

import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.Server;
import com.robypomper.josp.jcp.db.ServiceDBService;
import com.robypomper.josp.jcp.db.entities.Service;
import com.robypomper.josp.jcp.db.entities.ServiceStatus;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_Service;
import com.robypomper.josp.protocol.JOSPProtocol_ServiceToObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;


public class GWService {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final Server server;
    private final ClientInfo client;
    private final ServiceDBService serviceDBService;
    private final String fullSrvId;
    private final String srvId;
    private final String usrId;
    private final String instId;
    private final Service srvDB;
    private final ServiceStatus srvStatusDB;
    //private final ServiceStatus srvStatus;
    private final JOSPGWsBroker gwBroker;


    // Constructor

    public GWService(Server server, ClientInfo client, ServiceDBService serviceDBService, JOSPGWsBroker gwBroker) throws ServiceNotRegistered {
        this.server = server;
        this.client = client;
        this.serviceDBService = serviceDBService;
        this.gwBroker = gwBroker;
        this.fullSrvId = client.getClientId();
        this.srvId = JOSPProtocol_Service.fullSrvIdToSrvId(this.fullSrvId);
        this.usrId = JOSPProtocol_Service.fullSrvIdToUsrId(this.fullSrvId);
        this.instId = JOSPProtocol_Service.fullSrvIdToInstId(this.fullSrvId);

        Optional<Service> srvOpt = serviceDBService.find(srvId);
        if (!srvOpt.isPresent())
            throw new ServiceNotRegistered(fullSrvId, srvId, usrId);
        srvDB = srvOpt.get();

        Optional<ServiceStatus> srvStatusOpt = serviceDBService.findStatus(fullSrvId);
        if (srvStatusOpt.isPresent())
            srvStatusDB = srvStatusOpt.get();
        else {
            srvStatusDB = new ServiceStatus();
            srvStatusDB.setFullId(fullSrvId);
            srvStatusDB.setSrvId(srvId);
            srvStatusDB.setUsrId(usrId);
            srvStatusDB.setInstId(instId);
            //srvStatus.setVersion(instId);
        }

        srvStatusDB.setOnline(true);
        srvStatusDB.setLastConnectionAt(JOSPProtocol.getNowDate());
        updateStatusToDB();
        gwBroker.registerService(this);
    }


    // Sync with DB

    public void updateStatusToDB() {
        serviceDBService.save(srvStatusDB);
    }


    // Getters and setters

    public void setOffline() {
        srvStatusDB.setOnline(false);
        srvStatusDB.setLastDisconnectionAt(JOSPProtocol.getNowDate());
        updateStatusToDB();
    }

    public String getFullId() {
        return fullSrvId;
    }

    public String getSrvId() {
        return srvId;
    }

    public String getUsrId() {
        return usrId;
    }

    public String getInstId() {
        return instId;
    }

    public Service getSrv() {
        return srvDB;
    }

    public ServiceStatus getSrvStatus() {
        return srvStatusDB;
    }

    public boolean processFromServiceMsg(String msg) {
        String objId;
        try {
            objId = JOSPProtocol_ServiceToObject.getObjId(msg);
        } catch (JOSPProtocol.ParsingException e) {
            return false;
        }

        JOSPPerm.Type minReqPerm = JOSPPerm.Type.None;
        if (JOSPProtocol_ServiceToObject.isObjectSetNameMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectSetOwnerIdMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectAddPermMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectUpdPermMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectRemPermMsg(msg))
            minReqPerm = JOSPPerm.Type.CoOwner;

        if (JOSPProtocol_ServiceToObject.isObjectActionCmdMsg(msg))
            minReqPerm = JOSPPerm.Type.Actions;

        return gwBroker.sendToObject(this, objId, msg, minReqPerm);
    }

    // Communication

    public void sendData(String msg) throws Server.ServerStoppedException, Server.ClientNotConnectedException {
        server.sendData(client, msg);
    }

    // Updates

    public boolean sendUpdate(String msg) {
        try {
            server.sendData(client, msg);
            return true;

        } catch (Server.ServerStoppedException | Server.ClientNotConnectedException e) {
            return false;
        }
    }


    // Exceptions

    public static class ServiceNotRegistered extends Throwable {
        public ServiceNotRegistered(String fullSrvId, String srvId, String usrId) {
            super(String.format("Service '%s' not registered to JCP", fullSrvId));
        }
    }

}
