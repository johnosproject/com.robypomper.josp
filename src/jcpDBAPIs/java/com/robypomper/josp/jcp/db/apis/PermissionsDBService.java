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

package com.robypomper.josp.jcp.db.apis;

import com.robypomper.josp.jcp.db.apis.entities.Object;
import com.robypomper.josp.jcp.db.apis.entities.Permission;
import com.robypomper.josp.jcp.db.apis.entities.ServiceStatus;
import com.robypomper.josp.protocol.JOSPPerm;
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

    public List<ServiceStatus> getServicesAllowed(String objId, String ownerId, JOSPPerm.Type minPermReq) {
        String[] types = getPermListFromMin(minPermReq);
        List<Permission> ps = permissions.findForObj(JOSPPerm.Connection.LocalAndCloud.name(), types, objId);

        List<ServiceStatus> srvs = new ArrayList<>();
        for (Permission p : ps)
            if (p.getUsrId().equals(JOSPPerm.WildCards.USR_ALL.toString())) {
                if (p.getSrvId().equals(JOSPPerm.WildCards.SRV_ALL.toString()))
                    srvs.addAll(servicesStatus.findAll());
                else
                    srvs.addAll(servicesStatus.findBySrvId(p.getSrvId()));
            } else if (p.getUsrId().equals(JOSPPerm.WildCards.USR_OWNER.toString())) {
                if (p.getSrvId().equals(JOSPPerm.WildCards.SRV_ALL.toString())) {
                    if (ownerId.equals(JOSPPerm.WildCards.USR_ANONYMOUS_ID.toString()))
                        srvs.addAll(servicesStatus.findAll());
                    else
                        srvs.addAll(servicesStatus.findByUsrId(ownerId));
                } else {
                    if (ownerId.equals(JOSPPerm.WildCards.USR_ANONYMOUS_ID.toString()))
                        srvs.addAll(servicesStatus.findBySrvId(p.getSrvId()));
                    else
                        srvs.addAll(servicesStatus.findBySrvIdAndUsrId(p.getSrvId(), ownerId));
                }
            } else {
                if (p.getSrvId().equals(JOSPPerm.WildCards.SRV_ALL.toString()))
                    srvs.addAll(servicesStatus.findByUsrId(p.getUsrId()));
                else
                    srvs.addAll(servicesStatus.findBySrvIdAndUsrId(p.getSrvId(), p.getUsrId()));
            }

        return srvs;
    }

    public List<Object> getObjectAllowed(String srvId, String usrId, JOSPPerm.Type minPermReq) {
        String[] types = getPermListFromMin(minPermReq);
        List<Permission> ps = permissions.findForSrv(JOSPPerm.Connection.LocalAndCloud.name(), types, srvId, usrId);

        List<Object> objs = new ArrayList<>();
        for (Permission p : ps) {
            Optional<Object> optObj = objects.findById(p.getObjId());
            optObj.ifPresent(objs::add);
        }
        return objs;
    }


    // Queries utils

    private String[] getPermListFromMin(JOSPPerm.Type minPermReq) {
        if (minPermReq == JOSPPerm.Type.Status) {
            return new String[]{
                    JOSPPerm.Type.Status.name(),
                    JOSPPerm.Type.Actions.name(),
                    JOSPPerm.Type.CoOwner.name()
            };

        } else if (minPermReq == JOSPPerm.Type.Actions) {
            return new String[]{
                    JOSPPerm.Type.Actions.name(),
                    JOSPPerm.Type.CoOwner.name()
            };

        } else if (minPermReq == JOSPPerm.Type.CoOwner) {
            return new String[]{
                    JOSPPerm.Type.CoOwner.name()
            };
        }

        return new String[]{};
    }

    public static JOSPPerm dbPermToJOSPPerm(Permission permDB) {
        return new JOSPPerm(permDB.getId(), permDB.getObjId(), permDB.getSrvId(), permDB.getUsrId(), permDB.getType(), permDB.getConnection(), permDB.getUpdatedAt());
    }

    public static List<JOSPPerm> dbPermsToJOSPPerms(List<Permission> permDB) {
        List<JOSPPerm> permsDB = new ArrayList<>();
        for (Permission p : permDB)
            permsDB.add(dbPermToJOSPPerm(p));
        return permsDB;
    }

    public static Permission jospPermToDBPerm(JOSPPerm permJOSP) {
        Permission permDB = new Permission();
        permDB.setId(permJOSP.getId());
        permDB.setObjId(permJOSP.getObjId());
        permDB.setSrvId(permJOSP.getSrvId());
        permDB.setUsrId(permJOSP.getUsrId());
        permDB.setType(permJOSP.getPermType());
        permDB.setConnection(permJOSP.getConnType());
        permDB.setPermissionUpdatedAt(permJOSP.getUpdatedAt());
        return permDB;
    }

    public static List<Permission> jospPermsToDBPerms(List<JOSPPerm> permsJOSP) {
        List<Permission> permsDB = new ArrayList<>();
        for (JOSPPerm p : permsJOSP)
            permsDB.add(jospPermToDBPerm(p));
        return permsDB;
    }

}
