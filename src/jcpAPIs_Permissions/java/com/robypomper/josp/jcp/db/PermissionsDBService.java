package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.apis.params.permissions.PermissionsTypes;
import com.robypomper.josp.jcp.db.entities.Object;
import com.robypomper.josp.jcp.db.entities.Permission;
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


    // Constructor

    public PermissionsDBService(PermissionRepository permissions, ObjectRepository objects) {
        this.permissions = permissions;
        this.objects = objects;
    }


    // Access methods

    public List<Permission> findByObj(String objId) throws DataIntegrityViolationException {
        return permissions.findByObjId(objId);
    }

    public List<Permission> findBySrv(String srvId) throws DataIntegrityViolationException {
        return permissions.findBySrvId(srvId);
    }

    public List<Permission> findByUsr(String usrId) throws DataIntegrityViolationException {
        return permissions.findByUsrId(usrId);
    }

    public List<Permission> addAll(List<Permission> objPerms) {
        return permissions.saveAll(objPerms);
    }

    public void removeAll(List<Permission> objPerms) {
        permissions.deleteAll(objPerms);
    }


    // Permission queries

    public List<Object> getObjectStatusAllowed(String srvId, String usrId) {
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

    public boolean isObjectActionAllowed(String objId, String srvId, String usrId) {
        String[] types = new String[]{
                PermissionsTypes.Type.Actions.name(),
                PermissionsTypes.Type.CoOwner.name()
        };
        List<Permission> ps = permissions.findForSrv(PermissionsTypes.Connection.LocalAndCloud.name(), types, srvId, usrId);

        for (Permission p : ps)
            if (p.getObjId().equals(objId))
                return true;

        return false;
    }

}
