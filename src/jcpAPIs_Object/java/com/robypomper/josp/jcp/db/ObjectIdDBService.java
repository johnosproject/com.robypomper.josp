package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.ObjectId;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ObjectIdDBService {

    // Internal vars

    private final ObjectIdRepository objectIds;


    // Constructor

    public ObjectIdDBService(ObjectIdRepository objectIds) {
        this.objectIds = objectIds;
    }


    // Access methods

    public Optional<ObjectId> find(String objIdHw, String usrId) {
        return objectIds.findByObjIdHwAndUsrId(objIdHw, usrId);
    }

    public ObjectId add(ObjectId stock) throws DataIntegrityViolationException {
        return objectIds.save(stock);
    }
}
