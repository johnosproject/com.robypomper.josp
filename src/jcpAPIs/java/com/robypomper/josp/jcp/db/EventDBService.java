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

package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.Event;
import com.robypomper.josp.protocol.JOSPEvent;
import org.springframework.dao.DataIntegrityViolationException;
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

//    public Optional<User> get(JOSPEvent.SourceType src, JOSPEvent.EventType type) {
//        if (!user.isPresent() || user.get().getUsrId().compareTo(usrId) != 0)
//            user = users.findById(usrId);
//        return user;
//    }

    public Event add(Event stock) throws DataIntegrityViolationException {
        return events.save(stock);
    }

}
