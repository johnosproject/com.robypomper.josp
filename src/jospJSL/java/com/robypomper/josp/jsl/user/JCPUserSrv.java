package com.robypomper.josp.jsl.user;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.params.usrs.UsrName;
import com.robypomper.josp.jcp.apis.paths.APIUsrs;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;


/**
 * Support class for API Usr access to the service's user.
 */
public class JCPUserSrv {

    // Internal vars

    private final JCPClient_Service jcpClient;


    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public JCPUserSrv(JCPClient_Service jcpClient) {
        this.jcpClient = jcpClient;
    }


    // Getters methods

    public String getUserId() throws JCPClient.ConnectionException, JCPClient.RequestException {
        return jcpClient.execGetReq(APIUsrs.URL_PATH_USERNAME, UsrName.class, true).usrId;
    }

    public String getUsername() throws JCPClient.ConnectionException, JCPClient.RequestException {
        return jcpClient.execGetReq(APIUsrs.URL_PATH_USERNAME, UsrName.class, true).username;
    }

}
