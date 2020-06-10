package com.robypomper.josp.jsl.srvinfo;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.params.srvs.SrvName;
import com.robypomper.josp.jcp.apis.paths.APISrvs;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.jcpclient.AbsJCPAPIs;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;


/**
 * Support class for API Srv access to the service's info.
 */
public class JCPServiceInfo extends AbsJCPAPIs {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public JCPServiceInfo(JCPClient_Service jcpClient, JSLSettings_002 settings) {
        super(jcpClient, settings);
        registerToJCP();
    }

    public void registerToJCP() {
        try {
            jcpClient.execGetReq(APISrvs.URL_PATH_REGISTER, SrvName.class, true);
        } catch (JCPClient.RequestException | JCPClient.ConnectionException ignore) {
            jcpClient.addConnectListener(new JCPClient.ConnectListener() {
                @Override
                public void onConnected(JCPClient jcpClient) {
                    registerToJCP();
                }
            });
        }
    }

}
