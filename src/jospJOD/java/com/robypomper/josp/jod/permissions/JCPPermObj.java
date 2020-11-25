/* *****************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.josp.jod.permissions;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.paths.APIPermissions;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.jcpclient.AbsJCPAPIs;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;

import java.util.List;


/**
 * Support class for API Perm access to the object's permissions.
 */
public class JCPPermObj extends AbsJCPAPIs {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     * @param settings  the JOD settings.
     */
    public JCPPermObj(JCPClient_Object jcpClient, JODSettings_002 settings) {
        super(jcpClient, settings);
    }


    // Generator methods

    /**
     * Request to the JCP a generic object's permission list.
     * <p>
     * The list generated can be different depending on generation strategy request.
     *
     * @return a valid permission list.
     */
    public List<JOSPPerm> generatePermissionsFromJCP() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        JOSPPerm.GenerateStrategy strategy = locSettings.getPermissionsGenerationStrategy();
        String permsStr = jcpClient.execReq(Verb.GET, APIPermissions.FULL_PATH_OBJGENERATE + "/" + strategy, String.class, isSecure());
        try {
            return JOSPPerm.listFromString(permsStr);

        } catch (JOSPProtocol.ParsingException e) {
            throw new JCPClient2.RequestException(String.format("Can't parse JOSPPerm list from returned string '%s'", permsStr));
        }
    }

}
