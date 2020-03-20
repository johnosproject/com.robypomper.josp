package com.robypomper.josp.jcp.external.resources.auth;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.db.entities.User;

/**
 * Interface for authentication resource.
 * <p>
 * This interface implementations can be used to access auth's server resources
 * and methods.
 */
public interface AuthResource {

    // User Q&M

    /**
     * Request <code>usrId</code> user to auth server and cast to {@link User}
     * instance including {@link com.robypomper.josp.jcp.db.entities.UserProfile}
     * field.
     *
     * @param usrId the user id.
     * @return instance of {@link User} object populated with auth's user info.
     */
    User queryUser(String usrId) throws JCPClient.ConnectionException, JCPClient.RequestException;

}
