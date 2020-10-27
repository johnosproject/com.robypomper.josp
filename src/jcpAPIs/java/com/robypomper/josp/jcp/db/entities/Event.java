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

package com.robypomper.josp.jcp.db.entities;

import com.robypomper.josp.protocol.JOSPEvent;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;


@Entity
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long evnId;

    @Enumerated(EnumType.STRING)                                // Index 1, 2
    private JOSPEvent.SrcType srcType;

    private String srcId;

    @Enumerated(EnumType.STRING)                                // Index 2
    private JOSPEvent.Type evnType;

    private boolean isError;

    private String phase;

    @Lob
    private String evnPayload;

    @Lob
    private String errorPayload;

    /**
     * Get by emitter.
     */
    private Date emittedAt;                                     // Index 3, Ordered


    // Extra profile

    @CreationTimestamp
    private Date registeredAt;


    public static Event fromJOSPEvent(JOSPEvent event) {
        Event e = new Event();
        e.evnId = event.id;
        e.srcType = event.srcType;
        e.srcId = event.srcId;//objId
        e.evnType = event.type;
        e.isError = event.errorPayload != null;
        e.phase = event.phase;
        e.evnPayload = event.payload;
        e.errorPayload = event.errorPayload;
        e.emittedAt = event.emittedAt;
        return e;
    }

}
