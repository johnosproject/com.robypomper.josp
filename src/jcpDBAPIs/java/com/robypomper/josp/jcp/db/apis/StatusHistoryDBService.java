/*******************************************************************************
 * The John Cloud Platform is the set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.jcp.db.apis;

import com.robypomper.josp.jcp.db.apis.entities.ObjectStatusHistory;
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
