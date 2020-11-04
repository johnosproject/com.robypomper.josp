/* *****************************************************************************
 * The John Cloud Platform set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.josp.jcp.gw;

import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.Server;
import com.robypomper.josp.jcp.db.ServiceDBService;
import com.robypomper.josp.jcp.db.entities.Service;
import com.robypomper.josp.jcp.db.entities.ServiceStatus;
import com.robypomper.josp.protocol.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
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
    private ServiceStatus srvStatusDB;
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
        srvStatusDB = serviceDBService.save(srvStatusDB);
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

        // JOD redirect JSL requests

        JOSPPerm.Type minReqPerm = JOSPPerm.Type.None;
        if (JOSPProtocol_ServiceToObject.isObjectSetNameMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectSetOwnerIdMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectAddPermMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectUpdPermMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectRemPermMsg(msg)) {
            minReqPerm = JOSPPerm.Type.CoOwner;
            return gwBroker.sendToObject(this, objId, msg, minReqPerm);
        }

        if (JOSPProtocol_ServiceToObject.isObjectActionCmdMsg(msg)) {
            minReqPerm = JOSPPerm.Type.Actions;
            return gwBroker.sendToObject(this, objId, msg, minReqPerm);
        }

        // JCP processed JSL requests

        if (JOSPProtocol_ServiceToObject.isHistoryCompStatusMsg(msg)) {
            try {
                minReqPerm = JOSPProtocol_ServiceToObject.HISTORY_STATUS_REQ_MIN_PERM;
                GWObject object = gwBroker.findObject(objId);
                String reqId = JOSPProtocol_ServiceToObject.getHistoryCompStatusMsg_ReqId(msg);
                String compPath = JOSPProtocol_ServiceToObject.getHistoryCompStatusMsg_CompPath(msg);
                HistoryLimits limits = JOSPProtocol_ServiceToObject.getHistoryCompStatusMsg_Limits(msg);
                List<JOSPStatusHistory> statusesHistory = object.getStatusesHistory(compPath, limits);
                String resMsg = JOSPProtocol_ObjectToService.createHistoryCompStatusMsg(objId, compPath, reqId, statusesHistory);
                return gwBroker.sendToSingleCloudService(object, fullSrvId, resMsg, minReqPerm);
            } catch (JOSPProtocol.ParsingException e) {
                return false;
            }
        }
        if (JOSPProtocol_ServiceToObject.isHistoryEventsMsg(msg)) {
            try {
                minReqPerm = JOSPProtocol_ServiceToObject.HISTORY_EVENTS_REQ_MIN_PERM;
                GWObject object = gwBroker.findObject(objId);
                String reqId = JOSPProtocol_ServiceToObject.getHistoryEventsMsg_ReqId(msg);
                HistoryLimits limits = JOSPProtocol_ServiceToObject.getHistoryEventsMsg_Limits(msg);
                List<JOSPEvent> eventsHistory = object.getEvents(limits);
                String resMsg = JOSPProtocol_ObjectToService.createHistoryEventsMsg(objId, reqId, eventsHistory);
                return gwBroker.sendToSingleCloudService(object, fullSrvId, resMsg, minReqPerm);
            } catch (JOSPProtocol.ParsingException e) {
            }
        }

        return false;
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
