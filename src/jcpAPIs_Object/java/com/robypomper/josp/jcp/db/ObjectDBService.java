package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.Object;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Optional;


@Service
@SessionScope
public class ObjectDBService {

    // Internal vars

    private final ObjectRepository objects;


    // Constructor

    public ObjectDBService(ObjectRepository objects) {
        this.objects = objects;
    }


    // Access methods

    public Optional<Object> find(String objId) throws DataIntegrityViolationException {
        return objects.findById(objId);
    }

    public Object add(Object stock) throws DataIntegrityViolationException {
        return objects.save(stock);
    }

    public Object update(Object stock) throws DataIntegrityViolationException {
        return objects.save(stock);
    }

}
