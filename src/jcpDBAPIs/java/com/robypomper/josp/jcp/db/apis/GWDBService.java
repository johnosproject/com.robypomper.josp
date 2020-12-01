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

import com.robypomper.josp.jcp.db.apis.entities.GW;
import com.robypomper.josp.types.josp.gw.GWType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@org.springframework.stereotype.Service
public class GWDBService {

    // Internal vars

    private final GWRepository gws;
    private final GWStatusRepository gwsStatus;


    // Constructor

    public GWDBService(GWRepository gws,
                       ObjectIdRepository objectsId,
                       ObjectOwnerRepository objectsOwner,
                       GWStatusRepository gwsStatus) {
        this.gws = gws;
        this.gwsStatus = gwsStatus;
    }


    // Access methods

    public Optional<GW> findById(String id) {
        return gws.findById(id);
    }

    public List<GW> getAll() {
        return gws.findAll();
    }

    public List<GW> getAllObj2Srv() {
        return gws.findByType(GWType.Obj2Srv);
    }

    public List<GW> getAllSrv2Obj() {
        return gws.findByType(GWType.Srv2Obj);
    }

    public List<GW> getAllOnline(boolean online) {
        List<GW> ret = getAllObj2SrvOnline(online);
        ret.addAll(getAllSrv2ObjOnline(online));
        return ret;
    }

    public List<GW> getAllObj2SrvOnline(boolean online) {
        List<GW> ret = new ArrayList<>();
        for (GW gw : gws.findByType(GWType.Obj2Srv))
            if (gw.getStatus().isOnline() == online)
                ret.add(gw);
        return ret;
    }

    public List<GW> getAllSrv2ObjOnline(boolean online) {
        List<GW> ret = new ArrayList<>();
        for (GW gw : gws.findByType(GWType.Srv2Obj))
            if (gw.getStatus().isOnline() == online)
                ret.add(gw);
        return ret;
    }


    // Storage methods

    public GW save(GW gw) {
        System.out.println(String.format("Save GW %s online %s", gw.getGwId(), gw.getStatus().isOnline()));
        return gws.save(gw);
    }

    public void delete(GW gw) {
        gws.delete(gw);
    }

}
