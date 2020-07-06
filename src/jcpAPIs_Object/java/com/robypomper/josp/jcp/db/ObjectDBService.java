package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.Object;
import com.robypomper.josp.jcp.db.entities.ObjectId;
import com.robypomper.josp.jcp.db.entities.ObjectStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ObjectDBService {

    // Internal vars

    private final ObjectRepository objects;
    private final ObjectIdRepository objectsId;
    private final ObjectOwnerRepository objectsOwner;
    private final ObjectStatusRepository objectsStatus;


    // Constructor

    public ObjectDBService(ObjectRepository objects,
                           ObjectIdRepository objectsId,
                           ObjectOwnerRepository objectsOwner,
                           ObjectStatusRepository objectsStatus) {
        this.objects = objects;
        this.objectsId = objectsId;
        this.objectsOwner = objectsOwner;
        this.objectsStatus = objectsStatus;
    }


    // Access methods

    public Optional<Object> find(String objId) throws DataIntegrityViolationException {
        return objects.findById(objId);
    }

    public ObjectId save(ObjectId objId) throws DataIntegrityViolationException {
        return objectsId.save(objId);
    }

    public Object save(Object obj) throws DataIntegrityViolationException {
        assert obj.getOwner() != null;
        assert obj.getOwner().getObjId() != null;
        assert obj.getInfo() != null;
        assert obj.getInfo().getObjId() != null;

        return objects.save(obj);
    }

    public ObjectStatus save(ObjectStatus objStatus) throws DataIntegrityViolationException {
        return objectsStatus.save(objStatus);
    }

}
