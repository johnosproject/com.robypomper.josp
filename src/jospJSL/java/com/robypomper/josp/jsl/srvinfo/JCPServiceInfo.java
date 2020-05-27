package com.robypomper.josp.jsl.srvinfo;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.params.srvs.SrvName;
import com.robypomper.josp.jcp.apis.paths.APISrvs;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Support class for API Srv access to the service's info.
 */
public class JCPServiceInfo {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JCPClient_Service jcpClient;


    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public JCPServiceInfo(JCPClient_Service jcpClient) {
        this.jcpClient = jcpClient;
    }


    // Getter methods

    public String getId() throws JCPClient.ConnectionException, JCPClient.RequestException {
        return jcpClient.execGetReq(APISrvs.URL_PATH_USERNAME, SrvName.class, true).getSrvId();
    }

    public String getName() throws JCPClient.ConnectionException, JCPClient.RequestException {
        return jcpClient.execGetReq(APISrvs.URL_PATH_USERNAME, SrvName.class, true).getSrvName();
    }

}