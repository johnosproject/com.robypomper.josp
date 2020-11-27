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

import com.robypomper.java.JavaNetworks;
import com.robypomper.josp.jcp.db.apis.entities.GW;
import com.robypomper.josp.types.josp.gw.GWType;
import org.springframework.dao.DataIntegrityViolationException;

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

    public Optional<GW> findAvailableGW(GWType type) throws DataIntegrityViolationException {
        // List all gateways
        List<GW> allGWs = gws.findAll();

        // Find the most free, of right type and online GW
        int maxFreeCount = 0;
        GW gwMaxFree = null;
        for (GW gw : allGWs) {
            int freeCount = gw.getClientsMax() - gw.getStatus().getClients();
            if (freeCount > maxFreeCount && gw.getType() == type && gw.getStatus().isOnline()) {
                maxFreeCount = freeCount;
                gwMaxFree = gw;
            }
        }

        // If no GW available
        if (gwMaxFree == null)
            return Optional.empty();

        // Check if GW is NOT reachable
        if (!JavaNetworks.pingHost(gwMaxFree.getGwAddr(), gwMaxFree.getGwPort(), 5000)
                || !JavaNetworks.pingHost(gwMaxFree.getGwAPIsAddr(), gwMaxFree.getGwAPIsPort(), 5000)) {
            // Set offline
            gwMaxFree.getStatus().setOnline(false);
            //gws.save(gwMaxFree);
            gwsStatus.save(gwMaxFree.getStatus());

            // Recursive search
            return findAvailableGW(type);
        }

        // Return founded GW
        return Optional.of(gwMaxFree);
    }

    public void checkAllGWsAvailability() throws DataIntegrityViolationException {
        // List all gateways
        List<GW> allGWs = gws.findAll();
        for (GW gw : allGWs) {
            // Check if GW is reachable
            gw.getStatus().setOnline(JavaNetworks.pingHost(gw.getGwAddr(), gw.getGwPort(), 5000)
                    || JavaNetworks.pingHost(gw.getGwAPIsAddr(), gw.getGwAPIsPort(), 5000));

            // Save new GW state
            //gws.save(gwMaxFree);
            gwsStatus.saveAndFlush(gw.getStatus());
        }
    }

    public Iterable<GW> getAll() {
        return gws.findAll();
    }

    public GW save(GW gw) {
        return gws.save(gw);
    }

    public void delete(GW gw) {
        gws.delete(gw);
    }

}
