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

import com.robypomper.josp.protocol.JOSPStatusHistory;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;


@Entity
@Data
public class ObjectStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long shId;

    private String objId;

    private String compPath;

    private String compType;

    /**
     * Get by emitter.
     */
    private Date updatedAt;

    @Lob
    private String payload;


    // Extra profile

    @CreationTimestamp
    private Date registeredAt;


    public static ObjectStatusHistory fromJOSPStatusHistory(String objId, JOSPStatusHistory status) {
        ObjectStatusHistory s = new ObjectStatusHistory();
        s.shId = status.id;
        s.objId = objId;
        s.compPath = status.compPath;
        s.compType = status.compType;
        s.updatedAt = status.updatedAt;
        s.payload = status.payload;
        return s;
    }

}
