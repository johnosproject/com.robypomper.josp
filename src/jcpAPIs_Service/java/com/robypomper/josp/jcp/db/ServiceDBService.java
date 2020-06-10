package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.Service;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;


@org.springframework.stereotype.Service
public class ServiceDBService {

    // Internal vars

    private final ServiceRepository services;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Service> service = Optional.empty();


    // Constructor

    public ServiceDBService(ServiceRepository services) {
        this.services = services;
    }


    // Access methods

    public Optional<Service> find(String srvId) {
        return services.findById(srvId);
    }

    public void save(Service srv) throws DataIntegrityViolationException {
        srv = services.save(srv);
        // ToDo check references returned
    }

}
