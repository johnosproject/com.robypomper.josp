package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.Object;
import com.robypomper.josp.jcp.db.entities.ObjectId;
import com.robypomper.josp.jcp.db.entities.ObjectOwner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ObjectDBService {

    // Internal vars

    private final ObjectRepository objects;
    private final ObjectIdRepository objectsId;
    private final ObjectOwnerRepository objectsOwner;


    // Constructor

    public ObjectDBService(ObjectRepository objects,
                           ObjectIdRepository objectsId,
                           ObjectOwnerRepository objectsOwner) {
        this.objects = objects;
        this.objectsId = objectsId;
        this.objectsOwner = objectsOwner;
    }


    // Access methods

    public Optional<Object> find(String objId) throws DataIntegrityViolationException {
        return objects.findById(objId);
    }

    public void save(ObjectId objId) throws DataIntegrityViolationException {
        objId = objectsId.save(objId);
        // ToDo check references returned
    }

    public void save(Object obj) throws DataIntegrityViolationException {
        assert obj.getOwner() != null;
        assert obj.getOwner().getObjId() != null;
        assert obj.getInfo() != null;
        assert obj.getInfo().getObjId() != null;

        obj = objects.save(obj);
        ObjectOwner objOwn = objectsOwner.save(obj.getOwner());
        // ToDo check references returned
    }

}
