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

package com.robypomper.josp.jcp.gws.o2s;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.Server;
import com.robypomper.josp.jcp.db.apis.EventDBService;
import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.db.apis.StatusHistoryDBService;
import com.robypomper.josp.jcp.db.apis.entities.Object;
import com.robypomper.josp.jcp.db.apis.entities.*;
import com.robypomper.josp.jcp.gws.broker.GWsBroker;
import com.robypomper.josp.jcp.gws.s2o.GWService;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.josp.protocol.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class GWObject {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final Server server;
    private final ClientInfo client;
    private final String objId;
    private Object objDB;
    private final ObjectDBService objectDBService;
    private final PermissionsDBService permissionsDBService;
    private final EventDBService eventsDBService;
    private final StatusHistoryDBService statusesHistoryDBService;
    private final GWsBroker gwBroker;


    // Constructor

    public GWObject(Server server, ClientInfo client, ObjectDBService objectDBService, PermissionsDBService permissionsDBService, EventDBService eventsDBService, StatusHistoryDBService statusesHistoryDBService, GWsBroker gwBroker) {
        this.server = server;
        this.client = client;
        this.objId = client.getClientId();
        this.objectDBService = objectDBService;
        this.permissionsDBService = permissionsDBService;
        this.eventsDBService = eventsDBService;
        this.statusesHistoryDBService = statusesHistoryDBService;
        this.gwBroker = gwBroker;

        gwBroker.registerObject(this);
    }

    public GWObject(Object allowedObject, ObjectDBService objectDBService, PermissionsDBService permissionsDBService, EventDBService eventsDBService, StatusHistoryDBService statusesHistoryDBService, GWsBroker gwBroker) {
        this.eventsDBService = eventsDBService;
        this.statusesHistoryDBService = statusesHistoryDBService;
        this.server = null;
        this.client = null;
        this.objId = allowedObject.getObjId();
        this.objDB = allowedObject;
        this.objectDBService = objectDBService;
        this.permissionsDBService = permissionsDBService;
        this.gwBroker = gwBroker;

        setOffline();
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
        objDB.getStatus().setLastDisconnectionAt(JOSPProtocol.getNowDate());
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
        return gwBroker.sendToServices(this, msg, JOSPPerm.Type.Status);
    }

    private boolean processObjectStructMsg(String msg) {
        // store struct to db
        updateStructToDB(msg);

        // send struct to services
        return gwBroker.sendToServices(this, msg, JOSPPerm.Type.Status);
    }

    private boolean processObjectPermsMsg(String msg) {
        // store perms to db
        updatePermsToDB(msg);

        // send perms to services
        boolean sendAll = gwBroker.sendToServices(this, msg, JOSPPerm.Type.CoOwner);

        // send servPerm to service(s)
        // ToDo rewrite to send only at service that was changed permission to access to the object
        List<GWService> updatedServices = new ArrayList<>();
        for (GWService service : gwBroker.getAllowedServices(this, JOSPPerm.Type.CoOwner)) {
            String srvPermMsg = JOSPProtocol_ObjectToService.createServicePermMsg(getObjId(), JOSPPerm.Type.CoOwner, JOSPPerm.Connection.LocalAndCloud);
            gwBroker.sendToSingleCloudService(this, service.getFullId(), srvPermMsg, JOSPPerm.Type.CoOwner);
            updatedServices.add(service);
        }
        for (GWService service : gwBroker.getAllowedServices(this, JOSPPerm.Type.Actions)) {
            if (updatedServices.contains(service))
                continue;
            String srvPermMsg = JOSPProtocol_ObjectToService.createServicePermMsg(getObjId(), JOSPPerm.Type.Actions, JOSPPerm.Connection.LocalAndCloud);
            gwBroker.sendToSingleCloudService(this, service.getFullId(), srvPermMsg, JOSPPerm.Type.Actions);
            updatedServices.add(service);
        }
        for (GWService service : gwBroker.getAllowedServices(this, JOSPPerm.Type.Status)) {
            if (updatedServices.contains(service))
                continue;
            String srvPermMsg = JOSPProtocol_ObjectToService.createServicePermMsg(getObjId(), JOSPPerm.Type.Status, JOSPPerm.Connection.LocalAndCloud);
            gwBroker.sendToSingleCloudService(this, service.getFullId(), srvPermMsg, JOSPPerm.Type.Status);
            updatedServices.add(service);
        }
        // ToDo update service that was revoked permission to access to the object

        return sendAll;
    }

    private boolean processUpdateMsg(String msg) {
        // update struct to db
        updateStructToDB(msg);

        // send update to services
        return gwBroker.sendToServices(this, msg, JOSPPerm.Type.Status);
    }


    // Update to DB

    public void updateStatusToDB() {
        objDB.setStatus(objectDBService.save(objDB.getStatus()));
    }

    private void updateInfoToDB(String msg) {
        String objId;
        String name;
        String ownerId;
        String jodVers;
        String model;
        String brand;
        String longDescr;
        try {
            objId = JOSPProtocol_ObjectToService.getObjId(msg);
            name = JOSPProtocol_ObjectToService.getObjectInfoMsg_Name(msg);
            ownerId = JOSPProtocol_ObjectToService.getObjectInfoMsg_OwnerId(msg);
            jodVers = JOSPProtocol_ObjectToService.getObjectInfoMsg_JODVersion(msg);
            model = JOSPProtocol_ObjectToService.getObjectInfoMsg_Model(msg);
            brand = JOSPProtocol_ObjectToService.getObjectInfoMsg_Brand(msg);
            longDescr = JOSPProtocol_ObjectToService.getObjectInfoMsg_LongDescr(msg);
        } catch (JOSPProtocol.ParsingException e) {
            return;
        }

        Optional<Object> optObj = objectDBService.find(objId);
        if (optObj.isPresent())
            objDB = optObj.get();
        else {
            objDB = new Object();
            objDB.setOwner(new ObjectOwner());
            objDB.setInfo(new ObjectInfo());
            objDB.setStatus(new ObjectStatus());
        }

        objDB.getOwner().setObjId(objId);
        objDB.getOwner().setOwnerId(ownerId);

        objDB.setObjId(objId);
        objDB.setName(name);
        objDB.setActive(true);
        objDB.setVersion(jodVers);

        objDB.getInfo().setObjId(objId);
        objDB.getInfo().setModel(model);
        objDB.getInfo().setBrand(brand);
        objDB.getInfo().setLongDescr(longDescr);

        objDB.getStatus().setObjId(objId);
        objDB.getStatus().setOnline(true);
        objDB.getStatus().setLastConnectionAt(JOSPProtocol.getNowDate());

        objDB = objectDBService.save(objDB);
    }

    private void updateStructToDB(String msg) {
        if (JOSPProtocol_ObjectToService.isObjectStructMsg(msg)) {
            String struct;
            try {
                struct = JOSPProtocol_ObjectToService.getObjectStructMsg_Struct(msg);
            } catch (JOSPProtocol.ParsingException e) {
                return;
            }
            if (objDB.getStatus().getStructure() == null
                    || !objDB.getStatus().getStructure().equals(struct)) {
                objDB.getStatus().setStructure(struct);
                objDB.getStatus().setLastStructUpdateAt(JOSPProtocol.getNowDate());
                updateStatusToDB();
            }

        } else if (JOSPProtocol_ObjectToService.isObjectStateUpdMsg(msg)) {

            JOSPProtocol.StatusUpd upd;
            try {
                upd = JOSPProtocol.extractStatusUpdFromMsg(msg);
            } catch (JOSPProtocol.ParsingException e) {
                return;
            }

            // Extract new state
            String newState;
            JOSPProtocol.JOSPStateUpdateStr updStr = (JOSPProtocol.JOSPStateUpdateStr) upd.getUpdate();
            if (JSLBooleanState.JOSPBoolean.class.getSimpleName().compareToIgnoreCase(updStr.getType()) == 0)
                newState = Boolean.toString(new JSLBooleanState.JOSPBoolean(updStr.encode()).newState);
            else if (JSLRangeState.JOSPRange.class.getSimpleName().compareToIgnoreCase(updStr.getType()) == 0)
                newState = Double.toString(new JSLRangeState.JOSPRange(updStr.encode()).newState);
            else
                return;

            // Write new state in component
            String structStr = objDB.getStatus().getStructure();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, java.lang.Object> structMap;
            try {
                structMap = mapper.readValue(structStr, Map.class);
            } catch (JsonProcessingException e) {
                return;
            }

            if (!(structMap.get("components") instanceof ArrayList))
                return;

            Map<String, java.lang.Object> updatableComp = null;
            ArrayList<Map<String, java.lang.Object>> comps = (ArrayList<Map<String, java.lang.Object>>) structMap.get("components");
            for (String compName : upd.getComponentPath().split(">")) {
                for (Map<String, java.lang.Object> subComp : comps)
                    if (subComp.get("name").equals(compName)) {
                        comps = (ArrayList<Map<String, java.lang.Object>>) subComp.get("components");
                        if (comps == null)
                            updatableComp = subComp;
                        break;
                    }
            }

            if (updatableComp == null)
                return;

            updatableComp.put("state", newState);

            try {
                structStr = mapper.writeValueAsString(structMap);
            } catch (JsonProcessingException e) {
                return;
            }
            objDB.getStatus().setStructure(structStr);
            objDB.getStatus().setLastStatusUpdAt(JOSPProtocol.getNowDate());
            updateStatusToDB();
        }
    }

    private void updatePermsToDB(String msg) {
        List<JOSPPerm> perms;
        try {
            perms = JOSPProtocol_ObjectToService.getObjectPermsMsg_Perms(msg);
        } catch (JOSPProtocol.ParsingException e) {
            return;
        }

        // ToDo improve permissions update (avoid removeAll/addAll)
        List<Permission> permsDB = new ArrayList<>();
        for (JOSPPerm p : perms) {
            Permission perm = new Permission();
            perm.setId(p.getId());
            perm.setObjId(p.getObjId());
            perm.setSrvId(p.getSrvId());
            perm.setUsrId(p.getUsrId());
            perm.setType(p.getPermType());
            perm.setConnection(p.getConnType());
            perm.setPermissionUpdatedAt(p.getUpdatedAt());
            permsDB.add(perm);
        }

        permissionsDBService.removeAll(permissionsDBService.findByObj(objId));
        permissionsDBService.addAll(permsDB);

        gwBroker.updateObjectPerms(this);
    }


    // Events and statuses internal requests

    public List<JOSPStatusHistory> getStatusesHistory(String compPath, HistoryLimits limits) {
        List<ObjectStatusHistory> objStatuses = statusesHistoryDBService.find(getObjId(), compPath, limits);

        List<JOSPStatusHistory> statusesHistory = new ArrayList<>();
        for (ObjectStatusHistory s : objStatuses)
            statusesHistory.add(ObjectStatusHistory.toJOSPStatusHistory(s));

        return statusesHistory;
    }

    public List<JOSPEvent> getEvents(HistoryLimits limits) {

        List<Event> objEvents = eventsDBService.find(getObjId(), limits);

        List<JOSPEvent> eventsHistory = new ArrayList<>();
        for (Event e : objEvents)
            eventsHistory.add(Event.toJOSPEvent(e));

        return eventsHistory;
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