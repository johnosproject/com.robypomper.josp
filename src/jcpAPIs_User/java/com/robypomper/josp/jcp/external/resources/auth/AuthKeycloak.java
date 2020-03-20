package com.robypomper.josp.jcp.external.resources.auth;

import com.robypomper.josp.core.jcpclient.JCPClient;
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

    // Class constants

    private static final String API_BASE_URL = "https://localhost:8998/auth/admin/realms/jcp";


    // Internal vars

    @Autowired
    private JCPClient client;


    // User Q&M

    /**
     * {@inheritDoc}
     */
    public User queryUser(String usrId) throws JCPClient.ConnectionException, JCPClient.RequestException {
        UserRepresentation kcUser = client.execGetReq(API_BASE_URL + "/users/" + usrId, UserRepresentation.class, true);

        UserProfile profile = new UserProfile();
        User user = new User();

        profile.setEmail(kcUser.getEmail());
        profile.setName(kcUser.getFirstName());
        profile.setSurname(kcUser.getLastName());

        user.setUsrId(kcUser.getId());
        user.setUsername(kcUser.getUsername());
        user.setProfile(profile);

        return user;
    }

}
