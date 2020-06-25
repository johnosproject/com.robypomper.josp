package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.Object;
import com.robypomper.josp.jcp.db.entities.Permission;
import com.robypomper.josp.jcp.db.entities.ServiceStatus;
import com.robypomper.josp.protocol.JOSPPermissions;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class PermissionsDBService {

    // Internal vars

    private final PermissionRepository permissions;
    private final ObjectRepository objects;
    private final ServiceStatusRepository servicesStatus;


    // Constructor

    public PermissionsDBService(PermissionRepository permissions, ObjectRepository objects, ServiceStatusRepository servicesStatus) {
        this.permissions = permissions;
        this.objects = objects;
        this.servicesStatus = servicesStatus;
    }


    // Access methods

    public List<Permission> findByObj(String objId) throws DataIntegrityViolationException {
        return permissions.findByObjId(objId);
    }

    public List<Permission> addAll(List<Permission> objPerms) {
        return permissions.saveAll(objPerms);
    }

    public void removeAll(List<Permission> objPerms) {
        permissions.deleteAll(objPerms);
    }


    // Permission queries

    public List<ServiceStatus> getServicesAllowed(String objId, String ownerId, JOSPPermissions.Type minPermReq) {
        String[] types = getPermListFromMin(minPermReq);
        List<Permission> ps = permissions.findForObj(JOSPPermissions.Connection.LocalAndCloud.name(), types, objId);

        List<ServiceStatus> srvs = new ArrayList<>();
        for (Permission p : ps)
            if (p.getUsrId().equals(JOSPPermissions.WildCards.USR_ALL.toString())) {
                if (p.getSrvId().equals(JOSPPermissions.WildCards.SRV_ALL.toString()))
                    srvs.addAll(servicesStatus.findAll());
                else
                    srvs.addAll(servicesStatus.findBySrvId(p.getSrvId()));
            } else if (p.getUsrId().equals(JOSPPermissions.WildCards.USR_OWNER.toString())) {
                if (p.getSrvId().equals(JOSPPermissions.WildCards.SRV_ALL.toString())) {
                    if (ownerId.equals(JOSPPermissions.WildCards.USR_ANONYMOUS_ID.toString()))
                        srvs.addAll(servicesStatus.findAll());
                    else
                        srvs.addAll(servicesStatus.findByUsrId(ownerId));
                } else {
                    if (ownerId.equals(JOSPPermissions.WildCards.USR_ANONYMOUS_ID.toString()))
                        srvs.addAll(servicesStatus.findBySrvId(p.getSrvId()));
                    else
                        srvs.addAll(servicesStatus.findBySrvIdAndUsrId(p.getSrvId(), ownerId));
                }
            } else {
                if (p.getSrvId().equals(JOSPPermissions.WildCards.SRV_ALL.toString()))
                    srvs.addAll(servicesStatus.findByUsrId(p.getUsrId()));
                else
                    srvs.addAll(servicesStatus.findBySrvIdAndUsrId(p.getSrvId(), p.getUsrId()));
            }

        return srvs;
    }

    public List<Object> getObjectAllowed(String srvId, String usrId, JOSPPermissions.Type minPermReq) {
        String[] types = getPermListFromMin(minPermReq);
        List<Permission> ps = permissions.findForSrv(JOSPPermissions.Connection.LocalAndCloud.name(), types, srvId, usrId);

        List<Object> objs = new ArrayList<>();
        for (Permission p : ps) {
            Optional<Object> optObj = objects.findById(p.getObjId());
            optObj.ifPresent(objs::add);
        }
        return objs;
    }


    // Queries utils

    private String[] getPermListFromMin(JOSPPermissions.Type minPermReq) {
        if (minPermReq == JOSPPermissions.Type.Status) {
            return new String[]{
                    JOSPPermissions.Type.Status.name(),
                    JOSPPermissions.Type.Actions.name(),
                    JOSPPermissions.Type.CoOwner.name()
            };

        } else if (minPermReq == JOSPPermissions.Type.Actions) {
            return new String[]{
                    JOSPPermissions.Type.Actions.name(),
                    JOSPPermissions.Type.CoOwner.name()
            };

        } else if (minPermReq == JOSPPermissions.Type.CoOwner) {
            return new String[]{
                    JOSPPermissions.Type.CoOwner.name()
            };
        }

        return new String[]{};
    }

}
