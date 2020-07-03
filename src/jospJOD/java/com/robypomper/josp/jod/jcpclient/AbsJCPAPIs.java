package com.robypomper.josp.jod.jcpclient;

import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jod.JODSettings_002;

public class AbsJCPAPIs {

    protected final JCPClient2 jcpClient;
    protected final JODSettings_002 locSettings;

    public AbsJCPAPIs(JCPClient2 jcpClient, JODSettings_002 settings) {
        this.jcpClient = jcpClient;
        this.locSettings = settings;
    }
}
