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

import com.robypomper.communication.server.Server;
import com.robypomper.josp.jcp.db.EventDBService;
import com.robypomper.josp.jcp.db.ObjectDBService;
import com.robypomper.josp.jcp.db.PermissionsDBService;
import com.robypomper.josp.jcp.db.StatusHistoryDBService;
import com.robypomper.josp.jcp.db.entities.Object;
import com.robypomper.josp.jcp.db.entities.Permission;
import com.robypomper.josp.jcp.db.entities.ServiceStatus;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import com.robypomper.josp.protocol.JOSPProtocol_ServiceToObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class JOSPGWsBroker {

    // Internal vars

    private final Map<String, GWObject> objects = new HashMap<>();
    private final Map<String, GWService> services = new HashMap<>();
    @Autowired
    private PermissionsDBService permissionsDBService;
    @Autowired
    private ObjectDBService objectDBService;
    @Autowired
    private EventDBService eventsDBService;
    @Autowired
    private StatusHistoryDBService statusesHistoryDBService;


    // GWObject's method

    public void registerObject(GWObject object) {
        objects.put(object.getObjId(), object);
    }

    public void deregisterObject(GWObject object) {
        objects.remove(object.getObjId());
        for (GWService service : getAllowedServices(object, JOSPPerm.Type.Status))
            sendObjectDisconnectionToService(object, service);
    }

    public GWObject findObject(String objId) {
        return objects.get(objId);
    }


    // GWService's method

    public void registerService(GWService service) {
        services.put(service.getFullId(), service);
        // send allowed object's presentation to registered service
        for (GWObject object : getAllowedObjects(service, JOSPPerm.Type.Status)) {
            sendObjectInfoToService(object, service);
            sendObjectStructToService(object, service);
            sendObjectPermsToService(object, service);
            sendServicePermToService(object, service);
        }
    }

    public void deregisterService(GWService service) {
        services.remove(service.getFullId());
    }


    // Send methods

    public boolean sendToServices(GWObject object, String msg, JOSPPerm.Type minReqPerm) {
        for (GWService service : getAllowedServices(object, minReqPerm)) {
            if (!objectCanSendToService(object, service, minReqPerm))
                continue;

            try {
                if (JOSPProtocol_ObjectToService.isObjectStateUpdMsg(msg)) {
                    service.getSrvStatus().setLastStatusUpdAt(JOSPProtocol.getNowDate());
                    service.getSrvStatus().setLastStatusUpdSender(object.getObjId());
                    service.updateStatusToDB();
                }

                service.sendData(msg);

            } catch (Server.ServerStoppedException | Server.ClientNotConnectedException ignore) {}
        }

        return true;
    }

    public boolean sendToSingleCloudService(GWObject object, String fullSrvId, String msg, JOSPPerm.Type minReqPerm) {
        GWService service = services.get(fullSrvId);
        if (minReqPerm != JOSPPerm.Type.None && !objectCanSendToService(object, service, minReqPerm))
            return false;

        try {
            service.sendData(msg);

        } catch (Server.ServerStoppedException | Server.ClientNotConnectedException e) {
            return false;
        }

        return true;
    }

    public boolean sendToObject(GWService service, String objId, String msg, JOSPPerm.Type minReqPerm) {
        GWObject object = objects.get(objId);
        if (!serviceCanSendToObject(service, object, minReqPerm))
            return false;

        try {
            if (JOSPProtocol_ServiceToObject.isObjectActionCmdMsg(msg)) {
                object.getObj().getStatus().setLastActionCmdAt(JOSPProtocol.getNowDate());
                object.getObj().getStatus().setLastActionCmdSender(service.getFullId());
                object.updateStatusToDB();
                service.getSrvStatus().setLastActionCmdAt(JOSPProtocol.getNowDate());
                service.getSrvStatus().setLastActionCmdReceiver(object.getObjId());
                service.updateStatusToDB();
            }

            object.sendData(msg);

        } catch (Server.ServerStoppedException | Server.ClientNotConnectedException e) {
            return false;
        }

        return true;
    }


    // Sender (obj > srv)

    private void sendObjectInfoToService(GWObject object, GWService service) {
        String msg = JOSPProtocol_ObjectToService.createObjectInfoMsg(object.getObjId(), object.getObj().getName(), object.getObj().getVersion(), object.getObj().getOwner().getOwnerId(), object.getObj().getInfo().getModel(), object.getObj().getInfo().getBrand(), object.getObj().getInfo().getLongDescr(),object.getObj().getStatus().isOnline());
        sendToSingleCloudService(object, service.getFullId(), msg, JOSPPerm.Type.Status);
    }

    private void sendObjectStructToService(GWObject object, GWService service) {
        String msg = JOSPProtocol_ObjectToService.createObjectStructMsg(object.getObjId(), object.getObj().getStatus().getStructure());
        sendToSingleCloudService(object, service.getFullId(), msg, JOSPPerm.Type.Status);
    }

    private void sendObjectPermsToService(GWObject object, GWService service) {
        List<Permission> perms = permissionsDBService.findByObj(object.getObjId());
        List<JOSPPerm> jospPerms = new ArrayList<>();
        for (Permission p : perms)
            jospPerms.add(new JOSPPerm(p.getId(), p.getObjId(), p.getSrvId(), p.getUsrId(), p.getType(), p.getConnection(), p.getUpdatedAt()));

        String msg = JOSPProtocol_ObjectToService.createObjectPermsMsg(object.getObjId(), JOSPPerm.toString(jospPerms));
        sendToSingleCloudService(object, service.getFullId(), msg, JOSPPerm.Type.CoOwner);
    }

    private void sendServicePermToService(GWObject object, GWService service) {
        JOSPPerm.Type permType = JOSPPerm.Type.None;
        if (getAllowedServices(object, JOSPPerm.Type.CoOwner).contains(service))
            permType = JOSPPerm.Type.CoOwner;
        else if (getAllowedServices(object, JOSPPerm.Type.Actions).contains(service))
            permType = JOSPPerm.Type.Actions;
        else if (getAllowedServices(object, JOSPPerm.Type.Status).contains(service))
            permType = JOSPPerm.Type.Status;

        JOSPPerm.Connection permConn = JOSPPerm.Connection.LocalAndCloud;

        String msg = JOSPProtocol_ObjectToService.createServicePermMsg(object.getObjId(), permType, permConn);
        sendToSingleCloudService(object, service.getFullId(), msg, JOSPPerm.Type.Status);
    }

    private void sendObjectDisconnectionToService(GWObject object, GWService service) {
        String msg = JOSPProtocol_ObjectToService.createObjectDisconnectMsg(object.getObjId());
        sendToSingleCloudService(object, service.getFullId(), msg, JOSPPerm.Type.Status);
    }

    public void updateObjectPerms(GWObject object) {
        List<GWService> ownerServices = getAllowedServices(object, JOSPPerm.Type.CoOwner);
        for (GWService service : getAllowedServices(object, JOSPPerm.Type.Status)) {
            sendObjectInfoToService(object, service);
            sendObjectStructToService(object, service);
            if (ownerServices.contains(service))
                sendObjectPermsToService(object, service);
            sendServicePermToService(object, service);
        }
    }

    // Permission's methods

    public List<GWService> getAllowedServices(GWObject obj, JOSPPerm.Type minReqPerm) {
        List<GWService> allowedSrvs = new ArrayList<>();

        List<ServiceStatus> allowedServices = permissionsDBService.getServicesAllowed(obj.getObjId(), obj.getObj().getOwner().getOwnerId(), minReqPerm);
        for (ServiceStatus allowedService : allowedServices) {
            GWService srv = services.get(allowedService.getFullId());
            if (srv != null)
                allowedSrvs.add(srv);
        }

        return allowedSrvs;
    }

    public boolean objectCanSendToService(GWObject object, GWService service, JOSPPerm.Type minReqPerm) {
        List<GWService> allowedServices = getAllowedServices(object, minReqPerm);
        return allowedServices.contains(service);
    }

    public List<GWObject> getAllowedObjects(GWService srv, JOSPPerm.Type minReqPerm) {
        List<GWObject> allowedObjs = new ArrayList<>();

        List<Object> allowedObjects = permissionsDBService.getObjectAllowed(srv.getSrvId(), srv.getUsrId(), minReqPerm);
        for (Object allowedObject : allowedObjects) {
            GWObject gwObj = objects.get(allowedObject.getObjId());
            if (gwObj == null)
                gwObj = new GWObject(allowedObject, objectDBService, permissionsDBService, eventsDBService, statusesHistoryDBService, this);
            allowedObjs.add(objects.get(allowedObject.getObjId()));
        }

        return allowedObjs;
    }

    public boolean serviceCanSendToObject(GWService service, GWObject object, JOSPPerm.Type minReqPerm) {
        List<GWObject> allowedObjects = getAllowedObjects(service, minReqPerm);
        return allowedObjects.contains(object);
    }

}
