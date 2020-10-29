package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.ObjectStatusHistory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class StatusHistoryDBService {

    // Internal vars

    private final StatusHistoryRepository statusesHistory;

    // Constructor

    public StatusHistoryDBService(StatusHistoryRepository statusesHistory) {
        this.statusesHistory = statusesHistory;
    }


    public ObjectStatusHistory add(ObjectStatusHistory stock) throws DataIntegrityViolationException {
        return statusesHistory.save(stock);
    }
}
