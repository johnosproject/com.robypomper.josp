package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.Service;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Optional;


@org.springframework.stereotype.Service
@SessionScope
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

    public Optional<Service> get(String srvId) {
        if (!service.isPresent() || service.get().getSrvId().compareTo(srvId) != 0)
            service = services.findById(srvId);
        return service;
    }

    public Service add(Service stock) throws DataIntegrityViolationException {
        return services.save(stock);
    }

}
