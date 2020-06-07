package com.robypomper.josp.jod.jcpclient;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jod.JODSettings_002;

public class AbsJCPAPIs {

    protected final JCPClient jcpClient;
    protected final JODSettings_002 locSettings;

    public AbsJCPAPIs(JCPClient jcpClient, JODSettings_002 settings) {
        this.jcpClient = jcpClient;
        this.locSettings = settings;
    }
}
