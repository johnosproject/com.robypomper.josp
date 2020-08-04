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

package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.Service;
import com.robypomper.josp.jcp.db.entities.ServiceStatus;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;


@org.springframework.stereotype.Service
public class ServiceDBService {

    // Internal vars

    private final ServiceRepository services;
    private final ServiceStatusRepository servicesStatus;

    // Constructor

    public ServiceDBService(ServiceRepository services, ServiceStatusRepository servicesStatus) {
        this.services = services;
        this.servicesStatus = servicesStatus;
    }


    // Access methods

    public Optional<Service> find(String srvId) {
        return services.findById(srvId);
    }

    public Optional<ServiceStatus> findStatus(String fullSrvId) {
        return servicesStatus.findById(fullSrvId);
    }

    public Service save(Service srv) throws DataIntegrityViolationException {
        return services.save(srv);
    }

    public ServiceStatus save(ServiceStatus srvStatus) throws DataIntegrityViolationException {
        return servicesStatus.save(srvStatus);
    }

}
