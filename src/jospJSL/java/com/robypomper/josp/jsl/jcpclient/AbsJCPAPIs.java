package com.robypomper.josp.jsl.jcpclient;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jsl.JSLSettings_002;

public class AbsJCPAPIs {

    protected final JCPClient jcpClient;
    protected final JSLSettings_002 locSettings;

    public AbsJCPAPIs(JCPClient jcpClient, JSLSettings_002 settings) {
        this.jcpClient = jcpClient;
        this.locSettings = settings;
    }
}
