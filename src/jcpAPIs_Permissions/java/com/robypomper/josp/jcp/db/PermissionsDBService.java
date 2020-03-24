package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.Permission;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.List;


@Service
@SessionScope
public class PermissionsDBService {

    // Internal vars

    private final PermissionRepository permissions;


    // Constructor

    public PermissionsDBService(PermissionRepository permissions) {
        this.permissions = permissions;
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

}
