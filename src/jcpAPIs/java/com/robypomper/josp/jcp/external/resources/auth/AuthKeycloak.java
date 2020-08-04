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

package com.robypomper.josp.jcp.external.resources.auth;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jcp.apis.paths.APIAuth;
import com.robypomper.josp.jcp.db.entities.Service;
import com.robypomper.josp.jcp.db.entities.ServiceDetails;
import com.robypomper.josp.jcp.db.entities.User;
import com.robypomper.josp.jcp.db.entities.UserProfile;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;


/**
 * Keycloak implementation of {@link AuthResource} interface.
 */
@Component
@RequestScope
public class AuthKeycloak implements AuthResource {

    // Internal vars

    @Autowired
    private JCPClient2 client;


    // User Q&M

    /**
     * {@inheritDoc}
     */
    public User queryUser(String usrId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.RequestException, JCPClient2.ResponseException {
        UserRepresentation kcUser = client.execReq(true, Verb.GET, APIAuth.FULL_PATH_USER + "/" + usrId, UserRepresentation.class, true);

        UserProfile profile = new UserProfile();
        User user = new User();

        profile.setUsrId(kcUser.getId());
        profile.setEmail(kcUser.getEmail());
        profile.setName(kcUser.getFirstName());
        profile.setSurname(kcUser.getLastName());

        user.setUsrId(kcUser.getId());
        user.setUsername(kcUser.getUsername());
        user.setProfile(profile);

        return user;
    }

    /**
     * {@inheritDoc}
     */
    public Service queryService(String srvId) throws JCPClient2.ConnectionException, JCPClient2.RequestException {
        // No KeyCloack api to get client info
        //ClientRepresentation kcClient = client.execReq(true, Verb.GET, APIAuth.FULL_PATH_CLIENTS + "/" + srvId, ClientRepresentation.class, true);
        //System.out.println("WAR: empty service info are generated, because keycloak don't provide client's info");

        ServiceDetails details = new ServiceDetails();
        Service service = new Service();

        details.setSrvId(srvId);
        details.setEmail("");
        details.setWeb("");
        details.setCompany("");

        service.setSrvId(srvId);
        service.setSrvName(srvId.replace("-", " "));
        service.setDetails(details);

        return service;
    }
}
