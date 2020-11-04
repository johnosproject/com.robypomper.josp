package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.ObjectStatusHistory;
import com.robypomper.josp.protocol.HistoryLimits;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<ObjectStatusHistory> find(String objId, String compPath, HistoryLimits limits) {
        return statusesHistory.findByObjIdAndCompPath(objId, compPath);
    }
}
