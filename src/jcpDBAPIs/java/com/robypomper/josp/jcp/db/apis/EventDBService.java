/* *****************************************************************************
 * The John Cloud Platform set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2020 Roberto Pompermaier
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
 **************************************************************************** */

package com.robypomper.josp.jcp.db.apis;

import com.robypomper.josp.jcp.db.apis.entities.Event;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPEvent;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EventDBService {

    // Internal vars

    private final EventRepository events;


    // Constructor

    public EventDBService(EventRepository events) {
        this.events = events;
    }


    // Access methods


    // Access methods

    public List<Event> findBySrcId(String srcId) {
        return events.findBySrcIdOrderByEmittedAtDesc(srcId);
    }

    public List<Event> findBySrcIdAndEvnType(String srcId, JOSPEvent.Type type) {
        return events.findBySrcIdAndEvnType(srcId, type);
    }

    public Event add(Event stock) throws DataIntegrityViolationException {
        return events.save(stock);
    }

    public List<Event> find(String srcId, HistoryLimits limits) {
        if (limits.isLatestCount())
            return events.findBySrcId(srcId, PageRequest.of(0, (int) (long) limits.getLatestCount(), Sort.by(Sort.Direction.DESC, "evnId")));

        if (limits.isAncientCount())
            return events.findBySrcId(srcId, PageRequest.of(0, (int) (long) limits.getAncientCount(), Sort.by(Sort.Direction.ASC, "evnId")));

        if (limits.isDateRange())
            return events.findBySrcIdAndEmittedAtBetween(srcId, limits.getFromDate(), limits.getToDate());

        if (limits.isIDRange())
            return events.findBySrcIdAndEvnIdBetween(srcId, limits.getFromId(), limits.getToId());

        return events.findBySrcId(srcId);
    }

}
