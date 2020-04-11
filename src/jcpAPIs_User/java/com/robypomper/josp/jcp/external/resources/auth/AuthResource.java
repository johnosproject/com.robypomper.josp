package com.robypomper.josp.jcp.external.resources.auth;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.db.entities.Service;
import com.robypomper.josp.jcp.db.entities.ServiceDetails;
import com.robypomper.josp.jcp.db.entities.User;
import com.robypomper.josp.jcp.db.entities.UserProfile;

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
     * instance including {@link UserProfile}
     * field.
     *
     * @param usrId the user id.
     * @return instance of {@link Service} object populated with auth's user info.
     */
    User queryUser(String usrId) throws JCPClient.ConnectionException, JCPClient.RequestException;

    /**
     * Request <code>srvId</code> service to auth server and cast to {@link Service}
     * instance including {@link ServiceDetails} field.
     *
     * @param srvId the service id.
     * @return instance of {@link Service} object populated with auth's service info.
     */
    Service queryService(String srvId) throws JCPClient.ConnectionException, JCPClient.RequestException;

}
