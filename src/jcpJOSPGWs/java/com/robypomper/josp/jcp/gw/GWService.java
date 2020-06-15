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


    // Constructor

    public GWService(Server server, ClientInfo client, ServiceDBService serviceDBService) throws ServiceNotRegistered {
        this.server = server;
        this.client = client;
        this.serviceDBService = serviceDBService;
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
    }


    private void saveToDB() {
        serviceDBService.save(srvStatus);
    }

    public void setOffline() {
        srvStatus.setOnline(false);
        saveToDB();
    }

    public String getSrvId() {
        return srvId;
    }

    public String getUsrId() {
        return usrId;
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
        JOSPProtocol.ActionCmd cmd = JOSPProtocol.fromMsgToCmd(msg);
        if (cmd == null)
            return false;

        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("Processing command '%s...' from '%s' service", msg.substring(0, Math.min(10, msg.length())), fullSrvId));

        log.warn(Mrk_Commons.COMM_SRV_IMPL, String.format("Exec '%s' component's action not implemented", cmd.getComponentPath()));
        // ToDo: implement processAction() for GWService  Srv > Obj
        // Update Date lastActionExecuted
        // If allowed, Forward action to object
        // gw.gwObj.sendAction(String);

        log.debug(Mrk_Commons.COMM_SRV_IMPL, String.format("Command '%s...' processed for '%s' service", msg.substring(0, Math.min(10, msg.length())), fullSrvId));
        return true;
    }


    // Cloud requests

    public boolean processCloudRequestResponse(String msg) {
        return false;
    }


    // Exceptions

    public class ServiceNotRegistered extends Throwable {
        public ServiceNotRegistered(String fullSrvId, String srvId, String usrId) {
            super(String.format("Service '%s' not registered to JCP", fullSrvId));
        }
    }

}
