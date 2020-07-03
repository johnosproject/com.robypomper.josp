package com.robypomper.josp.jsl.jcpclient;

import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jsl.JSLSettings_002;

public class AbsJCPAPIs {

    protected final JCPClient2 jcpClient;
    protected final JSLSettings_002 locSettings;

    public AbsJCPAPIs(JCPClient2 jcpClient, JSLSettings_002 settings) {
        this.jcpClient = jcpClient;
        this.locSettings = settings;
    }
}
