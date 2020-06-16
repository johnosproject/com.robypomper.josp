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

    public void save(Service srv) throws DataIntegrityViolationException {
        srv = services.save(srv);
        // ToDo check references returned
    }

    public void save(ServiceStatus srvStatus) throws DataIntegrityViolationException {
        srvStatus = servicesStatus.save(srvStatus);
        // ToDo check references returned
    }

}
