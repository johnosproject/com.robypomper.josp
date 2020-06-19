package com.robypomper.josp.jcp.gw;

import com.robypomper.communication.server.Server;
import com.robypomper.josp.jcp.db.PermissionsDBService;
import com.robypomper.josp.jcp.db.entities.Object;
import com.robypomper.josp.jcp.db.entities.Permission;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_CloudRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class JOSPGWsBroker {

    @Autowired
    private PermissionsDBService permissionDBService;
    private final Map<String, GWObject> objects = new HashMap<>();
    private final Map<String, GWService> services = new HashMap<>();


    // GWObject's method

    // from GWObject
    public void registerObject(GWObject object, Object obj) {
        objects.put(object.getObjId(), object);
        // send registered object's info & struct to allowed services
        for (GWService service : getAllowedServices(object.getObjId()))
            sendObjectPresentationToService(obj, service);
    }

    // from GWObject
    public void deregisterObject(GWObject object) {
        objects.remove(object.getObjId());
        for (GWService service : getAllowedServices(object.getObjId()))
            sendObjectDisconnectionToService(object, service);
    }

    // from GWObject
    public void statusToServices(JOSPProtocol.StatusUpd upd) {
        // get allowed service to receive status updates
        // for each allowed service, send status update
        for (GWService service : getAllowedServices(upd.getObjectId()))
            sendUpdate(service, upd);
    }


    // GWService's method

    // from GWService
    public void registerService(GWService service) {
        services.put(service.getFullId(), service);
        // send allowed object's info & struct to registered service
        for (Object obj : permissionDBService.getObjectStatusAllowed(service.getSrvId(), service.getUsrId()))
            sendObjectPresentationToService(obj, service);
    }

    // from GWService
    public void deregisterService(GWService service) {
        services.remove(service.getFullId());
    }


    // PermissionsController's method

    // from PermissionsController
    public void updatedObjectsPermissions(List<Permission> oldPermissions, List<Permission> newPermissions) {
        // send / revoke ObjectPresentation for removed / updated / added permissions
    }


    // Send methods

    public boolean sendToObject(GWService service, String objId, String msg, PermissionsTypes.Type minReqPerm) {
        GWObject object = objects.get(objId);
        if (serviceCanSendToObject(service, object, minReqPerm))
            return false;

        try {
            object.sendData(msg);

        } catch (Server.ServerStoppedException | Server.ClientNotConnectedException e) {
            return false;
        }

        return true;
    }

    private void sendObjectPresentationToService(Object obj, GWService service) {
        try {
            service.sendData(JOSPProtocol_CloudRequests.createObjectInfoResponse(obj.getObjId(), obj.getName(), obj.getOwner().getOwnerId(), obj.getVersion()));
            service.sendData(JOSPProtocol_CloudRequests.createObjectStructureResponse(obj.getObjId(), obj.getStatus().getLastStructUpdate(), obj.getStatus().getStructure(), obj.getStatus().isOnline()));
        } catch (Server.ServerStoppedException | Server.ClientNotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void sendRevokeObjectPresentationToService(GWObject object, GWService service) {
//        try {
//            service.sendData(JOSPProtocol_CloudRequests.createRevokeObjectInfoResponse(obj.getObjId()));
//        } catch (Server.ServerStoppedException | Server.ClientNotConnectedException e) {
//            e.printStackTrace();
//        }
    }

    private void sendObjectDisconnectionToService(GWObject object, GWService service) {
        try {
            service.sendData(JOSPProtocol_CloudRequests.createObjectDisconnectionResponse(object.getObjId()));
        } catch (Server.ServerStoppedException | Server.ClientNotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void sendUpdate(GWService service, JOSPProtocol.StatusUpd upd) {
        try {
            service.sendData(JOSPProtocol.fromUpdToMsg(upd));

        } catch (Server.ServerStoppedException | Server.ClientNotConnectedException e) {
            e.printStackTrace();
        }
    }


    // Permission's methods

    // ToDo update param to GWObject and check minReqPerm
    private List<GWService> getAllowedServices(String objId) {
        List<GWService> allowedSrvs = new ArrayList<>();

        for (GWService service : services.values()) {
            List<Object> allowedObjs = permissionDBService.getObjectStatusAllowed(service.getSrvId(), service.getUsrId());
            for (Object obj : allowedObjs)
                if (obj.getObjId().equals(objId))
                    allowedSrvs.add(service);
        }

        return allowedSrvs;
    }

    private boolean serviceCanSendToObject(GWService service, GWObject object, PermissionsTypes.Type minReqPerm) {
        // ToDo add minReqPerm check
        List<GWService> allowedServices = getAllowedServices(object.getObjId());
        return allowedServices.contains(service);
    }
}
