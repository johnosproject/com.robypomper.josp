package com.robypomper.josp.jcp.gw;

import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.Server;
import com.robypomper.josp.jcp.db.ServiceDBService;
import com.robypomper.josp.jcp.db.entities.Service;
import com.robypomper.josp.jcp.db.entities.ServiceStatus;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.log.Mrk_Commons;
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
    private final ServiceStatus srvStatus;
    private final JOSPGWsBroker gwBroker;


    // Constructor

    public GWService(Server server, ClientInfo client, ServiceDBService serviceDBService, JOSPGWsBroker gwBroker) throws ServiceNotRegistered {
        this.server = server;
        this.client = client;
        this.serviceDBService = serviceDBService;
        this.gwBroker = gwBroker;
        this.fullSrvId = client.getClientId();
        System.out.println(fullSrvId);
        String[] ids = client.getClientId().split("/");
        this.srvId = ids[0];
        this.usrId = ids[1];
        this.instId = ids[2];

        Optional<Service> srvOpt = serviceDBService.find(srvId);
        if (!srvOpt.isPresent())
            throw new ServiceNotRegistered(fullSrvId, srvId, usrId);

        Optional<ServiceStatus> srvStatusOpt = serviceDBService.findStatus(fullSrvId);
        if (srvStatusOpt.isPresent())
            srvStatus = srvStatusOpt.get();
        else {
            srvStatus = new ServiceStatus();
            srvStatus.setFullId(fullSrvId);
            srvStatus.setSrvId(srvId);
            srvStatus.setUsrId(usrId);
            srvStatus.setInstId(instId);
            //srvStatus.setVersion(instId);
        }

        srvStatus.setOnline(true);
        saveToDB();
        gwBroker.registerService(this);
    }


    // Sync with DB

    private void saveToDB() {
        serviceDBService.save(srvStatus);
    }


    // Getters and setters

    public void setOffline() {
        srvStatus.setOnline(false);
        saveToDB();
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


    // Actions

    public boolean processAction(String msg) {
        // parse received data (here or in prev methods)
        JOSPProtocol.ActionCmd cmd;
        try {
            cmd = JOSPProtocol.fromMsgToCmdStr(msg);
        } catch (JOSPProtocol.ParsingException e) {
            /* Not a action command message */
            return false;
        }

        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("Processing command '%s...' from '%s' service", msg.substring(0, msg.indexOf("\n")), fullSrvId));

        gwBroker.actionToObject(cmd);
        //srvStatus.setLastActionExecuted(new Date());
        saveToDB();

        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("Command '%s...' processed for '%s' service", msg.substring(0, msg.indexOf("\n")), fullSrvId));
        return true;
    }


    // Cloud requests

    public boolean processCloudRequestResponse(String msg) {
        return false;
    }


    // Exceptions

    public static class ServiceNotRegistered extends Throwable {
        public ServiceNotRegistered(String fullSrvId, String srvId, String usrId) {
            super(String.format("Service '%s' not registered to JCP", fullSrvId));
        }
    }

}
