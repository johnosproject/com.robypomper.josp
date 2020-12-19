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

/**
 * Messaging class to respond JOSP GW O2S/S2O access info to JOD/JSL caller.
 */
public class AccessInfo {

    // Params

    public final String gwAddress;
    public final int gwPort;
    public final byte[] gwCertificate;


    // Constructor

    public AccessInfo(String gwAddress,
                      int gwPort,
                      byte[] gwCertificate) {
        this.gwAddress = gwAddress;
        this.gwPort = gwPort;
        this.gwCertificate = gwCertificate;
    }

}
