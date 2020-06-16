package com.robypomper.josp.jcp.external.resources.auth;

import com.robypomper.josp.core.jcpclient.JCPClient;
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
    private JCPClient client;


    // User Q&M

    /**
     * {@inheritDoc}
     */
    public User queryUser(String usrId) throws JCPClient.ConnectionException, JCPClient.RequestException {
        UserRepresentation kcUser = client.execGetReq(APIAuth.URL_PATH_USER + "/" + usrId, UserRepresentation.class, true);

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
    public Service queryService(String srvId) throws JCPClient.ConnectionException, JCPClient.RequestException {
        // No KeyCloack api to get client info
        //ClientRepresentation kcClient = client.execGetReq(APIAuth.URL_PATH_CLIENTS + "/" + srvId, ClientRepresentation.class, true);

        System.out.println("WAR: empty service info are generated, because keycloak don't provide client's info");

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
