package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.ObjectStatusHistory;
import com.robypomper.josp.protocol.HistoryLimits;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        if (limits.isLatestCount())
            return statusesHistory.findByObjIdAndCompPath(objId, compPath, PageRequest.of(0, (int) (long) limits.getLatestCount(), Sort.by(Sort.Direction.DESC, "shId")));

        if (limits.isAncientCount())
            return statusesHistory.findByObjIdAndCompPath(objId, compPath, PageRequest.of(0, (int) (long) limits.getAncientCount(), Sort.by(Sort.Direction.ASC, "shId")));

        if (limits.isDateRange())
            return statusesHistory.findByObjIdAndCompPathAndUpdatedAtBetween(objId, compPath, limits.getFromDate(), limits.getToDate());

        if (limits.isIDRange())
            return statusesHistory.findByObjIdAndCompPathAndShIdBetween(objId, compPath, limits.getFromId(), limits.getToId());

        return statusesHistory.findByObjIdAndCompPath(objId, compPath);
    }
}
