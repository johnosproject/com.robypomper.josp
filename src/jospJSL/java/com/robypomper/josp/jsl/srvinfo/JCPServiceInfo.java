package com.robypomper.josp.jsl.srvinfo;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.core.jcpclient.JCPClient2;
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
            jcpClient.execReq(Verb.GET, APISrvs.FULL_PATH_REGISTER, SrvName.class, true);

        } catch (JCPClient2.RequestException | JCPClient2.ConnectionException | JCPClient2.ResponseException ignore) {
            jcpClient.addConnectListener(new JCPClient2.ConnectListener() {
                @Override
                public void onConnected(JCPClient2 jcpClient) {
                    registerToJCP();
                }

                @Override
                public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {}
            });
        }
    }

}
