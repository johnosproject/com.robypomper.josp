package com.robypomper.josp.jcp.gw;

import com.robypomper.communication.server.Server;
import com.robypomper.josp.jcp.db.PermissionsDBService;
import com.robypomper.josp.jcp.db.entities.Object;
import com.robypomper.josp.jcp.db.entities.ServiceStatus;
import com.robypomper.josp.protocol.JOSPPermissions;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
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


    // GWObject's method

    public void registerObject(GWObject object) {
        objects.put(object.getObjId(), object);
    }

    public void deregisterObject(GWObject object) {
        objects.remove(object.getObjId());
        for (GWService service : getAllowedServices(object, JOSPPermissions.Type.Status))
            sendObjectDisconnectionToService(object, service);
    }


    // GWService's method

    public void registerService(GWService service) {
        services.put(service.getFullId(), service);
        // send allowed object's presentation to registered service
        for (GWObject object : getAllowedObjects(service, JOSPPermissions.Type.Status)) {
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

    public boolean sendToServices(GWObject object, String msg, JOSPPermissions.Type minReqPerm) {
        for (GWService service : getAllowedServices(object, minReqPerm)) {
            if (!objectCanSendToService(object, service, minReqPerm))
                continue;

            try {
                service.sendData(msg);

            } catch (Server.ServerStoppedException | Server.ClientNotConnectedException ignore) {}
        }

        return true;
    }

    public boolean sendToSingleCloudService(GWObject object, String fullSrvId, String msg, JOSPPermissions.Type minReqPerm) {
        GWService service = services.get(fullSrvId);
        if (!objectCanSendToService(object, service, minReqPerm))
            return false;

        try {
            service.sendData(msg);

        } catch (Server.ServerStoppedException | Server.ClientNotConnectedException e) {
            return false;
        }

        return true;
    }

    public boolean sendToObject(GWService service, String objId, String msg, JOSPPermissions.Type minReqPerm) {
        GWObject object = objects.get(objId);
        if (!serviceCanSendToObject(service, object, minReqPerm))
            return false;

        try {
            object.sendData(msg);

        } catch (Server.ServerStoppedException | Server.ClientNotConnectedException e) {
            return false;
        }

        return true;
    }


    // Sender (obj > srv)

    private void sendObjectInfoToService(GWObject object, GWService service) {
        String msg = JOSPProtocol_ObjectToService.createObjectInfoMsg(object.getObjId(), object.getObj().getName(), object.getObj().getVersion(), object.getObj().getOwner().getOwnerId(), object.getObj().getInfo().getModel(), object.getObj().getInfo().getBrand(), object.getObj().getInfo().getLongDescr());
        sendToSingleCloudService(object, service.getFullId(), msg, JOSPPermissions.Type.Status);
    }

    private void sendObjectStructToService(GWObject object, GWService service) {
        String msg = JOSPProtocol_ObjectToService.createObjectStructMsg(object.getObjId(), object.getObj().getStatus().getStructure());
        sendToSingleCloudService(object, service.getFullId(), msg, JOSPPermissions.Type.Status);
    }

    private void sendObjectPermsToService(GWObject object, GWService service) {
        // ToDo get the all object's perms
        List<JOSPProtocol_ObjectToService.JOSPPerm> perms = new ArrayList<>();

        String permsStr = JOSPProtocol_ObjectToService.JOSPPerm.toString(perms);
        String msg = JOSPProtocol_ObjectToService.createObjectPermsMsg(object.getObjId(), permsStr);
        sendToSingleCloudService(object, service.getFullId(), msg, JOSPPermissions.Type.CoOwner);
    }

    private void sendServicePermToService(GWObject object, GWService service) {
        // ToDo get permission's type and conn for service>object
        JOSPPermissions.Type permType = JOSPPermissions.Type.Status;
        JOSPPermissions.Connection permConn = JOSPPermissions.Connection.LocalAndCloud;

        String msg = JOSPProtocol_ObjectToService.createServicePermMsg(object.getObjId(), permType, permConn);
        sendToSingleCloudService(object, service.getFullId(), msg, JOSPPermissions.Type.Status);
    }

    private void sendObjectDisconnectionToService(GWObject object, GWService service) {
        String msg = JOSPProtocol_ObjectToService.createObjectDisconnectMsg(object.getObjId());
        sendToSingleCloudService(object, service.getFullId(), msg, JOSPPermissions.Type.Status);
    }


    // Permission's methods

    public List<GWService> getAllowedServices(GWObject obj, JOSPPermissions.Type minReqPerm) {
        List<GWService> allowedSrvs = new ArrayList<>();

        List<ServiceStatus> allowedServices = permissionsDBService.getServicesAllowed(obj.getObjId(), obj.getObj().getOwner().getOwnerId(), minReqPerm);
        for (ServiceStatus allowedService : allowedServices) {
            GWService srv = services.get(allowedService.getFullId());
            if (srv != null)
                allowedSrvs.add(srv);
        }

        return allowedSrvs;
    }

    public boolean objectCanSendToService(GWObject object, GWService service, JOSPPermissions.Type minReqPerm) {
        List<GWService> allowedServices = getAllowedServices(object, minReqPerm);
        return allowedServices.contains(service);
    }

    public List<GWObject> getAllowedObjects(GWService srv, JOSPPermissions.Type minReqPerm) {
        List<GWObject> allowedObjs = new ArrayList<>();

        List<Object> allowedObjects = permissionsDBService.getObjectAllowed(srv.getSrvId(), srv.getUsrId(), minReqPerm);
        for (Object allowedObject : allowedObjects) {
            GWObject gwObj = objects.get(allowedObject.getObjId());
            if (gwObj != null)
                allowedObjs.add(objects.get(allowedObject.getObjId()));
        }

        return allowedObjs;
    }

    public boolean serviceCanSendToObject(GWService service, GWObject object, JOSPPermissions.Type minReqPerm) {
        List<GWObject> allowedObjects = getAllowedObjects(service, minReqPerm);
        return allowedObjects.contains(object);
    }

}
