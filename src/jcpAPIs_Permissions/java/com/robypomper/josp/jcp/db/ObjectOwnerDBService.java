package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.ObjectOwner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Optional;


@Service
@SessionScope
public class ObjectOwnerDBService {

    // Internal vars

    private final ObjectOwnerRepository objOwners;


    // Constructor

    public ObjectOwnerDBService(ObjectOwnerRepository objOwners) {
        this.objOwners = objOwners;
    }


    // Access methods

    public Optional<ObjectOwner> find(String objId) throws DataIntegrityViolationException {
        return objOwners.findById(objId);
    }

    public ObjectOwner add(ObjectOwner stock) throws DataIntegrityViolationException {
        return objOwners.save(stock);
    }

    public void update(ObjectOwner stock) throws DataIntegrityViolationException {
        objOwners.save(stock);
    }

}
