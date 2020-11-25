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

package com.robypomper.josp.params.jospgws;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Messaging class to transmit JSL service access info to required JOSP GW S2O.
 */
public class S2OAccessRequest {

    // Params

    public final String instanceId;
    public final byte[] srvCertificate;


    // Constructor

    @JsonCreator
    public S2OAccessRequest(@JsonProperty("instanceId") String instanceId,
                            @JsonProperty("srvCertificate") byte[] srvCertificate) {
        this.instanceId = instanceId;
        this.srvCertificate = srvCertificate;
    }
}
