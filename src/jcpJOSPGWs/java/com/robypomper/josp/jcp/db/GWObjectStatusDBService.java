package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.GWObjectStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GWObjectStatusDBService {

    // Internal vars

    private final GWObjectStatusRepository objects;


    // Constructor

    public GWObjectStatusDBService(GWObjectStatusRepository objects) {
        this.objects = objects;
    }

    // Access methods

    public Optional<GWObjectStatus> find(String objId) throws DataIntegrityViolationException {
        return objects.findById(objId);
    }

    public GWObjectStatus addOrUpdate(GWObjectStatus stock) throws DataIntegrityViolationException {
        return objects.save(stock);
    }

}
