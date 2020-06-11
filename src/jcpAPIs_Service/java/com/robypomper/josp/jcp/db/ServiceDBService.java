package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.apis.params.permissions.PermissionsTypes;
import com.robypomper.josp.jcp.db.entities.Object;
import com.robypomper.josp.jcp.db.entities.Permission;
import com.robypomper.josp.jcp.db.entities.Service;
import com.robypomper.josp.jcp.db.entities.ServiceStatus;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@org.springframework.stereotype.Service
public class ServiceDBService {

    // Internal vars

    private final ServiceRepository services;
    private final ServiceStatusRepository servicesStatus;
    private final ObjectRepository objects;
    private final PermissionRepository permissions;

    // Constructor

    public ServiceDBService(ServiceRepository services, ServiceStatusRepository servicesStatus, ObjectRepository objects, PermissionRepository permissions) {
        this.services = services;
        this.servicesStatus = servicesStatus;
        this.objects = objects;
        this.permissions = permissions;
    }


    // Access methods

    public Optional<Service> find(String srvId) {
        return services.findById(srvId);
    }

    public Optional<ServiceStatus> findStatus(String fullSrvId) {
        return servicesStatus.findById(fullSrvId);
    }

    public void save(Service srv) throws DataIntegrityViolationException {
        srv = services.save(srv);
        // ToDo check references returned
    }

    public void save(ServiceStatus srvStatus) throws DataIntegrityViolationException {
        srvStatus = servicesStatus.save(srvStatus);
        // ToDo check references returned
    }


    public List<Object> getObjectPresentationAllowed(String srvId, String usrId) {
        String[] types = new String[]{
                PermissionsTypes.Type.Status.name(),
                PermissionsTypes.Type.Actions.name(),
                PermissionsTypes.Type.CoOwner.name()
        };
        List<Permission> ps = permissions.findForSrv(PermissionsTypes.Connection.LocalAndCloud.name(), types, srvId, usrId);

        List<Object> objs = new ArrayList<>();
        for (Permission p : ps) {
            Optional<Object> optObj = objects.findById(p.getObjId());
            optObj.ifPresent(objs::add);
        }
        return objs;
    }

}
