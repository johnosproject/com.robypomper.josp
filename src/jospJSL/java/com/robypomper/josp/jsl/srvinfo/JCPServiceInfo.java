package com.robypomper.josp.jsl.srvinfo;

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
    }

}
